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
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/communities")
public class CommunityController {

    private static final Logger log = LogManager.getLogger(UserController.class);

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/create")
    public String formularioCrear(Model model) {

        model.addAttribute("community", new Community());
        log.info("Creating new community!");

        return "communities/create";
    }

    @Transactional
    @PostMapping("/create")
    public String createCommunity(
        @ModelAttribute Community community, 
        @ModelAttribute User edited, 
        Model model, 
        HttpSession session) {

        User owner = (User) session.getAttribute("u");

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