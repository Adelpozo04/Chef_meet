package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/communities")
public class CommunityController {

    private static final Logger log = LogManager.getLogger(CommunityController.class);

    @Autowired
    private EntityManager entityManager;

    @GetMapping
    public String showCommunities(Model model, HttpSession session) {

        User client = (User) session.getAttribute("u");
        if (client == null) {
            log.info("Usuario sin iniciar sesion accede a Communities");
        }
        else {
            log.info("Usuario con nombre {} accede a Communities", client.getUsername());
        }

        return "communities";
    }

    @GetMapping("/create")
    public String createCommunity(Model model) {
        model.addAttribute("community", new Community());
        model.addAttribute("createError", false);
        return "communities/create";
    }

    @Transactional
    @PostMapping("/create")
    public String createCommunity(
            @ModelAttribute Community community,
            @ModelAttribute User edited,
            Model model,
            HttpSession session) {

        if (community.getTitle().isBlank() || community.getDescription().isBlank()) {
            model.addAttribute("createError", true);
            log.info("ERROR AL INTENTAR CREAR COMUNIDAD");
            return "communities/create";
        }
        model.addAttribute("createError", false);

        // Usuario logueado que ejecuta esta query -> creador de la comunidad
        User owner = (User) session.getAttribute("u");
        owner = entityManager.find(User.class, owner.getId());
        if (community.getMembers().isEmpty())
            owner.getOwnedCommunities().add(community);

        // Set community owner and add it as member
        community.setOwner(owner);
        community.getMembers().add(owner);
        entityManager.persist(community);
        entityManager.flush();

        log.info("New community created by: {}", community.getOwner().getUsername());
        log.info("New community created with title: {}", community.getTitle());
        log.info("New community created with description: {}", community.getDescription());
        return "redirect:/communities";
    }
}