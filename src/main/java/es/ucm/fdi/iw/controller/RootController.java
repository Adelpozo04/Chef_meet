package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.Event;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
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

    @Autowired
    private es.ucm.fdi.iw.LocalData localData; // Clase que gestiona el guardado de archivos del proyecto

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Procesar los datos del registro
    // Puede recibir el parametro photo
    @Transactional
    @PostMapping("/register")
    public String postRegister(@ModelAttribute User newUser, @RequestParam String pass2, @RequestParam("photo") MultipartFile photo, Model model) {
        
        // Comprobar si las contraseñas coinciden
        if (!newUser.getPassword().equals(pass2)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        // Comprobar si el nombre de usuario ya existe en la base de datos
        long count = entityManager.createNamedQuery("User.hasUsername", Long.class)
                .setParameter("username", newUser.getUsername())
                .getSingleResult();
        
        if (count > 0) {
            model.addAttribute("error", "Ese nombre de usuario no está disponible");
            return "register";
        }
    
        // Configurar al nuevo usuario
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Hashear la contraseña
        newUser.setRoles(User.Role.USER.toString()); // Darle el rol basico
        newUser.setEnabled(true); // Activar la cuenta

        // Guardar el nuevo usuario en la base de datos para generar su ID
        entityManager.persist(newUser);
        entityManager.flush(); // Para obligar a la base de datos a generar el ID inmediatamente

        log.info("Nuevo usuario registrado: {} con ID {}", newUser.getUsername(), newUser.getId());

        // Guardar la foto para el avatar si se ha subido alguna
        if(!photo.isEmpty()) {
            try {
                // Obtener la ruta del archivo
                File f = localData.getFile("user", "" + newUser.getId() + ".jpg");
                f.getParentFile().mkdirs(); // Crear la carpeta si no existe

                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
                    stream.write(photo.getBytes());
                }
                log.info("Foto de perfil guardada correctamente para el usuario: {}", newUser.getId());
            } catch (Exception e) {
                log.warn("Error al guardar la foto de perfil para el usuario " + newUser.getId(), e);
            }
        }
        // Redirigir al login con un mensaje de exito
        return "redirect:/login?registerd=true";
    }
    @GetMapping("/account")
    public String account(Model model, HttpSession session, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);

        // Obtener el usuario de la sesion
        User u = (User) session.getAttribute("u");
        if(u != null) {

            List<Event> myEvents = entityManager.createQuery(
                "SELECT r.event FROM Reservation r WHERE r.attendee.id = :userId ORDER BY r.event.title ASC", Event.class)
                .setParameter("userId", u.getId())
                .getResultList();
            
            List<Community> myCreatedCommunities = entityManager.createNamedQuery("Community.ownedCommunities", Community.class)
                                                    .setParameter("id", u.getId())
                                                    .getResultList();

            List<Community> myJoinedCommunities = entityManager.createNamedQuery("Community.joinedCommunities", Community.class)
                                                    .setParameter("id", u.getId())
                                                    .getResultList();

            
            for(Community c : myCreatedCommunities) {
                log.debug("Community {} created by user {}", c.getTitle(), u.getUsername());
            }
            for(Community c : myJoinedCommunities) {
                log.debug("Community {} has user {} joined", c.getTitle(), u.getUsername());
            }

            // Pasar la lista ordenada al HTML
            model.addAttribute("myEvents", myEvents);
            model.addAttribute("myCreatedCommunities", myCreatedCommunities);
            model.addAttribute("myJoinedCommunities", myJoinedCommunities);
        }
        return "account";
    }

    @GetMapping("/authors")
    public String autores(Model model) {
        return "authors";
    }
}
