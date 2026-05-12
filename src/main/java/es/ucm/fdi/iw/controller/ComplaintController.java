package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.transaction.Transactional;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.Complaint;
import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Recipe;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class ComplaintController {
    
    private static final Logger log = LogManager.getLogger(ComplaintController.class);

    @Autowired
    private EntityManager entityManager;

    //Cuando se pulse el boton de crear receta se dirige a la pagina para rellenar el formulario con los datos de esta
    @GetMapping("complaint/create")
    public String createComplaint(Model model) {

        List<Recipe> allRecipes = entityManager.createQuery(
            "SELECT r FROM Recipe r ORDER BY r.title ASC", Recipe.class)
            .getResultList();

        List<Event> allEvents = entityManager.createQuery(
            "SELECT e FROM Event e ORDER BY e.title ASC", Event.class)
            .getResultList();

        List<Community> allCommunities = entityManager.createQuery(
            "SELECT c FROM Community c ORDER BY c.title ASC", Community.class)
            .getResultList();

        List<User> allUsers = entityManager.createQuery(
            "SELECT u FROM User u ORDER BY u.username ASC", User.class)
            .getResultList();

        model.addAttribute("AllRecipes", allRecipes);
        model.addAttribute("AllEvents", allEvents);
        model.addAttribute("AllCommunities", allCommunities);
        model.addAttribute("AllUsers", allUsers);
        model.addAttribute("complaint", new Complaint());
        
        return "complaint/create";
    }

    @Transactional
    @PostMapping("complaint/create")
    public String createComplaint(
            @ModelAttribute Complaint complaint,
            @ModelAttribute User edited,
            Model model,
            HttpSession session) {

        if (complaint.getTitle().isBlank() || complaint.getDescription().isBlank()){
            model.addAttribute("createError", true);
            log.info("ERROR AL INTENTAR CREAR QUEJA");
            return "complaint/create";
        }

        model.addAttribute("createError", false);

        // Usuario logueado que ejecuta esta query -> creador de la queja
        User owner = (User) session.getAttribute("u");
        owner = entityManager.find(User.class, owner.getId());
        if (complaint.getOwner() == null){
            owner.getComplaints().add(complaint);
        }
            
        // Set community owner and add it as member
        complaint.setOwner(owner);
        complaint.setResolved(false);
        entityManager.persist(complaint);
        entityManager.flush();

        log.info("New complaint created by: {}", complaint.getOwner().getUsername());
        log.info("New complaint created with title: {}", complaint.getTitle());
        log.info("New complaint created with description: {}", complaint.getDescription());
        return "redirect:/account";
        
    }

    // Cargar quejas
    @GetMapping({"", "/"})
    public String showComplaints(Model model) {
        // Se piden todas las quejas a la base de datos
        List<Complaint> complaints = entityManager.createQuery("SELECT c FROM Complaint c", Complaint.class).getResultList();
        model.addAttribute("complaints", complaints);
        return "account"; // Redirige a complaint.html
    }

}
