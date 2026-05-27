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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Ingredient;
import es.ucm.fdi.iw.model.IngredientInRecipe;
import es.ucm.fdi.iw.model.Recipe;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

//Controlador de las recetas, maneja la creación, eliminación, carga y valoración de estas, así como su adición a comunidades y la subida de fotos para las recetas
@Controller
@RequestMapping("/recipe")
public class RecipeController {
    
    private static final Logger log = LogManager.getLogger(RecipeController.class);

    @Autowired
    private EntityManager entityManager;

    //Usado para el acceso a la ruta donde se almacenan las fotos de las recetas
    @Autowired
    private LocalData localData;

    //Cuando se pulse el boton de crear receta se dirige a la pagina para rellenar el formulario con los datos de esta
    @GetMapping("/create")
    public String createRecipe(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "recipe/create";
    }

    //Una vez que se cree la receta se maneja su creación en la base de datos con este metodo, se le asigna un autor, se crean los IngredientInRecipe necesarios para la receta y se suben las fotos que se hayan incluido
    @Transactional
    @PostMapping("/create")
    public String createRecipe(
            @ModelAttribute Recipe recipe,
            @ModelAttribute User edited,
            @RequestParam Map<String, MultipartFile> allParams, //Esto debe llamarse si o si allParams para que tome todos los parametros independientemente del nombre de estos y poder hacer bien la criba de steps y las imagenes
            @RequestParam List<Long> ingredientIds,
            @RequestParam List<String> quantities,
            Model model,
            HttpSession session) {

        //Se revisa que esta tenga los campos necesarios rellenados, si no es asi se muestra un mensaje de error y se vuelve a la pagina de crear receta
        if (recipe.getTitle().isBlank() || recipe.getDifficulty().isBlank() || 
            recipe.getTime().isBlank() || recipe.getCalories().isBlank()){
            model.addAttribute("createError", true);
            log.info("ERROR AL INTENTAR CREAR RECETA");
            return "recipe/create";
        }

        model.addAttribute("createError", false);

        // Usuario logueado que ejecuta esta query -> creador de la receta
        User author = (User) session.getAttribute("u");
        author = entityManager.find(User.class, author.getId());
        if (recipe.getAuthor() == null)
            author.getRecipes().add(recipe);

        //Se añaden los datos a la propia recta
        recipe.setAuthor(author);
        recipe.setHasRating(false);
        recipe.setAverageRating(0);
        entityManager.persist(recipe);
        entityManager.flush();

        //Nos creamos una lista de ingredientes usados en la receta
        List<IngredientInRecipe> list = new ArrayList<>();

        //Iteramos sobre los ingredientes y la cantidad de cada uno para crear los IngredientInRecipe necesarios para la receta
        for (int i = 0; i < ingredientIds.size(); i++) {

            //Sacamos el ingrediente segun el id
            Ingredient ing = entityManager.find(Ingredient.class, ingredientIds.get(i));

            //Nos creamos un ingrediente de receta
            IngredientInRecipe ri = new IngredientInRecipe();

            //Le asignamos sus valores
            ri.setRecipeUsed(recipe);
            ri.setIngredientUsed(ing);
            ri.setQuantity(quantities.get(i));

            //Lo anyadimos a la lista
            list.add(ri);
        }

        //Le pasamos la lista a la receta
        recipe.setRecipeIngredients(list);

        //Añadimos la imagen a la receta
        try{
            setPic(allParams, recipe.getId(), null, session, model);
        }
        catch(IOException e){
            log.warn("Error uploading photo for recipe {} ", recipe.getId(), e);
        }

        log.info("New recipe created by: {}", recipe.getAuthor().getUsername());
        log.info("New recipe created with title: {}", recipe.getTitle());
        log.info("New recipe created with description: {}", recipe.getSteps()[0]);
        return "redirect:/recipe";
        
    }

    //Metodo para añadir una receta a la comunidad
    @GetMapping("/addToCommunity/{id}")
    public String showAddToCommunityPage(@PathVariable long id, Model model, HttpSession session) {
        // Buscar la info de la receta en la base de datos usando el id que viene en la url
        Recipe recipe = entityManager.find(Recipe.class, id);

        // Si el id no existe, se redirige a eventos
        if (recipe == null) {
            return "redirect:/recipe";
        }

        // Sacamos las comunidades a las que pertenece el usuario para mostrarlas en un dropdown y que pueda elegir a cual añadir la receta
        User sessionUser = (User) session.getAttribute("u");

        User user = entityManager.find(User.class, sessionUser.getId());

        List<Community> communities = new ArrayList<>();

        communities.addAll(user.getJoinedCommunities());
        communities.addAll(user.getOwnedCommunities());

        // Se piden todos los eventos a la base de datos
        List<Event> allEvents = entityManager.createQuery("SELECT e FROM Event e", Event.class).getResultList();

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
                boolean isMember = e.getCommunity() != null && e.getCommunity().getMembers().contains(u);
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

        // Mis eventos: Soy el organizador o estoy en la lista de asistentes
        List<Event> myEvents = visibleEvents.stream()
            .filter(e -> (e.getOrganizer() != null && e.getOrganizer().getId() == user.getId()) ||
                            (e.getAttendees() != null && e.getAttendees().stream().anyMatch(r -> r.getAttendee().getId() == user.getId())))      
            .toList();

        model.addAttribute("recipe", recipe);
        model.addAttribute("communities", communities);
        model.addAttribute("events", myEvents);

        return "recipe/addToCommunity";
    }

    //Una vez que se ha elegido la comunidad y se ha pulsado que se quiere añadir receta
    @PostMapping("/addToCommunity")
    @Transactional
    public String addRecipe(@RequestParam Long communityId,
                            @RequestParam Long eventId,
                            @RequestParam Long recipeId){

        Recipe recipe = entityManager.find(Recipe.class, recipeId);

        if(communityId != -1){
            //Tomamos tanto la comunidad como la receta que se quieren relacionar
            Community community = entityManager.find(Community.class, communityId);

            community.getRecipes().add(recipe);
            recipe.getCommunities().add(community);

            entityManager.persist(community);
        }
        
        if(eventId != -1){
            Event event = entityManager.find(Event.class, eventId);

            event.getRecipes().add(recipe);
            recipe.getEvents().add(event);

            entityManager.persist(event);
        }

        //Se añade mutuamente      
        entityManager.persist(recipe);
        entityManager.flush();

        return "redirect:/recipe";
    }

    //Metodo para cargar las cosas en la pestaña de valoracion
    @GetMapping("/addRating/{id}")
    public String showAddRatingPage(@PathVariable long id, Model model){

        //Se carga la receta que se quiere valorar para mostrar su información en la pestaña de valoración y que el usuario sepa a que receta le esta añadiendo la valoración
        Recipe recipe = entityManager.find(Recipe.class, id);

        model.addAttribute("recipe", recipe);

        return "recipe/addRating";
    }

    //Una vez se valore la receta se envia el rating con el metodo especifico y el valor que le ha dado el usuario
    @PostMapping("/addRating/{id}")
    @Transactional
    public String addRating(@PathVariable long id, @RequestParam float rating){
        
        Recipe recipe = entityManager.find(Recipe.class, id);
        recipe.addRating(rating);

        entityManager.persist(recipe);
        entityManager.flush();

        return "redirect:/recipe";
    }

    // Cargar recetas
    @GetMapping({"", "/"})
    public String showRecipes(Model model) {
        // Se piden todas las recetas a la base de datos
        List<Recipe> recipes = entityManager.createQuery("SELECT r FROM Recipe r", Recipe.class).getResultList();
        model.addAttribute("recipes", recipes);
        return "recipe"; // Redirige a recipe.html
    }

   /**
   * Uploads a profile pic for a user id
   * 
   * @param cover: Imagen principal de la receta
   * @param stepPhotos: map con las imagenes de las fotografias y la key que representa el paso al que pertenecen
   * 
   */
  @PostMapping("{id}/pic")
  @ResponseBody
  public String setPic(@RequestParam Map<String, MultipartFile> allParams,
                      @PathVariable long id,
                      HttpServletResponse response, 
                      HttpSession session, Model model) throws IOException {

    

        log.info("Updating photo for recipe {}", id);

        

        //Si no se ha enviado el cover sacamos mensaje de error
        if (allParams.isEmpty()) {

            log.info("failed to upload photo: emtpy file?");

        } else {

            for (Map.Entry<String, MultipartFile> entry : allParams.entrySet()){

                if(entry.getKey().equals("cover") || entry.getKey().startsWith("step")){
                    //Nos creamos la ruta en la que se va a guardar la fotografia
                    File f = localData.getFile("../src/main/resources/static/img/recipes", "" + id + "_" + entry.getKey() + ".jpg");

                    if (allParams.get(entry.getKey()).isEmpty()) {
                        log.info("failed to upload photo: emtpy file?");
                    } 
                    else {
                        //Sacamos el stream de la ruta que hemos indicado donde se va a almacenar la fotografia
                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {

                            //Nos almacenamos los bytes de la imagen que se nos ha pasado
                            byte[] bytes = entry.getValue().getBytes();

                            //Escribimos los bytes en el fichero cuya ruta hemos indicado anteriormente
                            stream.write(bytes);

                            log.info("Uploaded photo for {} into {}!", id, f.getAbsolutePath());

                        } catch (Exception e) {

                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                            log.warn("Error uploading " + id + " ", e);
                        }
                    }

                }
                
            }

        }

        return "{\"status\":\"photo uploaded correctly\"}";
  
    }

    // Borrar la receta en la base de datos
    @Transactional
    @PostMapping("/{id}/delete") 
    public String deleteRecipe(@PathVariable long id) {
        // Buscar la receta en la base de datos por su id
        Recipe recipe = entityManager.find(Recipe.class, id);

        if(recipe != null) {
            // Eliminar de la base de datos
            entityManager.remove(recipe);
            log.info("El administrador ha borrado la receta: {}", recipe.getTitle());
        }

        return "redirect:/recipe";
    } 
    
}
