package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/event")
public class EventController {
    
    private static final Logger log = LogManager.getLogger(EventController.class);

    @Autowired
    private EntityManager entityManager;

    // Cargar eventos
    @GetMapping({"", "/"})
    public String showEvents(Model model, HttpSession session, @RequestParam(required = false) String error) {
        // Si la URL trae "?error=full", se le envia la señal al HTML
        if("full".equals(error)) {
            model.addAttribute("errorFull", true);
        }
        // Se piden todos los eventos a la base de datos
        List<Event> allEvents = entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList();
        
        User sessionUser = (User) session.getAttribute("u");

        if(sessionUser != null) {
            // Usuario logueado: separar en dos listas
            long uid = sessionUser.getId();

            // Mis eventos: Soy el organizador estoy en la lista de asistentes
            List<Event> myEvents = allEvents.stream()
                .filter(e -> (e.getOrganizer() != null && e.getOrganizer().getId() == uid) ||
                             (e.getOrganizer() != null && e.getAttendees().stream().anyMatch(r -> r.getAttendee().getId() == uid)))      
                .toList();
                
            // Otros eventos
            List<Event> otherEvents = allEvents.stream()
                .filter(e -> !myEvents.contains(e))
                .toList();
            
            model.addAttribute("myEvents", myEvents);
            model.addAttribute("otherEvents", otherEvents);

        }
        else {
            // Usuario anonimo: todos los eventos van a otros eventos
            model.addAttribute("otherEvents", allEvents);
        }

        // Todos los eventos
        model.addAttribute("events", allEvents);
        return "event"; // Redirige a event.html
    }
    
    // Mostrar formulario? crear evento
    @GetMapping("/create") 
    public String createEvent(Model model) {
        model.addAttribute("event", new Event());
        return "event/create";
    }

    // Guardar el evento en la base de datos
    @Transactional
    @PostMapping("/create")
    public String createEvent(
        @Valid @ModelAttribute Event event,
        BindingResult result,
        @RequestParam("photo") MultipartFile photo,
        Model model, // para poder mandar mensajes de error al HTML
        HttpSession session) {
            
            // Comprobar si hay errores en los datos del evento
            if(result.hasErrors()) {
                log.warn("Errores de validacion en el evento: {}", result.getAllErrors());
                return "event/create";
            }
            // Validar imagen
            if(!photo.isEmpty()) {
                // Verificar el tipo de archivo
                String contentType = photo.getContentType();

                if(contentType == null ||  (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))){
                    // Mandar señal de error al HTML y recargar formulario
                    model.addAttribute("errorImage", true);
                    return "event/create";
                }
            }
            // Obtener el usuario logueado de la sesion
            User organizer = (User) session.getAttribute("u");
            organizer = entityManager.find(User.class, organizer.getId());

            // Asignarlo como organizador
            event.setOrganizer(organizer);

            // Guardar en la base de datos
            entityManager.persist(event);
            entityManager.flush();

            // Guardar la imagen si el usuario ha subido alguna
            if(!photo.isEmpty()) {
                try {
                    String contentType = photo.getContentType();
                    // Asignar la extension correcta segun el tipo
                    String extension = contentType.equals("image/png") ? ".png" : ".jpg";
                    // Nombrar la foto usando el id del evento
                    String fileName = "ev_" + event.getId() + extension;

                    // Asegurar de que la carpeta existe fisicamente, si no la crea
                    Path directory = Paths.get("src/main/resources/static/img/events/");
                    if(!Files.exists(directory)) {
                        Files.createDirectories(directory);
                    }
                    // Ruta final
                    Path path = directory.resolve(fileName);

                    // Guardar el archivo
                    Files.write(path, photo.getBytes());

                    // Actualizar el evento con la ruta para que el HTML sepa donde buscarla
                    event.setImagePath("/img/events/" + fileName);

                    // Forzar a la base de datos a guardar esta actualizacion
                    entityManager.merge(event);
                    entityManager.flush();
                    // Al usar @Transactional, JPA guarda este cambio final en la base de datos automaticamente
                } catch (IOException e) {
                    log.error("Error al guardar la imagen del evento", e);
                }
            }

            log.info("Nuevo evento creado por {}: {}", organizer.getUsername(), event.getTitle());

            return "redirect:/event";

    }

    // Borrar el evento en la base de datos
    @Transactional
    @PostMapping("/{id}/delete") 
    public String deleteEvent(@PathVariable long id) {
        // Buscar el evento en la base de datos pot su id
        Event event = entityManager.find(Event.class, id);

        if(event != null) {
            // Eliminar de la base de datos
            entityManager.remove(event);
            log.info("El administrador ha borrado el evento: {}", event.getTitle());
        }

        return "redirect:/event";
    } 

    // Endpoint de la API REST que devuelve todos los eventos registrados en la base de datos.
    // Implementado como un punto de acceso para peticiones asincronas AJAX o fetch desde el frontend, 
    // para cargar los marcadores de ubicaciones en el mapa

    // Cuando el navegador hace una peticion web a esta ruta, entra aqui
    // Respuesta en formato JSON
    @GetMapping(path = "/api/all", produces = "application/json")
    @ResponseBody
    public List<Event.Transfer> getEventsForMap() {
        // Consulta a la base de datos para obtener todos los eventos y guardarlos en una lista de objetos Event.Java
        List<Event> events = entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList();

        // Convertir los eventos al formato JSON seguro Event.Transfer
        return events.stream().map(Event::toTransfer).collect(Collectors.toList());
    }
}
