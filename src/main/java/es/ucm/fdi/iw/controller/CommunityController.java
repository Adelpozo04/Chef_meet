package es.ucm.fdi.iw.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.Country;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Recipe;
import es.ucm.fdi.iw.model.Message.Transfer;
import es.ucm.fdi.iw.model.User.Role;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Controller
@RequestMapping("/communities")
public class CommunityController {

    private static final Logger log = LogManager.getLogger(CommunityController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LocalData localData;
    

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
        HttpSession session
    ) {

        User user = entityManager.find(User.class, ((User) session.getAttribute("u")).getId() );
        Community community = entityManager.find(Community.class, id);
        User owner = community.getOwner();
        List<Message> messages = entityManager
                                        .createNamedQuery("Message.withReferencedId", Message.class)
                                        .setParameter("rID", id)
                                        .setMaxResults(50)
                                        .getResultList();

        List<Message.Transfer> lastMessages = new ArrayList<>();
        for(Message m : messages) {
            lastMessages.add( new Transfer(m.getSender().getUsername(), m.getText()) );
        }

        boolean userIsOwner = community.getOwner().getId() == user.getId();
        boolean userIsAdmin = user.hasRole(Role.ADMIN);
        boolean userIsMember = community
                                .getMembers()
                                .stream()
                                .anyMatch(u -> u.getId() == user.getId());


        // Separar los eventos en proximos y pasados
        LocalDateTime now = LocalDateTime.now();
        List<Event> upcomingEvents = community.getEvents().stream()
                .filter(e -> e.getDate().isAfter(now))
                .toList();
        List<Event> pastEvents = community.getEvents().stream()
                .filter(e -> e.getDate().isBefore(now))
                .toList();
        
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("pastEvents", pastEvents);
            
        List<Recipe> recipes = community.getRecipes().stream().toList();

        model.addAttribute("recipes", recipes);

        model.addAttribute("community", community);
        model.addAttribute("isOwner", userIsOwner);
        model.addAttribute("isMember", userIsMember);
        model.addAttribute("isAdmin", userIsAdmin);
        model.addAttribute("owner", owner);
        model.addAttribute("members", community.getMembers());
        model.addAttribute("lastMessages", lastMessages);

        return "communities/view";
    }

    @Transactional
    @PostMapping("/{id}/delete")
    public String deleteCommunity(
        @PathVariable long id,
        HttpSession session) {

        User user = (User) session.getAttribute("u");
        Community community = entityManager.find(Community.class, id);

        // Eliminar comunidad de la base de datos
        if(user.hasRole(Role.ADMIN) || community.getOwner().getId() == user.getId())
            entityManager.remove(community);

        // Eliminar canal de la comunidad
        List<String> topics = new ArrayList<>(
            Arrays.asList(((String) session.getAttribute("topics")).split(","))
        );
        topics.removeIf( t -> t.equals(String.format("community-%d", community.getId())) );
        session.setAttribute("topics", topics);

        return "redirect:/communities";
    }

    @Transactional
    @PostMapping("/{id}/add")
    public String addUserToCommunity(
        @PathVariable long id,
        HttpSession session
    ){

        User user = entityManager.find(User.class, ((User) session.getAttribute("u")).getId() );
        Community community = entityManager.find(Community.class, id);

        // Actualizar topics
        List<String> topics = new ArrayList<>();
        topics.add("community-" + community.getId());
        session.setAttribute("topics", String.join(",", topics));

        if ( !community.getMembers().contains(user) )
            community.getMembers().add(user);


        log.info("Usuario {} quiere unirse a la comunidad con ID {}", user.getUsername(), community.getId());
        return "redirect:/communities/" + id;
    }

    @Transactional
    @PostMapping("/{id}/remove")
    public String removeUserFromCommunity(
        @PathVariable long id,
        HttpSession session
    ){

        User user = entityManager.find(User.class, ((User) session.getAttribute("u")).getId() );
        Community community = entityManager.find(Community.class, id);

        // Eliminar de la base de datos
        community.getMembers().removeIf( u -> u.getId() == user.getId() );

        // Eliminar canal de la comunidad
        List<String> topics = new ArrayList<>(
            Arrays.asList(((String) session.getAttribute("topics")).split(","))
        );
        topics.removeIf( t -> t.equals(String.format("community-%d", community.getId())) );
        session.setAttribute("topics", topics);

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
        RedirectAttributes redirectAttributes,  // Usado para mantener atributos de error
        @RequestParam(value = "countryID", required = false) Long countryId,
        @RequestParam("photo") MultipartFile photo,
        HttpSession session
    ) {

        // Validar titulo de comunidad, descripcion y pais -> Deben proporcionarse
        if (community.getTitle().isBlank() || community.getDescription().isBlank() || countryId == null) {
            redirectAttributes.addFlashAttribute("errorIncomplete", true);
            log.error("Intentando crear comunidad: Campos vacios");
            return "redirect:/communities/create";
        }

        // Validar imagen -> Maximo de 5MB y formato permitido
        if(!photo.isEmpty()) {
            // Verificar el tipo de archivo
            String contentType = photo.getContentType();

            if(contentType == null ||  (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))){
                // Mandar señal de error al HTML y recargar formulario
                redirectAttributes.addFlashAttribute("errorImage", true);
                return "redirect:/communities/create";
            }

            // Restriccion de tamanyo
            long maxBytes = 5 * 1024 * 1024; // 5 MB
            if (photo.getSize() > maxBytes) {
                redirectAttributes.addFlashAttribute("errorSize", true);
                return "redirect:/communities/create";
            }
        }

        // Usuario logueado que ejecuta esta query -> creador de la comunidad
        User owner = entityManager.find(User.class, ((User)session.getAttribute("u")).getId() );
        if (community.getMembers().isEmpty())
            owner.getOwnedCommunities().add(community);

        // Crear comunidad y almacenar en BBDD
        community.setOwner(owner);
        community.getMembers().add(owner);
        community.setCountry(entityManager.find(Country.class, countryId));
        entityManager.persist(community);
        entityManager.flush();

        // Actualizar topics
        List<String> topics = new ArrayList<>();
        topics.add("community-" + community.getId());
        session.setAttribute("topics", String.join(",", topics));

        // Guardar la imagen si el usuario ha subido alguna
        if(!photo.isEmpty()) {
            try {
                // Obtener el archivo donde se guarda la imagen dentro de la carpeta 'events' en 'iwdata'
                File f = localData.getFile("communities", String.valueOf(community.getId()));

                // Asegurar que la carpeta 'events' existe antes de guardar, si no, se crea
                f.getParentFile().mkdirs();

                try(BufferedOutputStream stream = new BufferedOutputStream( new FileOutputStream(f) )) {
                    stream.write(photo.getBytes());
                }

                // Actualizar la imagen del evento con la ruta 
                community.setImagePath("/communities/" + community.getId() + "/pic");

                // Forzar a la base de datos a guardar esta actualizacion
                entityManager.merge(community);
                entityManager.flush();
            }
            catch (IOException e) {
                log.error("Error al guardar la imagen de la comunidad: ", e);
            }
        }

        model.addAttribute("errorIncomplete", false);
        log.info("New community created by: {}", community.getOwner().getUsername());
        log.info("New community created with title: {}", community.getTitle());
        log.info("New community created with description: {}", community.getDescription());
        return "redirect:/communities/" + String.valueOf(community.getId());
    }


    @GetMapping("/{id}/pic")
    @ResponseBody
    public void getCommunityPhoto(
        @PathVariable long id, 
        HttpServletResponse response
    ) throws IOException {

        File f = localData.getFile("communities", String.valueOf(id));
        if (f.exists() && f.canRead()) {
            // Si hay foto subida por el usuario, se proporciona al navegador
            response.setContentType("image/jpeg");
            Files.copy(f.toPath(), response.getOutputStream());
        }
        else {
            // Si no existe, se envia la imagen de por defecto
            response.sendRedirect("/img/communities/default.png");
        }

    }

}