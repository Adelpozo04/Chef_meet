package es.ucm.fdi.iw.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/event")
public class EventController {
    
    private static final Logger log = LogManager.getLogger(EventController.class);

    @Autowired
    private EntityManager entityManager;

    // Cargar eventos
    @GetMapping({"", "/"})
    public String showEvents(Model model) {
        // Se piden todos los eventos a la base de datos
        List<Event> events = entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList();
        model.addAttribute("events", events);
        return "event"; // Redirige a event.html
    }
    
    // Mostrar formulario? crear evento
    @GetMapping("/create") 
    public String createEvent(Model model) {
        model.addAttribute("event", new Event());
        return "event/create";
    }

    // Guardar el evento en la base de datos
    @Transactional
    @PostMapping("/create")
    public String createEvent(
        @ModelAttribute Event event,
        HttpSession session) {
            
            // Obtener el usuario logueado de la sesion
            User organizer = (User) session.getAttribute("u");
            organizer = entityManager.find(User.class, organizer.getId());

            // Asignarlo como organizador
            event.setOrganizer(organizer);

            // Guardar en la base de datos
            entityManager.persist(event);
            entityManager.flush();

            log.info("Nuevo evento creado por {}: {}", organizer.getUsername(), event.getTitle());

            return "redirect:/event";

    }

    // Borrar el evento en la base de datos
    @Transactional
    @PostMapping("/{id}/delete") 
    public String deleteEvent(@PathVariable long id) {
        // Buscar el evento en la base de datos pot su id
        Event event = entityManager.find(Event.class, id);

        if(event != null) {
            // Eliminar de la base de datos
            entityManager.remove(event);
            log.info("El administrador ha borrado el evento: {}", event.getTitle());
        }

        return "redirect:/event";
    } 
}
