package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Reserve;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class ReserveController {
    
    @Autowired
    private EntityManager entityManager;

    // Obtener clave de google maps desde application.properties
    @Value("${google.maps.key}")
    private String googleMapsKey;

    @GetMapping("/reserve/{id}")
    public String showReservePage(@PathVariable long id, Model model, HttpSession session) {
        // Buscar el evento en la base de datos usando el id que viene en la url
        Event event = entityManager.find(Event.class, id);

        // Si el id no existe, se redirige a eventos
        if (event == null) {
            return "redirect:/event";
        }

        // Calcular plazas libres
        int reservedSpots = event.getAttendees() != null ? event.getAttendees().size() : 0;
        int availableSpots = event.getCapacity() - reservedSpots;

        // Comprobar si el usuario actual ya esta apuntado
        boolean isAlreadyAttending = false;
        User u = (User) session.getAttribute("u");
        if (u != null && event.getAttendees() != null) {
            isAlreadyAttending = event.getAttendees().stream()
                    .anyMatch(r -> r.getAttendee().getId() == u.getId());
        }


        model.addAttribute("event", event);
        model.addAttribute("availableSpots", availableSpots);
        model.addAttribute("reservedSpots", reservedSpots);
        model.addAttribute("isAlreadyAttending", isAlreadyAttending);
        // Pasar la API key al HTML
        model.addAttribute("googleMapsKey", googleMapsKey);
        return "reserve";
    }

    @Transactional
    @PostMapping("/reserve/confirm")
    public String confirmReserve(@RequestParam long eventId, HttpSession session) {
        // Obtener el usuario logueado de la sesion
        //User u = (User) session.getAttribute("u");
        //u = entityManager.find(User.class, u.getId());

        long userId = ((User) session.getAttribute("u")).getId();
        User u = entityManager.find(User.class, userId);
        // Buscar el evento que queremos reservar
        Event event = entityManager.find(Event.class, eventId);

        // Si existen ambos, se crea la reserva en la base de datos
        if(event != null && u != null) {

            // Validacion del servidor para comprobar si hay plazas disponibles
            int reservedSpots = event.getAttendees() != null ? event.getAttendees().size() : 0;
            if (reservedSpots >= event.getCapacity()) {
                // Si el evento esta lleno, se delvuelve al usuario a la pagina de eventos con un error
                return "redirect:/event?error=full";
            }

            // Validacion servidor para comprobar si el usuario esta ya apuntado
            boolean isAlreadyAttending = event.getAttendees().stream()
                    .anyMatch(r -> r.getAttendee().getId() == u.getId());

            if (isAlreadyAttending ) {
                // Si ya esta apuntado, no se vuelve a apuntar
                return "redirect:/account?tab=events";
            }

            // Si pasa las validaciones de seguridad, se lleva a cabo la reserva
            Reserve reserve = new Reserve();
            reserve.setAttendee(u);
            reserve.setEvent(event);

            // Comunicar al evento que agregue la reserva a su lista
            event.getAttendees().add(reserve);

            entityManager.persist(reserve);
            entityManager.flush();
        }

        // Redirigir al perfil del usuario a la pestaña de mis eventos
        return "redirect:/account?tab=events";
    }
}

