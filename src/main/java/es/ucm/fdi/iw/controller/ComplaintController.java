package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    //Sistema de logs
    private static final Logger log = LogManager.getLogger(ComplaintController.class);

    //Uso del entity manager para hacer consultas sobre la base de datos
    @Autowired
    private EntityManager entityManager;

    //Cuando se pulse el boton de crear queja se dirige a la pagina para rellenar el formulario con los datos de esta
    @GetMapping("complaint/create")
    public String createComplaint(Model model) {

        //Se toman todas las recetas que existen
        List<Recipe> allRecipes = entityManager.createQuery(
            "SELECT r FROM Recipe r ORDER BY r.title ASC", Recipe.class)
            .getResultList();

        //Se toman todos los eventos que existen
        List<Event> allEvents = entityManager.createQuery(
            "SELECT e FROM Event e ORDER BY e.title ASC", Event.class)
            .getResultList();

        //Se toman todas las comunidades que existen
        List<Community> allCommunities = entityManager.createQuery(
            "SELECT c FROM Community c ORDER BY c.title ASC", Community.class)
            .getResultList();

        //Se toman todos los usuarios que existen
        List<User> allUsers = entityManager.createQuery(
            "SELECT u FROM User u ORDER BY u.username ASC", User.class)
            .getResultList();

        //Se añaden al modelo para su uso en la pagina de crear queja
        model.addAttribute("AllRecipes", allRecipes);
        model.addAttribute("AllEvents", allEvents);
        model.addAttribute("AllCommunities", allCommunities);
        model.addAttribute("AllUsers", allUsers);
        model.addAttribute("complaint", new Complaint());
        
        return "complaint/create";
    }

    //Una vez se envia la queja creada
    @Transactional
    @PostMapping("complaint/create")
    public String createComplaint(
            @ModelAttribute Complaint complaint,
            @ModelAttribute User edited,
            Model model,
            HttpSession session) {

        //Se revisa que esta tenga los campos necesarios rellenados, si no es asi se muestra un mensaje de error y se vuelve a la pagina de crear queja
        if (complaint.getTitle().isBlank() || complaint.getDescription().isBlank() || complaint.getType() == null){
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
            
        // Se indica la persona que ha dejado la queja
        complaint.setOwner(owner);

        //Se pone que la queja aun no esta resuelta ya que se acaba de crear
        complaint.setResolved(false);
        entityManager.persist(complaint);
        entityManager.flush();

        log.info("New complaint created by: {}", complaint.getOwner().getUsername());
        log.info("New complaint created with title: {}", complaint.getTitle());
        log.info("New complaint created with description: {}", complaint.getDescription());
        return "redirect:/account";
        
    }

    //Metodo para meterse en la pagina de una queja concreta
    @GetMapping("complaint/{id}")
    public String showComplaint(@PathVariable long id, Model model) {

        // Buscar la info de la receta en la base de datos usando el id que viene en la url
        Complaint complaint = entityManager.find(Complaint.class, id);

        // Si el id no existe, se redirige a eventos
        if (complaint == null) {
            return "redirect:/admin/";
        }

        //Se carga el elemento correspondiente segun el tipo de queja
        if(complaint.getType() == Complaint.typeMap.get("RECIPE")) {
            Recipe recipe = entityManager.find(Recipe.class, complaint.getReferenceId());

            model.addAttribute("recipe", recipe);
        }
        else if(complaint.getType() == Complaint.typeMap.get("EVENT")) {
            Event event = entityManager.find(Event.class, complaint.getReferenceId());

            if(event == null){
                log.info("ERROR: La queja con id {} se refiere a un evento con id {} que no existe", complaint.getId(), complaint.getReferenceId());
            }

            model.addAttribute("event", event);
        }
        else if(complaint.getType() == Complaint.typeMap.get("COMMUNITY")) {
            Community community = entityManager.find(Community.class, complaint.getReferenceId());

            model.addAttribute("community", community);
        }
        else if(complaint.getType() == Complaint.typeMap.get("USER")) {
            User user = entityManager.find(User.class, complaint.getReferenceId());

            model.addAttribute("user", user);
        }

        //Se toma la queja correspondiente para mostrar su información facilmente
        model.addAttribute("complaint", complaint);
        
        return "complaint";
    }

    //Metodo que maneja que una qieja se marque como resuelta, llamado por el admin
    @Transactional
    @PostMapping("/complaint/{id}")
    public String resolveComplaint(@PathVariable long id) {

        Complaint complaint = entityManager.find(Complaint.class, id);

        if (complaint != null) {

            complaint.setResolved(true);

            log.info("Complaint {} marcada como resuelta", complaint.getId());
        }

        return "redirect:/admin/";
    }

    /* 
    // Cargar quejas
    @GetMapping({"", "/"})
    public String showComplaints(Model model) {
        // Se piden todas las quejas a la base de datos
        List<Complaint> complaints = entityManager.createQuery("SELECT c FROM Complaint c", Complaint.class).getResultList();
        model.addAttribute("complaints", complaints);
        return "account"; // Redirige a complaint.html
    }*/

    // Borrar la queja en la base de datos
    @Transactional
    @PostMapping("complaint/{id}/delete") 
    public String deleteComplaint(@PathVariable long id) {
        // Buscar la queja en la base de datos por su id
        Complaint complaint = entityManager.find(Complaint.class, id);

        if(complaint != null) {
            // Eliminar de la base de datos
            entityManager.remove(complaint);
            log.info("El administrador ha borrado la queja: {}", complaint.getTitle());
        }

        return "redirect:/admin/";
    } 

}
