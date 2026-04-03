package es.ucm.fdi.iw.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.Country;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Controller
@RequestMapping("/communities")
public class CommunityController {

    private static final Logger log = LogManager.getLogger(CommunityController.class);

    @Autowired
    private EntityManager entityManager;


    @GetMapping
    public String showCommunities(Model model, HttpSession session) {

        User client = (User) session.getAttribute("u");
        List<Community> myCommunities = new ArrayList<>();
        List<Community> otherCommunities = null;

        if(client != null){
            myCommunities = entityManager.createNamedQuery("Community.selectWhereMeMember", Community.class)
            .setParameter("userId", client.getId())
            .getResultList();

            log.info("Mostrar comunidades creadas por usuario con ID {}", client.getId());
            for(Community c : myCommunities){
                log.info("Comunidad {} encontrada", c.getTitle());
            }

        }

        otherCommunities = entityManager.createNamedQuery("Community.selectAll", Community.class).getResultList();
        for(Community c : myCommunities){   // Eliminar las repeticiones -> MUY TEMPORAL
            otherCommunities.removeIf(comm -> comm.getId() == c.getId());
        }

        model.addAttribute("myCommunities", myCommunities);
        model.addAttribute("otherCommunities", otherCommunities);
        return "communities";
    }


    @GetMapping("/{id}")
    public String showOneCommunity(
        @PathVariable long id, 
        Model model,
        HttpSession session) {

        User user = (User) session.getAttribute("u");
        Community community = entityManager.find(Community.class, id);
        User owner = community.getOwner();
        boolean userIsOwner = community.getOwner().getId() == user.getId();
        boolean userIsMember = community.getMembers().stream().
                                anyMatch(u -> u.getId() == user.getId());

        if(userIsOwner)
            log.info("El usuario {} es el creador de la comunidad {}", user.getUsername(), community.getTitle());
        else
            log.info("El usuario {} NO es el creador de la comunidad {}", user.getUsername(), community.getTitle());

        if(userIsMember)
            log.info("El usuario {} pertence a la comunidad {}", user.getUsername(), community.getTitle());
        else
            log.info("El usuario {} NO pertence a la comunidad {}", user.getUsername(), community.getTitle());

        // Separar los eventos en proximos y pasados
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Event> upcomingEvents = community.getEvents().stream()
                .filter(e -> e.getDate().isAfter(now))
                .toList();
        List<Event> pastEvents = community.getEvents().stream()
                .filter(e -> e.getDate().isBefore(now))
                .toList();
        
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("pastEvents", pastEvents);
            
        model.addAttribute("community", community);
        model.addAttribute("isOwner", userIsOwner);
        model.addAttribute("isMember", userIsMember);
        model.addAttribute("owner", owner);
        model.addAttribute("members", community.getMembers());

        return "communities/view";
    }

    @Transactional
    @PostMapping("/{id}/add")
    public String addUserToCommunity(
        @PathVariable long id,
        HttpSession session){

        User user = (User) session.getAttribute("u");
        Community community = entityManager.find(Community.class, id);

        if ( !community.getMembers().contains(user) )
            community.getMembers().add(user);


        log.info("Usuario {} quiere unirse a la comunidad con ID {}", user.getUsername(), community.getId());
        return "redirect:/communities/" + id;
    }

    @Transactional
    @PostMapping("/{id}/remove")
    public String removeUserFromCommunity(
        @PathVariable long id,
        HttpSession session){

        User user = (User) session.getAttribute("u");
        Community community = entityManager.find(Community.class, id);

        community.getMembers().removeIf( u -> u.getId() == user.getId() );

        log.info("Usuario {} quiere salirse de la comunidad con ID {}", user.getUsername(), community.getId());
        return "redirect:/communities/" + id;
    }

    @GetMapping("/create")
    public String viewcreateCommunity(Model model) {

        List<Country> countries = entityManager.createNamedQuery("Country.selectAll", Country.class).getResultList();
        model.addAttribute("countries", countries);
        model.addAttribute("community", new Community());

        model.addAttribute("createError", false);

        return "communities/create";
    }

    @Transactional
    @PostMapping("/create")
    public String createCommunity(
            @ModelAttribute Community community,
            Model model,
            @RequestParam(value = "countryID") Long countryId,
            HttpSession session) {

        if (community.getTitle().isBlank() || community.getDescription().isBlank()) {
            model.addAttribute("createError", true);
            log.error("Intentando crear comunidad: Campos vacios");
            return "redirect:/communities/create";
        }

        // Usuario logueado que ejecuta esta query -> creador de la comunidad
        User owner = (User) session.getAttribute("u");
        owner = entityManager.find(User.class, owner.getId());
        if (community.getMembers().isEmpty())
            owner.getOwnedCommunities().add(community);

        community.setOwner(owner);
        community.getMembers().add(owner);
        community.setCountry(entityManager.find(Country.class, countryId));

        entityManager.persist(community);
        entityManager.flush();

        model.addAttribute("createError", false);
        log.info("New community created by: {}", community.getOwner().getUsername());
        log.info("New community created with title: {}", community.getTitle());
        log.info("New community created with description: {}", community.getDescription());
        return "redirect:/communities";
    }


}