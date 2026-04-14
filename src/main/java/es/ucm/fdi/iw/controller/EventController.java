package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Reservation;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.controller.UserController.NoEsTuPerfilException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/event")
public class EventController {
    
    private static final Logger log = LogManager.getLogger(EventController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LocalData localData;

    // Excepcion para denegar una accion a usuarios no autorizados
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "No tienes permisos para realizar esta acción")
        public static class DontHavePermissionException extends RuntimeException {
    } 
    // Obtener clave de google maps desde application.properties
    @Value("${google.maps.key:}")
    private String googleMapsKey;

    // Cargar eventos
    @GetMapping({"", "/"})
    public String showEvents(Model model, HttpSession session, @RequestParam(required = false) String error) {
        // Si la URL trae "?error=full", se le envia la señal al HTML
        if("full".equals(error)) {
            model.addAttribute("errorFull", true);
        }

        // Se piden todos los eventos a la base de datos
        List<Event> allEvents = entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList();
        
        User sessionUser = (User) session.getAttribute("u");

        // Filtrar eventos segun privacidad
        List<Event> visibleEvents = new java.util.ArrayList<>();
        for(Event e: allEvents) {
            if(!e.isPrivate()) {
                visibleEvents.add(e); // Eventos publicos, siempre visibles
            } 
            else if(sessionUser != null) {
                User u = entityManager.find(User.class, sessionUser.getId());

                // Es miembro de la comunidad?
                boolean isMember = e.getCommunity() != null && e.getCommunity().getMembers().contains(u);
                // Organizador del evento?
                boolean isOrganizer = e.getOrganizer() != null && e.getOrganizer().getId() == u.getId();
                // Tiene una reserva para el evento?
                boolean isAttendee = e.getAttendees() != null && e.getAttendees().stream().anyMatch(r -> r.getAttendee().getId() == u.getId());
                // Eventos privados, solo visibles si el usuario pertenece a la comunidad
                
                // Si cumple cualquiera de las tres condiciones, el evento es visible
                if(isMember || isOrganizer || isAttendee) {
                    visibleEvents.add(e);
                }
            }
        }

        // Filtrar eventos en mis eventos y otros eventos
        if(sessionUser != null) {
            // Usuario logueado: separar en dos listas
            long uid = sessionUser.getId();

            // Mis eventos: Soy el organizador estoy en la lista de asistentes
            List<Event> myEvents = visibleEvents.stream()
                .filter(e -> (e.getOrganizer() != null && e.getOrganizer().getId() == uid) ||
                             (e.getAttendees() != null && e.getAttendees().stream().anyMatch(r -> r.getAttendee().getId() == uid)))      
                .toList();
                
            // Otros eventos
            List<Event> otherEvents = visibleEvents.stream()
                .filter(e -> !myEvents.contains(e))
                .toList();
            
            model.addAttribute("myEvents", myEvents);
            model.addAttribute("otherEvents", otherEvents);

        }
        else {
            // Usuario anonimo: todos los eventos van a otros eventos
            model.addAttribute("otherEvents", visibleEvents);
        }

        // Todos los eventos
        model.addAttribute("events", visibleEvents);

        // Pasar la API key al HTML
        model.addAttribute("googleMapsKey", googleMapsKey);
        
        return "event"; // Redirige a event.html
    }
    
    // Mostrar formulario crear evento
    @GetMapping("/create") 
    public String createEvent(Model model, HttpSession session, @RequestParam(required = false) Long communityId) {
        User sessionUser = (User) session.getAttribute("u");

        // Buscar a que comunidades pertenece el usuario
        List<es.ucm.fdi.iw.model.Community> myCommunities = entityManager.createQuery(
            "SELECT c FROM Community c JOIN c.members m WHERE m.id = :uid", es.ucm.fdi.iw.model.Community.class)
            .setParameter("uid", sessionUser.getId()).getResultList();

        model.addAttribute("event", new Event());
        model.addAttribute("myCommunities", myCommunities);
        model.addAttribute("preselectedCommunityId", communityId); // Para poder preseleccionar en el menu
        return "event/create";
    }

    // Guardar el evento en la base de datos
    @Transactional
    @PostMapping("/create")
    public String createEvent(
        @Valid @ModelAttribute Event event,
        BindingResult result,
        @RequestParam(required = false) Long communityId,
        @RequestParam("photo") MultipartFile photo,
        Model model, // para poder mandar mensajes de error al HTML
        HttpSession session) {
            
            // Comprobar si hay errores en los datos del evento
            if(result.hasErrors()) {
                log.warn("Errores de validacion en el evento: {}", result.getAllErrors());
                return "event/create";
            }
            // Validar imagen
            if(!photo.isEmpty()) {
                // Verificar el tipo de archivo
                String contentType = photo.getContentType();

                if(contentType == null ||  (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))){
                    // Mandar señal de error al HTML y recargar formulario
                    model.addAttribute("errorImage", true);
                    return "event/create";
                }

                // Restriccion de tamanyo
                long maxBytes = 5 * 1024 * 1024; // 5 MB
                if (photo.getSize() > maxBytes) {
                    model.addAttribute("errorSize", true);
                    return "event/create";
                }
            }
            // Obtener el usuario logueado de la sesion
            User organizer = (User) session.getAttribute("u");
            organizer = entityManager.find(User.class, organizer.getId());

            // Asignarlo como organizador
            event.setOrganizer(organizer);

            // Asignar el evento a una comunidad si se debe
            if (communityId != null) {
                es.ucm.fdi.iw.model.Community community = entityManager.find(es.ucm.fdi.iw.model.Community.class, communityId);
                event.setCommunity(community);
            }
            else {
                event.setPrivate(false); // Si no esta asociado a una comunidad, forzar a que el evento sea publico
            }

            // Guardar en la base de datos
            entityManager.persist(event);
            entityManager.flush();

            // Autoreserva para el organizador
            Reservation autoReservation = new Reservation();
            autoReservation.setAttendee(organizer);
            autoReservation.setEvent(event);

            // Si la lista de asistentes esta vacia, se inicia y se mete la reserva
            if (event.getAttendees() == null) {
                event.setAttendees(new java.util.ArrayList<>());
            }
            event.getAttendees().add(autoReservation);

            // Guardar la reserva en la base de datos
            entityManager.persist(autoReservation);
            entityManager.flush();

            // Guardar la imagen si el usuario ha subido alguna
            if(!photo.isEmpty()) {
                try {
                    // Obtener el archivo donde se guarda la imagen dentro de la carpeta 'events' en 'iwdata'
                    File f = localData.getFile("events", String.valueOf(event.getId()));

                    // Asegurar que la carpeta 'events' existe antes de guardar, si no, se crea
                    f.getParentFile().mkdirs();

                    try(BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
                        stream.write(photo.getBytes());
                    }

                    // Actualizar la imagen del evento con la ruta 
                    event.setImagePath("/event/" + event.getId() + "/pic");

                    // Forzar a la base de datos a guardar esta actualizacion
                    entityManager.merge(event);
                    entityManager.flush();
                    
                    // Al usar @Transactional, JPA guarda este cambio final en la base de datos automaticamente
                } catch (IOException e) {
                    log.error("Error al guardar la imagen del evento", e);
                }
            }

            log.info("Nuevo evento creado por {}: {}", organizer.getUsername(), event.getTitle());

            // Suscribir al creador al canal de su nuevo evento
            String topics = (String) session.getAttribute("topics");
            if(topics != null && !topics.isEmpty()) {
                session.setAttribute("topics", topics + ",event-" + event.getId());
            }
            else {
                session.setAttribute("topics", "event-" + event.getId());
            }
            return "redirect:/event";

    }

    // Borrar el evento en la base de datos
    @Transactional
    @PostMapping("/{id}/delete") 
    public String deleteEvent(@PathVariable long id, HttpSession session) {
        // Obtener el evento en la base de datos por su id
        Event event = entityManager.find(Event.class, id);

        if(event == null) {
            return "redirect:/event";
        }

        // Obtener el usuario que quiere realizar la accion
        User requester = (User) session.getAttribute("u");
        if(requester == null) {
            throw new DontHavePermissionException(); // Si no esta logueado
        }

        // Comprobar permisos, ¿es admin o el creador del evento?
        boolean isAdmin = requester.hasRole(User.Role.ADMIN);
        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().getId() == requester.getId();

        if(!isAdmin && !isOrganizer)
        {
            log.warn("El usuario {} ha intentado eliminar el evento {} sin tener permisos.", requester.getUsername(), event.getTitle());
            throw new DontHavePermissionException();
        }

        // Eliminar de la base de datos si tiene permisos
        entityManager.remove(event);
        log.info("El usuario {} ha borrado el evento: {}", requester.getUsername(), event.getTitle());
        

        return "redirect:/event";
    } 

    // Endpoint de la API REST que devuelve todos los eventos registrados en la base de datos.
    // Implementado como un punto de acceso para peticiones asincronas AJAX o fetch desde el frontend, 
    // para cargar los marcadores de ubicaciones en el mapa

    // Cuando el navegador hace una peticion web a esta ruta, entra aqui
    // Respuesta en formato JSON
    @GetMapping(path = "/api/all", produces = "application/json")
    @ResponseBody
    public List<Event.Transfer> getEventsForMap() {
        // Consulta a la base de datos para obtener todos los eventos y guardarlos en una lista de objetos Event.Java
        List<Event> events = entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList();

        // Convertir los eventos al formato JSON seguro Event.Transfer
        return events.stream().map(Event::toTransfer).collect(Collectors.toList());
    }

    // Endpoint para proporcionar la imagen del evento desde la carpeta externa iwdata
    @GetMapping("/{id}/pic")
    @ResponseBody
    public void getEventPhoto(@PathVariable long id, HttpServletResponse response) throws IOException {
        File f = localData.getFile("events", String.valueOf(id));
        if (f.exists() && f.canRead()) {
            // Si hay foto subida por el usuario, se proporciona al navegador
            response.setContentType("image/jpeg");
            Files.copy(f.toPath(), response.getOutputStream());
        } else {
            // Si no existe, se envia la imagen de por defecto
            response.sendRedirect("/img/events/default.jpg");
        }

    }
}
