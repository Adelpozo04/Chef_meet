package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Reservation;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class ReservationController {
    
    private static final Logger log = LogManager.getLogger(ReservationController.class);

    @Autowired
    private EntityManager entityManager;

    // Para los mensajes
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Obtener clave de google maps desde application.properties
    @Value("${google.maps.key:}")
    private String googleMapsKey;

    @GetMapping("/reservation/{id}")
    public String showReservationPage(@PathVariable long id, Model model, HttpSession session) {
        // Buscar el evento en la base de datos usando el id que viene en la url
        Event event = entityManager.find(Event.class, id);

        // Si el id no existe, se redirige a eventos
        if (event == null) {
            return "redirect:/event";
        }

        User sessionUser = (User) session.getAttribute("u");

        if (!canAccessEvent(event, sessionUser)) {
            return "redirect:/event";
        }

        // Calcular plazas libres
        int reservedSpots = event.getAttendees() != null ? event.getAttendees().size() : 0;
        int availableSpots = event.getCapacity() - reservedSpots;

        // Comprobar si el usuario actual ya esta apuntado
        boolean isAlreadyAttending = false;
        boolean canEditEvent = false;

        User u = (User) session.getAttribute("u");
        if (u != null && event.getAttendees() != null) {
            isAlreadyAttending = event.getAttendees().stream()
                    .anyMatch(r -> r.getAttendee().getId() == u.getId());
        }

        // Solo puede editar una reserva el admin o el organizador del evento
        if (u != null) {
            User currentUser = entityManager.find(User.class, u.getId());

            boolean isAdmin = currentUser.hasRole(User.Role.ADMIN);
            boolean isOrganizer = event.getOrganizer() != null 
                    && event.getOrganizer().getId() == currentUser.getId();

            canEditEvent = isAdmin || isOrganizer;
        }

        // Enviar los datos a la vista
        model.addAttribute("event", event);
        model.addAttribute("availableSpots", availableSpots);
        model.addAttribute("reservedSpots", reservedSpots);
        model.addAttribute("isAlreadyAttending", isAlreadyAttending);
        model.addAttribute("canEditEvent", canEditEvent);
        // Pasar la API key al HTML
        model.addAttribute("googleMapsKey", googleMapsKey);
        return "reservation";
    }

    @Transactional
    @PostMapping("/reservation/confirm")
    public String confirmReservation(@RequestParam long eventId, HttpSession session) {

        //User u = (User) session.getAttribute("u");
        //u = entityManager.find(User.class, u.getId());

        // Obtener el usuario logueado de la sesion
        long userId = ((User) session.getAttribute("u")).getId();
        User u = entityManager.find(User.class, userId);
        // Buscar el evento que queremos reservar
        Event event = entityManager.find(Event.class, eventId);

        if (event == null || u == null) {
            return "redirect:/event";
        }
    
        if (!canAccessEvent(event, u)) {
            return "redirect:/event";
        }

        // Si existen ambos, se crea la reserva en la base de datos
        if(event != null && u != null) {

            // Comprobar si hay plazas disponibles
            int reservedSpots = event.getAttendees() != null ? event.getAttendees().size() : 0;
            if (reservedSpots >= event.getCapacity()) {
                // Si el evento esta lleno, se delvuelve al usuario a la pagina de eventos con un error
                return "redirect:/event?error=full";
            }

            // Comprobar si el usuario esta ya apuntado
            boolean isAlreadyAttending = event.getAttendees().stream()
                    .anyMatch(r -> r.getAttendee().getId() == u.getId());

            if (isAlreadyAttending ) {
                // Si ya esta apuntado, no se vuelve a apuntar
                return "redirect:/account?tab=events";
            }

            // Si se pasan las validaciones de seguridad, se lleva a cabo la reserva
            Reservation reservation = new Reservation();
            reservation.setAttendee(u);
            reservation.setEvent(event);

            // Comunicar al evento que agregue la reserva a su lista
            event.getAttendees().add(reservation);

            entityManager.persist(reservation);
            entityManager.flush();

            // Enviar notificacion por websocket al canal del evento
            try {
                // Construir el JSON del mensaje usando Jackson
                ObjectMapper mapper = new ObjectMapper();
                // Nodo principal de JSON
                ObjectNode rootNode = mapper.createObjectNode();
                // Tipo de mensaje
                rootNode.put("type", "EVENT_JOIN");
                // Texto de la notificacion
                rootNode.put("text", u.getUsername() + " se ha unido al evento " + event.getTitle());
                // Convertir JSON a texto
                String json = mapper.writeValueAsString(rootNode);

                // Publicar en el canal del evento
                messagingTemplate.convertAndSend("/topic/event-" + event.getId(), json);
                
            } catch (Exception e) {

               log.error("Error al publicar en el canal del evento", e );
            }

            // Suscribir al usuario al canal de este evento
            // Obtener de la sesion la lista de canales a los que esta suscrito el usuario
            String topics = (String) session.getAttribute("topics");
            // Si ya habia canales guardados en sesion, añadir el nuevo canal del evento
            if(topics != null && !topics.isEmpty()) {
                session.setAttribute("topics", topics + ",event-" + event.getId());
            }
            // Si no, crear la lista con el canal de este evento
            else {
                session.setAttribute("topics", "event-" + event.getId());
            }
            log.info("Usuario {} suscrito a event-{}", u.getUsername(), event.getId());
        }

        // Redirigir al perfil del usuario a la pestaña de mis eventos
        return "redirect:/account?tab=events";
    }

    // Comprueba si un usuario puede acceder a un evento
    // Los eventos publicos son accesibles para cualquier usuario
    // Los eventos privados solo son accesibles si el usuario es admin,
    // organizador, asistente, miembro de la comunidad o creador de la comunidad.
    private boolean canAccessEvent(Event event, User user) {

        // Si el evento no existe, no se puede acceder
        if (event == null) {
            return false;
        }

        // Los eventos públicos se pueden ver siempre
        if (!event.isPrivate()) {
            return true;
        }

        // Si es privado y no hay usuario logueado, no se puede acceder
        if (user == null) {
            return false;
        }

        // Recargar usuario desde base de datos
        User currentUser = entityManager.find(User.class, user.getId());

        // Si el usuario no existe, no puede acceder
        if (currentUser == null) {
            return false;
        }

        // El admin puede acceder
        boolean isAdmin = currentUser.hasRole(User.Role.ADMIN);

        // El organizador del evento puede acceder
        boolean isOrganizer = event.getOrganizer() != null
                && event.getOrganizer().getId() == currentUser.getId();

        // Si ya tiene reserva, puede acceder
        boolean isAttendee = event.getAttendees() != null
                && event.getAttendees().stream()
                    .anyMatch(r -> r.getAttendee().getId() == currentUser.getId());

        // Si el evento pertenece a una comunidad, comprobar permisos sobre esa comunidad
        boolean isCommunityMember = event.getCommunity() != null
                && event.getCommunity().getMembers().stream()
                    .anyMatch(member -> member.getId() == currentUser.getId());

        // También permitimos al creador de la comunidad
        boolean isCommunityOwner = event.getCommunity() != null
                && event.getCommunity().getOwner() != null
                && event.getCommunity().getOwner().getId() == currentUser.getId();

        return isAdmin || isOrganizer || isAttendee || isCommunityMember || isCommunityOwner;
    }
}

