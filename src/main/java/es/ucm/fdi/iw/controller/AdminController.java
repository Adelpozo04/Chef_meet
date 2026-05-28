package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Topic;
import es.ucm.fdi.iw.controller.EventController.DontHavePermissionException;
import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.Complaint;
import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Lorem;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Recipe;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

/**
* Controlador encargado de gestionar las funcionalidades de admin.
* Tofas las rutas parten de /admin
 */
@Controller
@RequestMapping("admin")
public class AdminController {

  // Codificador de contraseñas
  @Autowired
  private PasswordEncoder passwordEncoder;

  // Para realizar consultas a la base de datos
  @Autowired
  private EntityManager entityManager;

  /**
   * Añade al modelo algunosa tributos guardados en sesion
   * Se ejecuta antes de cada metodo del controlador 
  */
  @ModelAttribute
  public void populateModel(HttpSession session, Model model) {
    for (String name : new String[] { "u", "url", "ws", "topics"}) {
      model.addAttribute(name, session.getAttribute(name));
    }
  }

  private static final Logger log = LogManager.getLogger(AdminController.class);

  /**
   * Muestr la pagina principal del panel de admin
   */
  @GetMapping("/")
  public String index(Model model) {
    log.info("Admin acaba de entrar");

    // Cargar usuarios
    model.addAttribute("users",
        entityManager.createQuery("SELECT u FROM User u", User.class).getResultList());
    
    // Cargar recetas
    model.addAttribute("recipes", 
      entityManager.createQuery("SELECT r FROM Recipe r", Recipe.class).getResultList());
    
    // Cargar comunidades
    model.addAttribute("communities", 
      entityManager.createQuery("SELECT c FROM Community c", Community.class).getResultList());

    // Cargar eventos
    model.addAttribute("events", 
      entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList());

      // Cargar quejas
    model.addAttribute("complaints",
        entityManager.createQuery("SELECT c FROM Complaint c", Complaint.class).getResultList());
    
    // Devuelve la lista admin.html
    return "admin";
  }

  /**
   * Activa o desactiva un usuario
   * Devuelve un JSON indicando el nuevo estado del usuario
   */
  @PostMapping("/toggle/{id}")
  @Transactional
  @ResponseBody
  public String toggleUser(@PathVariable long id, Model model) {
    log.info("Admin cambia estado de " + id);

    // Busca user por id
    User target = entityManager.find(User.class, id);

    // Cambiar su estado actual
    target.setEnabled(!target.isEnabled());
    return "{\"enabled\":" + target.isEnabled() + "}";
  }

  /**
   * Devuelve en formato JSON los ultimo mensajes recibidos
   * Maximo de 5
   */
  @GetMapping(path = "all-messages", produces = "application/json")
  @Transactional // para no recibir resultados inconsistentes
  @ResponseBody // para indicar que no devuelve vista, sino un objeto (jsonizado)
  public List<Message.Transfer> retrieveMessages(HttpSession session) {
    
    // Consulta para obtener los mensajes
    TypedQuery<Message> query = entityManager.createQuery("select m from Message m", Message.class);
    
    query.setMaxResults(5);
    query.setFirstResult(0); // para paginar: cambias el 1er resultado
    
    // Convertir los mensajes a objteos Transfer para enviarlos como JSON
    return query.getResultList().stream().map(Transferable::toTransfer)
        .collect(Collectors.toList());
  }

  @RequestMapping("/populate")
  @ResponseBody
  @Transactional
  public String populate(Model model) {

    // create some groups
    Topic g1 = new Topic();
    g1.setName("g1");
    g1.setKey(UserController.generateRandomBase64Token(6));
    entityManager.persist(g1);
    Topic g2 = new Topic();
    g2.setName("g2");
    g2.setKey(UserController.generateRandomBase64Token(6));
    entityManager.persist(g2);

    // create some users & assign to groups
    for (int i = 0; i < 15; i++) {
      User u = new User();
      u.setUsername("user" + i);
      u.setPassword(passwordEncoder
          .encode("aa"));
            //UserController.generateRandomBase64Token(9)));
      u.setEnabled(true);
      u.setRoles(User.Role.USER.toString());
      u.setFirstName(Lorem.nombreAlAzar());
      u.setLastName(Lorem.apellidoAlAzar());
      entityManager.persist(u);
      if (i%2 == 0) {
        g1.getMembers().add(u);
        // u.getTopics().add(g1); NO FUNCIONA: propietario es g, no u
      }
      if (i%3 == 0) {
        g2.getMembers().add(u);
      }
    }
    return "{\"admin\": \"populated\"}";
  }

  /**
  * Eliminar un evento en la base de datos
  */
  @PostMapping("/event/{id}/delete") 
  @Transactional
  public String deleteEvent(@PathVariable long id) {

    log.info("Admin intentando borrar el evento con id: " + id);

    // Obtener el evento en la base de datos por su id
    Event event = entityManager.find(Event.class, id);

    // Si existe, eliminarlo
    if(event != null) {
      entityManager.remove(event);
      log.info("Evento borrado con exito.");
    }

    return "redirect:/admin/";
  } 

  /**
  * Borrar una comunidad en la base de datos
  */
  @PostMapping("/community/{id}/delete")
  @Transactional
  public String deleteCommunity(
    @PathVariable long id) {

    log.info("Admin intentando borrar la comunidad con id: " + id);

    // Obtener la comunidad en la base de datos por su id
    Community community = entityManager.find(Community.class, id);

    // Si existe, eliminarlo
    if(community != null) {
      entityManager.remove(community);
      log.info("Comunidad borrada con exito.");
    }
    
    return "redirect:/admin/";
  }

  /**
   *  Eliminar una receta de la base de datos
   */
  @PostMapping("/recipe/{id}/delete") 
  @Transactional
  public String deleteRecipe(@PathVariable long id) {

    log.info("Admin intentando borrar la receta con id: " + id);

    // Buscar receta por id
    Recipe recipe = entityManager.find(Recipe.class, id);

    if(recipe != null) {
        // Eliminar de la base de datos
        entityManager.remove(recipe);
        log.info("Receta borrada con exito.");
    }

    return "redirect:/admin/";
  } 

  /**
 * NUEVO
 * Página de administración para consultar cuántas veces
 * ha iniciado sesión cada usuario.
 *
 * Al estar dentro de /admin/login-stats, esta vista queda protegida
 * igual que el resto del panel de administración.
 */
@GetMapping("/stats")
public String showLoginStats(Model model) {

  // Cargar todos los usuarios ordenados por nombre de usuario
  List<User> users = entityManager.createQuery(
      "SELECT u FROM User u ORDER BY u.username ASC", User.class)
      .getResultList();

  // Enviar los usuarios a la vista.
  // Cada usuario ya contiene el campo loginCount.
  model.addAttribute("users", users);

  // Cargar la plantilla admin/loginStats.html
  return "adminStats";
}
}