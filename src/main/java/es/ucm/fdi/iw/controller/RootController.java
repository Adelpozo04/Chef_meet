package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Event;

/**
 * Non-authenticated requests only.
 */
@Controller
@ControllerAdvice
// Al separar la logica en varios controladores no se comparte el metodo ModelAttribute populateModel
// que solo se ejecutaba en este script.  
// Usando ControllerAdvice se convierte en un metodo global para que Spring boot lo inyect
// automaticamente en todas las vistas y controladores
public class RootController {

    private static final Logger log = LogManager.getLogger(RootController.class);

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws", "topics" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "login";
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    /*
    @GetMapping("/recipe")
    public String recipe(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "recipe";
    }
    */  

    /* 
    @GetMapping("/event")
    public String event(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "event";
    }*/

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/account")
    public String account(Model model, HttpSession session, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);

        // Obtener el usuario de la sesion
        User u = (User) session.getAttribute("u");
        if(u != null) {
            // Consulta a la base de datos para obtener las reservas y eventos del usuario, ordenados alfabeticamente
            // En la tabla de reservas busca todas las que pertenezcan al usuario con este ID y obtiene el evento, devolviendo una lista de objetos Event
            List<Event> myEvents = entityManager.createQuery(
                "SELECT r.event FROM Reservation r WHERE r.attendee.id = :userId ORDER BY r.event.title ASC", Event.class)
                .setParameter("userId", u.getId())
                .getResultList();
            
                // Pasar la lista ordenada al HTML
                model.addAttribute("myEvents", myEvents);
        }
        return "account";
    }

    @GetMapping("/authors")
    public String autores(Model model) {
        return "authors";
    }
}
