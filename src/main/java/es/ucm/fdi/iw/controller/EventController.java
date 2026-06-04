package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Reservation;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.controller.UserController.NoEsTuPerfilException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.messaging.simp.SimpMessagingTemplate;


@Controller
@RequestMapping("/event")
public class EventController {
    
    private static final Logger log = LogManager.getLogger(EventController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LocalData localData;

    // Excepcion para denegar una accion a usuarios no autorizados
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "No tienes permisos para realizar esta acción")
        public static class DontHavePermissionException extends RuntimeException {
    } 
    // Obtener clave de google maps desde application.properties
    @Value("${google.maps.key:}")
    private String googleMapsKey;

    // Cargar eventos
    @GetMapping({"", "/"})
    public String showEvents(Model model, HttpSession session, @RequestParam(required = false) String error) {
        // Si la URL trae "?error=full", se le envia la señal al HTML
        if("full".equals(error)) {
            model.addAttribute("errorFull", true);
        }

        // Se piden todos los eventos a la base de datos
        List<Event> allEvents = entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList();
        
        // Obtener de la sesion actual el usuario guardado
        User sessionUser = (User) session.getAttribute("u");

        List<Event> visibleEvents = new java.util.ArrayList<>();

        // Filtrar eventos segun privacidad
        for(Event e: allEvents) {
            // Eventos publicos, siempre visibles por cualquier usuario
            if(!e.isPrivate()) {
                visibleEvents.add(e); 
            } 
            else if(sessionUser != null) {
                User u = entityManager.find(User.class, sessionUser.getId());

                // Comprobar si el usuario es admin
                boolean isAdmin = u.hasRole(User.Role.ADMIN);
                // Es miembro de la comunidad asociada al evento?
                boolean isMember = e.getCommunity() != null && e.getCommunity().getMembers().stream().anyMatch(member -> member.getId() == u.getId());
                // Organizador del evento?
                boolean isOrganizer = e.getOrganizer() != null && e.getOrganizer().getId() == u.getId();
                // Tiene una reserva para el evento?
                boolean isAttendee = e.getAttendees() != null && e.getAttendees().stream().anyMatch(r -> r.getAttendee().getId() == u.getId());
                // Eventos privados, solo visibles si el usuario cumple alguna condicion
                // Si cumple cualquiera de las condiciones, el evento es visible
                if(isAdmin|| isMember || isOrganizer || isAttendee) {
                    visibleEvents.add(e);
                }
            }
        }

        // Filtrar eventos en mis eventos y otros eventos
        if(sessionUser != null) {
            // Usuario logueado: separar en dos listas
            long uid = sessionUser.getId();

            // Mis eventos: Soy el organizador o estoy en la lista de asistentes
            List<Event> myEvents = visibleEvents.stream()
                .filter(e -> (e.getOrganizer() != null && e.getOrganizer().getId() == uid) ||
                             (e.getAttendees() != null && e.getAttendees().stream().anyMatch(r -> r.getAttendee().getId() == uid)))      
                .toList();
                
            // Otros eventos
            List<Event> otherEvents = visibleEvents.stream()
                .filter(e -> !myEvents.contains(e))
                .toList();
            
            // Enviar ambas listas a la vista
            model.addAttribute("myEvents", myEvents);
            model.addAttribute("otherEvents", otherEvents);
        }
        else {
            // Usuario anonimo: todos los eventos van a otros eventos
            model.addAttribute("otherEvents", visibleEvents);
        }

        // Enviar todos los eventos visibles a la vista
        model.addAttribute("events", visibleEvents);

        // Enviar la API key de Google Maps al HTML
        model.addAttribute("googleMapsKey", googleMapsKey);
        
        return "event"; // Redirige a event.html
    }
    
    // Mostrar formulario crear evento
    @GetMapping("/create") 
    public String createEvent(Model model, HttpSession session, @RequestParam(required = false) Long communityId) {
        User sessionUser = (User) session.getAttribute("u");

        // Buscar a que comunidades pertenece el usuario
        List<es.ucm.fdi.iw.model.Community> myCommunities = entityManager.createQuery(
            "SELECT c FROM Community c JOIN c.members m WHERE m.id = :uid", es.ucm.fdi.iw.model.Community.class)
            .setParameter("uid", sessionUser.getId()).getResultList();

        model.addAttribute("event", new Event());
        model.addAttribute("myCommunities", myCommunities);
        model.addAttribute("preselectedCommunityId", communityId); // Para poder preseleccionar en el menu
        return "event/create";
    }

    // Guardar el evento en la base de datos
    @Transactional
    @PostMapping("/create")
    public String createEvent(
        @Valid @ModelAttribute Event event,
        BindingResult result,
        @RequestParam(required = false) Long communityId,
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

                // Comprobar formato imagen
                if(contentType == null ||  (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))){
                    // Mandar señal de error al HTML y recargar formulario
                    model.addAttribute("errorImage", true);
                    return "event/create";
                }

                // Restriccion de tamanyo
                long maxBytes = 5 * 1024 * 1024; // 5 MB
                if (photo.getSize() > maxBytes) {
                    model.addAttribute("errorSize", true);
                    return "event/create";
                }
            }

            // Obtener el usuario logueado de la sesion
            User organizer = (User) session.getAttribute("u");
            organizer = entityManager.find(User.class, organizer.getId());

            // Asignarlo como organizador
            event.setOrganizer(organizer);

            // Asignar el evento a una comunidad si se debe
            if (communityId != null) {
                es.ucm.fdi.iw.model.Community community = entityManager.find(es.ucm.fdi.iw.model.Community.class, communityId);
                event.setCommunity(community);
            }
            else {
                event.setPrivate(false); // Si no esta asociado a una comunidad, forzar a que el evento sea publico
            }

            // Guardar en la base de datos
            entityManager.persist(event);
            entityManager.flush();

            // Autoreserva para el organizador
            Reservation autoReservation = new Reservation();
            autoReservation.setAttendee(organizer);
            autoReservation.setEvent(event);

            // Si la lista de asistentes esta vacia, se inicia y se mete la reserva
            if (event.getAttendees() == null) {
                event.setAttendees(new java.util.ArrayList<>());
            }
            event.getAttendees().add(autoReservation);

            // Guardar la reserva en la base de datos
            entityManager.persist(autoReservation);
            entityManager.flush();

            // Guardar la imagen si el usuario ha subido alguna
            if(!photo.isEmpty()) {
                try {
                    // Obtener el archivo donde se guarda la imagen dentro de la carpeta 'events' en 'iwdata'
                    File f = localData.getFile("events", String.valueOf(event.getId()));

                    // Asegurar que la carpeta 'events' existe antes de guardar, si no, se crea
                    f.getParentFile().mkdirs();

                    try(BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
                        stream.write(photo.getBytes());
                    }

                    // Actualizar la imagen del evento con la ruta 
                    event.setImagePath("/event/" + event.getId() + "/pic");

                    // Forzar a la base de datos a guardar esta actualizacion
                    entityManager.merge(event);
                    entityManager.flush();
                    
                    // Al usar @Transactional, JPA guarda este cambio final en la base de datos automaticamente
                }
                catch (IOException e) {
                    log.error("Error al guardar la imagen del evento", e);
                }
            }

            log.info("Nuevo evento creado por {}: {}", organizer.getUsername(), event.getTitle());

            // Suscribir al creador al canal de su nuevo evento
            String topics = (String) session.getAttribute("topics");
            if(topics != null && !topics.isEmpty()) {
                session.setAttribute("topics", topics + ",event-" + event.getId());
            }
            else {
                session.setAttribute("topics", "event-" + event.getId());
            }
            return "redirect:/event";

    }

    // Muestra el formulario para editar un evento concreto.
    // Solo puede entrar el administrador o el organizador del evento.
    @GetMapping("/{id}/edit")
    public String editEvent(@PathVariable long id, Model model, HttpSession session) {

        // Buscar el evento en base de datos usando el id de la URL
        Event event = entityManager.find(Event.class, id);

        // Si el evento no existe, se redirige a la lista de eventos
        if (event == null) {
            return "redirect:/event";
        }

        // Obtener el usuario que esta intentando editar desde la sesion
        User requester = (User) session.getAttribute("u");

        // Si no hay usuario logueado, no tiene permisos
        if (requester == null) {
            throw new DontHavePermissionException();
        }

        // Recargar el usuario desde base de datos para tener sus roles actualizados
        requester = entityManager.find(User.class, requester.getId());

        // Comprobar si el usuario es administrador
        boolean isAdmin = requester.hasRole(User.Role.ADMIN);

        // Comprobar si el usuario es el organizador/creador del evento
        boolean isOrganizer = event.getOrganizer() != null 
                && event.getOrganizer().getId() == requester.getId();

        // Si no es admin ni organizador, se bloquea el acceso
        if (!isAdmin && !isOrganizer) {
            log.warn("El usuario {} ha intentado editar el evento {} sin permisos.",
                    requester.getUsername(), event.getTitle());
            throw new DontHavePermissionException();
        }

        // Enviar el evento a la vista para rellenar el formulario con sus datos actuales
        model.addAttribute("event", event);

        // Cargar la plantilla event/edit.html
        return "event/edit";
    }


    // Guarda los cambios realizados en el formulario de edicion.
    // Tambien comprueba permisos, validaciones e imagen nueva si se ha subido.
    @Transactional
    @PostMapping("/{id}/edit")
    public String updateEvent(
            @PathVariable long id,
            @Valid @ModelAttribute("event") Event editedEvent,
            BindingResult result,
            @RequestParam("photo") MultipartFile photo,
            Model model,
            HttpSession session) {

        // Buscar el evento original en base de datos
        Event event = entityManager.find(Event.class, id);

        // Si no existe, se vuelve a la lista de eventos
        if (event == null) {
            return "redirect:/event";
        }

        // Obtener el usuario que intenta guardar los cambios
        User requester = (User) session.getAttribute("u");

        // Si no hay sesión, no tiene permisos
        if (requester == null) {
            throw new DontHavePermissionException();
        }

        // Recargar el usuario desde base de datos
        requester = entityManager.find(User.class, requester.getId());

        // Comprobar si es administrador
        boolean isAdmin = requester.hasRole(User.Role.ADMIN);

        // Comprobar si es el organizador del evento
        boolean isOrganizer = event.getOrganizer() != null 
                && event.getOrganizer().getId() == requester.getId();

        // Si no tiene permisos, no puede guardar los cambios
        if (!isAdmin && !isOrganizer) {
            log.warn("El usuario {} ha intentado guardar cambios del evento {} sin permisos.",
                    requester.getUsername(), event.getTitle());
            throw new DontHavePermissionException();
        }

        // Calcular cuantas personas tienen ya plaza reservada
        int reservedSpots = event.getAttendees() != null ? event.getAttendees().size() : 0;

        // Evitar que el aforo editado sea menor que las plazas ya reservadas
        if (editedEvent.getCapacity() != null && editedEvent.getCapacity() < reservedSpots) {
            model.addAttribute("capacityError", true);

            // Mantener datos necesarios para que la vista no falle al volver al formulario
            editedEvent.setId(event.getId());
            editedEvent.setImagePath(event.getImagePath());

            model.addAttribute("event", editedEvent);
            return "event/edit";
        }

        // Si hay errores de validacion en los campos del evento, volver al formulario
        if (result.hasErrors()) {
            editedEvent.setId(event.getId());
            editedEvent.setImagePath(event.getImagePath());

            model.addAttribute("event", editedEvent);
            return "event/edit";
        }

        // Si el usuario ha subido una nueva imagen, validar formato y tamaño
        if (!photo.isEmpty()) {
            String contentType = photo.getContentType();

            // Solo se permiten imagenes JPEG o PNG
            if (contentType == null || 
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                model.addAttribute("errorImage", true);

                editedEvent.setId(event.getId());
                editedEvent.setImagePath(event.getImagePath());

                model.addAttribute("event", editedEvent);
                return "event/edit";
            }

            // Tamaño maximo permitido: 5 MB
            long maxBytes = 5 * 1024 * 1024;

            if (photo.getSize() > maxBytes) {
                model.addAttribute("errorSize", true);

                editedEvent.setId(event.getId());
                editedEvent.setImagePath(event.getImagePath());

                model.addAttribute("event", editedEvent);
                return "event/edit";
            }
        }

        // Enviar notificacion por websocket al canal del evento para avisar de que se ha hecho un cambio
        try {
            // Construir el JSON del mensaje usando Jackson
            ObjectMapper mapper = new ObjectMapper();
            // Nodo principal de JSON
            ObjectNode rootNode = mapper.createObjectNode();
            // Tipo de mensaje
            rootNode.put("type", "EVENT_EDIT");
            // Texto de la notificacion
            rootNode.put("text", "Se han hecho cambios en el evento " + event.getTitle());
            // Convertir JSON a texto
            String json = mapper.writeValueAsString(rootNode);

            // Publicar en el canal del evento
            messagingTemplate.convertAndSend("/topic/event-" + event.getId(), json);
            
        } catch (Exception e) {

        log.error("Error al publicar en el canal del evento", e );
        }

        // Actualizar solo los campos editables del evento.
        // No se cambia el organizador, la comunidad ni la lista de asistentes.
        event.setTitle(editedEvent.getTitle());
        event.setTheme(editedEvent.getTheme());
        event.setDate(editedEvent.getDate());
        event.setLocation(editedEvent.getLocation());
        event.setPrice(editedEvent.getPrice());
        event.setCapacity(editedEvent.getCapacity());
        event.setDescription(editedEvent.getDescription());

        // Si se ha subido una imagen nueva, sustituir la anterior
        if (!photo.isEmpty()) {
            try {
                // Obtener el archivo donde se guarda la imagen del evento
                File f = localData.getFile("events", String.valueOf(event.getId()));

                // Crear la carpeta si no existe
                f.getParentFile().mkdirs();

                // Guardar los bytes de la imagen nueva
                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
                    stream.write(photo.getBytes());
                }

                // Actualizar la ruta de imagen del evento
                event.setImagePath("/event/" + event.getId() + "/pic");
            }
            catch (IOException e) {
                log.error("Error al actualizar la imagen del evento", e);
            }
        }

        // Guardar los cambios en base de datos
        entityManager.merge(event);
        entityManager.flush();

        log.info("El usuario {} ha editado el evento {}", requester.getUsername(), event.getTitle());

        // Volver a la pagina de reserva del evento actualizado
        return "redirect:/reservation/" + event.getId();
    }

    // Borrar el evento en la base de datos
    @Transactional
    @PostMapping("/{id}/delete") 
    public String deleteEvent(@PathVariable long id, HttpSession session) {
        // Obtener el evento en la base de datos por su id
        Event event = entityManager.find(Event.class, id);

        if(event == null) {
            return "redirect:/event";
        }

        // Obtener el usuario que quiere realizar la accion
        User requester = (User) session.getAttribute("u");
        if(requester == null) {
            throw new DontHavePermissionException(); // Si no esta logueado
        }

        // Comprobar permisos, ¿es admin o el creador del evento?
        boolean isAdmin = requester.hasRole(User.Role.ADMIN);
        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().getId() == requester.getId();

        if(!isAdmin && !isOrganizer)
        {
            log.warn("El usuario {} ha intentado eliminar el evento {} sin tener permisos.", requester.getUsername(), event.getTitle());
            throw new DontHavePermissionException();
        }

        // Eliminar de la base de datos si tiene permisos
        entityManager.remove(event);
        log.info("El usuario {} ha borrado el evento: {}", requester.getUsername(), event.getTitle());
        

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

    // Endpoint para proporcionar la imagen del evento desde la carpeta externa iwdata
    @GetMapping("/{id}/pic")
    @ResponseBody
    public void getEventPhoto(@PathVariable long id, HttpServletResponse response) throws IOException {
        File f = localData.getFile("events", String.valueOf(id));
        if (f.exists() && f.canRead()) {
            // Si hay foto subida por el usuario, se proporciona al navegador
            response.setContentType("image/jpeg");
            Files.copy(f.toPath(), response.getOutputStream());
        } else {
            // Si no existe, se envia la imagen de por defecto
            response.sendRedirect("/img/events/default.jpg");
        }

    }
}
