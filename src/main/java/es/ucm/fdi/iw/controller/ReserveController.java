package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReserveController {
    
    @Autowired
    private EntityManager entityManager;

    @GetMapping("/reserve")
    public String showReservePage(@RequestParam long id, Model model) {
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
}

