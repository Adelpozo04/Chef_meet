package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Non-authenticated requests only.
 */
@Controller
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

    @GetMapping("/recipe")
    public String recipe(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "recipe";
    }

    @GetMapping("/communities")
    public String communities(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "communities";
    }

    @GetMapping("/event")
    public String event(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "event";
    }

    @GetMapping("/account")
    public String account(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "account";
    }

    @GetMapping("/authors")
    public String autores(Model model) {
        return "authors";
    }

    @GetMapping("/reserve")
    public String reserve(@RequestParam(name="nameString", required=false, defaultValue = "Evento") String nameString, Model model) {
        String image;

        // logica sencilla para asignar la imagen segun el nombre del evento recibido
        switch (nameString.toLowerCase()) {
            case "paella":
                image = "/img/events/ev_espana.jpg";
                nameString = "Evento Paella";
                break;
            case "pizza":
                image = "/img/events/ev_italia.jpg";
                nameString = "Evento Pizza";
                break;
            case "sushi":
                image = "/img/events/ev_japon.jpg";
                nameString = "Evento Sushi";
                break;
            default:
                image = "/img/events/ev_espana.jpg";
                nameString = "Evento";
                break;
        }
        model.addAttribute("title", nameString);
        model.addAttribute("rootImage", image);
        return "reserve";
    }
}
