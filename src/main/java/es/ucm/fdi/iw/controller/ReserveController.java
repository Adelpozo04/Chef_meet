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

@Controller
public class ReserveController {
    
    @Autowired
    private EntityManager entityManager;

    @GetMapping("/reserve/{id}")
    public String showReservePage(@PathVariable long id, Model model) {
        // Buscar el evento en la base de datos usando el id que viene en la url
        Event event = entityManager.find(Event.class, id);

        // Si el id no existe, se redirige a eventos
        if (event == null) {
            return "redirect:/event";
        }

        // Calcular plazas libres
        int reservedSpots = event.getAttendees() != null ? event.getAttendees().size() : 0;
        int availableSpots = event.getCapacity() - reservedSpots;

        model.addAttribute("event", event);
        model.addAttribute("availableSpots", availableSpots);

        return "reserve";
    }

    @Transactional
    @PostMapping("/reserve/confirm")
    public String confirmReserve(@RequestParam long eventId, HttpSession session) {
        // Obtener el usuario logueado de la sesion
        User u = (User) session.getAttribute("u");
        u = entityManager.find(User.class, u.getId());

        // Buscar el evento que queremos reservar
        Event event = entityManager.find(Event.class, eventId);

        // Si existen ambos, se crea la reserva en la base de datos
        if(event != null && u != null) {
            Reserve reserve = new Reserve();
            reserve.setAttendee(u);
            reserve.setEvent(event);

            entityManager.persist(reserve);
            entityManager.flush();
        }

        // Redirigir al perfil del usuario a la pestaña de mis eventos
        return "redirect:/account?tab=events";
    }
}

