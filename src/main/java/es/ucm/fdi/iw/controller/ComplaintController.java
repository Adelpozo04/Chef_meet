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

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import es.ucm.fdi.iw.model.Message;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class ComplaintController {
    
    //Sistema de logs
    private static final Logger log = LogManager.getLogger(ComplaintController.class);

    //Uso del entity manager para hacer consultas sobre la base de datos
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Carga todas las listas necesarias para crear una queja:
    // recetas, eventos, comunidades y usuarios.
    // Se separa en un metodo porque se usa tanto al entrar al formulario
    // como cuando hay un error y hay que volver a mostrar la pagina.
    private void addComplaintCreateLists(Model model) {
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
    }

    // Comprueba que el elemento denunciado existe realmente.
    private boolean referenceExists(Integer type, Long referenceId) {
        if (type == null || referenceId == null) {
            return false;
        }

        if (type.equals(Complaint.typeMap.get("USER"))) {
            return entityManager.find(User.class, referenceId) != null;
        }

        if (type.equals(Complaint.typeMap.get("RECIPE"))) {
            return entityManager.find(Recipe.class, referenceId) != null;
        }

        if (type.equals(Complaint.typeMap.get("COMMUNITY"))) {
            return entityManager.find(Community.class, referenceId) != null;
        }

        if (type.equals(Complaint.typeMap.get("EVENT"))) {
            return entityManager.find(Event.class, referenceId) != null;
        }

        return false;
    }

    // Devuelve el texto del elemento denunciado.
    // Sirve para que en la vista no aparezca solo el ID,
    // sino el nombre real del usuario, receta, comunidad o evento.
    private String getReferenceText(Complaint complaint, Model model) {
        if (complaint.getType() == null || complaint.getReferenceId() == null) {
            return "Sin elemento asociado";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("USER"))) {
            User user = entityManager.find(User.class, complaint.getReferenceId());
            model.addAttribute("user", user);
            return user != null ? user.getUsername() : "Usuario eliminado";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("RECIPE"))) {
            Recipe recipe = entityManager.find(Recipe.class, complaint.getReferenceId());
            model.addAttribute("recipe", recipe);
            return recipe != null ? recipe.getTitle() : "Receta eliminada";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("COMMUNITY"))) {
            Community community = entityManager.find(Community.class, complaint.getReferenceId());
            model.addAttribute("community", community);
            return community != null ? community.getTitle() : "Comunidad eliminada";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("EVENT"))) {
            Event event = entityManager.find(Event.class, complaint.getReferenceId());
            model.addAttribute("event", event);
            return event != null ? event.getTitle() : "Evento eliminado";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("MESSAGE"))) {
            Message message = entityManager.find(Message.class, complaint.getReferenceId());

            if (message == null) {
                return "Mensaje eliminado";
            }

            String senderName = message.getSender() != null 
                    ? message.getSender().getUsername() 
                    : "Usuario desconocido";

            return senderName + ": " + message.getText();
        }

        return "Tipo desconocido";
    }

    //Cuando se pulse el boton de crear queja se dirige a la pagina para rellenar el formulario con los datos de esta
    @GetMapping("complaint/create")
    public String createComplaint(Model model) {

        // Se cargan las listas para que el usuario pueda elegir
        // a qué usuario, receta, comunidad o evento quiere asociar la queja
        addComplaintCreateLists(model);
        // Objeto vacío que se rellenará con los datos del formulario
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

        // Validacion de los campos basicos:
        // título, descripcion y tipo de queja
       boolean invalidBasicFields =
            complaint.getTitle() == null || complaint.getTitle().isBlank()
            || complaint.getDescription() == null || complaint.getDescription().isBlank()
            || complaint.getType() == null;

        // Validacion del elemento denunciado
        boolean invalidReference = !referenceExists(complaint.getType(), complaint.getReferenceId());

        // Si falta algun campo o no hay elemento seleccionado,
        // se vuelve al formulario con mensaje de error
        if (invalidBasicFields || invalidReference) {
            addComplaintCreateLists(model);
            model.addAttribute("complaint", complaint);
            model.addAttribute("createError", true);

            log.info("ERROR AL INTENTAR CREAR QUEJA. Campos inválidos o referencia no seleccionada.");
            return "complaint/create";
        }

        

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

        complaint.setDate(LocalDateTime.now());
        
        entityManager.persist(complaint);
        entityManager.flush();

        showNotificationAdmin(complaint);
        log.info("New complaint created by: {}", complaint.getOwner().getUsername());
        log.info("New complaint created with title: {}", complaint.getTitle());
        log.info("New complaint created with description: {}", complaint.getDescription());
        return "redirect:/account";
        
    }

    //Metodo para meterse en la pagina de una queja concreta
    @GetMapping("complaint/{id}")
    public String showComplaint(@PathVariable long id, Model model, HttpSession session) {

        // Obtener de la sesion actual del usuario

        User sessionUser = (User) session.getAttribute("u");
        User currentUser = entityManager.find(User.class, sessionUser.getId());

        boolean isAdmin = currentUser.hasRole(User.Role.ADMIN);

        // Si no es admin no puede entrar a esta vista
        if (!isAdmin) {
            return "redirect:/account";
        }

        // Buscar la info de la receta en la base de datos usando el id que viene en la url
        Complaint complaint = entityManager.find(Complaint.class, id);

        // Si el id no existe, se redirige a eventos
        if (complaint == null) {
            return "redirect:/admin/";
        }

        // Obtiene el nombre del elemento denunciado
        String referenceText = getReferenceText(complaint, model);
        // Texto legible del tipo de queja
        String typeText = "";

        if (complaint.getType().equals(Complaint.typeMap.get("USER"))) {
            typeText = "Usuario";
        }
        else if (complaint.getType().equals(Complaint.typeMap.get("RECIPE"))) {
            typeText = "Receta";
        }
        else if (complaint.getType().equals(Complaint.typeMap.get("COMMUNITY"))) {
            typeText = "Comunidad";
        }
        else if (complaint.getType().equals(Complaint.typeMap.get("EVENT"))) {
            typeText = "Evento";
        }
        else if (complaint.getType().equals(Complaint.typeMap.get("MESSAGE"))) {
            typeText = "Mensaje de chat";
        }
        

        model.addAttribute("complaint", complaint);
        model.addAttribute("referenceText", referenceText);
        model.addAttribute("typeText", typeText);
        
        return "complaint";
    }

    //Metodo que maneja que una queja se marque como resuelta, llamado por el admin
    @Transactional
    @PostMapping("/complaint/{id}")
    public String resolveComplaint(@PathVariable long id, HttpSession session) {

        // Obtener de la sesion actual el usuario guardado
        User sessionUser = (User) session.getAttribute("u");
        User currentUser = entityManager.find(User.class, sessionUser.getId());

        // Comprobar si el usuario es admin
        boolean isAdmin = currentUser.hasRole(User.Role.ADMIN);

        Complaint complaint = entityManager.find(Complaint.class, id);

        if (complaint != null && isAdmin) {

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

    // Permite denunciar un mensaje de chat mediante AJAX.
    // Se llama desde JavaScript al hacer click sobre un mensaje y aceptar el confirm.
    @Transactional
    @PostMapping("complaint/report/{messageId}")
    @ResponseBody
    public Map<String, Object> reportMessage(
            @PathVariable long messageId,
            HttpSession session) {

        // Buscar el mensaje denunciado en base de datos
        Message message = entityManager.find(Message.class, messageId);

        // Si el mensaje no existe, se devuelve error
        if (message == null) {
            return Map.of(
                "ok", false,
                "error", "El mensaje denunciado no existe"
            );
        }

        // Obtener el usuario logueado que realiza la denuncia
        User owner = (User) session.getAttribute("u");

        // Si no hay usuario en sesion, no se permite denunciar
        if (owner == null) {
            return Map.of(
                "ok", false,
                "error", "Debes iniciar sesión para denunciar mensajes"
            );
        }

        // Recargar el usuario desde base de datos
        owner = entityManager.find(User.class, owner.getId());

        // Crear una nueva queja asociada al mensaje denunciado
        Complaint complaint = new Complaint();
        complaint.setOwner(owner);
        complaint.setTitle("Mensaje de chat denunciado");

        // La descripción guarda información util para que el admin entienda la denuncia
        complaint.setDescription(
            "Mensaje enviado por " 
            + (message.getSender() != null ? message.getSender().getUsername() : "usuario desconocido")
            + ": " 
            + message.getText()
        );

        // Tipo especifico de queja: mensaje
        complaint.setType(Complaint.typeMap.get("MESSAGE"));

        // Guardar el id del mensaje denunciado
        complaint.setReferenceId(message.getId());

        // Una denuncia nueva empieza como pendiente
        complaint.setResolved(false);

        // Asociar la queja al usuario que denuncia
        owner.getComplaints().add(complaint);

        // Guardar la queja en base de datos
        entityManager.persist(complaint);
        entityManager.flush();

        log.info("El usuario {} ha denunciado el mensaje {}", owner.getUsername(), message.getId());

        // Respuesta JSON para la llamada AJAX
        return Map.of(
            "ok", true,
            "complaintId", complaint.getId()
        );
    }

    // Mostrar notificacion al admin cada vez que un usuario haga una queja
    private void showNotificationAdmin(Complaint complaint) {
        try {

                if(complaint == null) return;

                if(complaint.getType() == null) return;

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode rootNode = mapper.createObjectNode();

                String reporterName = complaint.getOwner() != null ?
                complaint.getOwner().getUsername() : "Usuario desconocido";

                String reportedText = getReportedText(complaint);

                rootNode.put("type", "NEW_COMPLAINT");
                // Texto de la notificacion
                rootNode.put("text", reporterName + " ha reportado " + reportedText);
                // Convertir JSON a texto
                String json = mapper.writeValueAsString(rootNode);

                // Publicar en el canal del admin
                messagingTemplate.convertAndSend("/topic/admin", json);
                
            } catch (Exception e) {

               log.error("Error al publicar la notificacion de queja al admin", e );
            }
    }

    private String getReportedText(Complaint complaint) {
        if (complaint.getReferenceId() == null) {
            return "un elemento";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("USER"))) {
            User reported = entityManager.find(User.class, complaint.getReferenceId());
            return reported != null 
                ? "al usuario " + reported.getUsername()
                : "a un usuario desconocido";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("RECIPE"))) {
            Recipe recipe = entityManager.find(Recipe.class, complaint.getReferenceId());
            return recipe != null 
                ? "la receta " + recipe.getTitle()
                : "una receta desconocida";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("COMMUNITY"))) {
            Community community = entityManager.find(Community.class, complaint.getReferenceId());
            return community != null 
                ? "la comunidad " + community.getTitle()
                : "una comunidad desconocida";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("EVENT"))) {
            Event event = entityManager.find(Event.class, complaint.getReferenceId());
            return event != null 
                ? "el evento " + event.getTitle()
                : "un evento desconocido";
        }

        if (complaint.getType().equals(Complaint.typeMap.get("MESSAGE"))) {
            return "un mensaje de chat";
        }

        return "un elemento";
    }
}
