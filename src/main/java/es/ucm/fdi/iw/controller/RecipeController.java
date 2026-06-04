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
import es.ucm.fdi.iw.model.Ingredient;
import es.ucm.fdi.iw.model.IngredientInRecipe;
import es.ucm.fdi.iw.model.Recipe;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


import es.ucm.fdi.iw.model.Event;

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

    
    //Una vez que se cree la receta se maneja su creacion en la base de datos con este metodo, se le asigna un autor, se crean los IngredientInRecipe necesarios para la receta y se suben las fotos que se hayan incluido
    @Transactional
    @PostMapping("/create")
    public String createRecipe(
            @ModelAttribute Recipe recipe,
            @ModelAttribute User edited,
            @RequestParam Map<String, MultipartFile> allParams, //Esto debe llamarse si o si allParams para que tome todos los parametros independientemente del nombre de estos y poder hacer bien la criba de steps y las imagenes
            @RequestParam(value = "ingredientIds", required = false) List<Long> ingredientIds,
            @RequestParam(value = "quantities", required = false) List<String> quantities,
            Model model,
            HttpSession session) {

                
        //Se revisa que esta tenga los campos necesarios rellenados, si no es asi se muestra un mensaje de error y se vuelve a la pagina de crear receta
        if (recipe.getTitle().isBlank() || recipe.getDifficulty().isBlank() || 
            recipe.getTime().isBlank() || recipe.getCalories().isBlank()){
            model.addAttribute("createError", true);
            log.info("ERROR AL INTENTAR CREAR RECETA");
            return "recipe/create";
        }

        
        // Comprobar que se ha añadido al menos un ingrediente
        if (ingredientIds == null || ingredientIds.isEmpty() || quantities == null || quantities.isEmpty()) {

            model.addAttribute("ingredientError", true);
            log.info("ERROR AL INTENTAR CREAR RECETA: no se han añadido ingredientes");
            return "recipe/create";
        }

        // Comprobar que se ha añadido al menos un paso
        if (recipe.getSteps() == null || recipe.getSteps().length == 0) {

            model.addAttribute("stepError", true);
            log.info("ERROR AL INTENTAR CREAR RECETA: no se han añadido pasos");
            return "recipe/create";
        }

        // Comprobar que los pasos no esten vacios
        for (String step : recipe.getSteps()) {
            if (step == null || step.isBlank()) {
                model.addAttribute("stepError", true);
                log.info("ERROR AL INTENTAR CREAR RECETA: hay pasos vacíos");
                return "recipe/create";
            }
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

    //Metodo para añadir una receta a una comunidad o a un evento
    @GetMapping("/addToCommunity/{id}")
    public String showAddToCommunityPage(@PathVariable long id, @RequestParam(required = false) String error, Model model, HttpSession session) {
        // Buscar la info de la receta en la base de datos usando el id que viene en la url
        Recipe recipe = entityManager.find(Recipe.class, id);

        // Si el id no existe, se redirige a recetas
        if (recipe == null) {
            return "redirect:/recipe";
        }

        // Sacamos las comunidades a las que pertenece el usuario para mostrarlas en un dropdown y que pueda elegir a cual añadir la receta
        User sessionUser = (User) session.getAttribute("u");

        User user = entityManager.find(User.class, sessionUser.getId());

        List<Community> communities = new ArrayList<>();

        communities.addAll(user.getJoinedCommunities());
        communities.addAll(user.getOwnedCommunities());

        List<Event> events = entityManager.createQuery("SELECT e FROM Event e WHERE e.organizer.id = :uid", Event.class).
                                setParameter("uid", user.getId())
                            .getResultList();
        model.addAttribute("recipe", recipe);
        model.addAttribute("communities", communities);
        model.addAttribute("events", events);

        
        if ("private".equals(error)) {
            model.addAttribute("errorPrivate", true);
        }
        if ("maxRecipes".equals(error)) {
            model.addAttribute("errorMaxRecipes", true);
        }

        return "recipe/addToCommunity";
    }

    
    //Una vez que se ha elegido la comunidad y se ha pulsado que se quiere añadir receta
    @PostMapping("/addToCommunity")
    @Transactional
    public String addRecipe(@RequestParam Long communityId,
                            @RequestParam Long recipeId,
                            HttpSession session){

        //Tomamos tanto la comunidad como la receta que se quieren relacionar
        Community community = entityManager.find(Community.class, communityId);
        Recipe recipe = entityManager.find(Recipe.class, recipeId);

        if (community == null || recipe == null) {
            return "redirect:/recipe";
        }

        User sessionUser = (User) session.getAttribute("u");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = entityManager.find(User.class, sessionUser.getId());
        
        // Solo se puede añadir una receta a una comunidad si la receta es publica.
        if (!recipe.isPublicRecipe()) {
            log.warn("El usuario {} ha intentado añadir una receta privada ({}) a la comunidad {}",
                    user.getUsername(), recipe.getTitle(), community.getTitle());

            return "redirect:/recipe/addToCommunity/" + recipeId + "?error=private";
        }

        // Comprobar que el usuario pertenece a la comunidad o es su creador/admin.
        boolean isMember = community.getMembers().stream()
                .anyMatch(member -> member.getId() == user.getId());

        boolean isOwner = community.getOwner() != null
                && community.getOwner().getId() == user.getId();

        boolean isAdmin = user.hasRole(User.Role.ADMIN);

        if (!isMember && !isOwner && !isAdmin) {
            log.warn("El usuario {} ha intentado añadir una receta a una comunidad a la que no pertenece",
                    user.getUsername());

            return "redirect:/recipe";
        }

        // Evitar duplicados
        boolean alreadyAdded = community.getRecipes().stream()
                .anyMatch(r -> r.getId() == recipe.getId());

        if (!alreadyAdded) {
            community.getRecipes().add(recipe);
            recipe.getCommunities().add(community);
        }

        entityManager.persist(community);
        entityManager.persist(recipe);
        entityManager.flush();

        return "redirect:/recipe";
    }

    // Añade una receta publica a un evento creado por el usuario
    // Solo se permite si la receta es publica y el usuario es el organizador del evento
    @PostMapping("/addToEvent")
    @Transactional
    public String addRecipeToEvent(@RequestParam Long eventId,
                                @RequestParam Long recipeId,
                                HttpSession session) {

        Event event = entityManager.find(Event.class, eventId);
        Recipe recipe = entityManager.find(Recipe.class, recipeId);

        if (event == null || recipe == null) {
            return "redirect:/recipe";
        }

        User sessionUser = (User) session.getAttribute("u");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = entityManager.find(User.class, sessionUser.getId());

        // La receta debe ser publica para poder vincularse a un evento
        if (!recipe.isPublicRecipe()) {
            log.warn("El usuario {} ha intentado añadir una receta privada ({}) al evento {}",
                    user.getUsername(), recipe.getTitle(), event.getTitle());

            return "redirect:/recipe/addToCommunity/" + recipeId + "?error=private";
        }

        // Solo el creador del evento o un admin puede añadir recetas al evento
        boolean isOrganizer = event.getOrganizer() != null
                && event.getOrganizer().getId() == user.getId();

        boolean isAdmin = user.hasRole(User.Role.ADMIN);

        if (!isOrganizer && !isAdmin) {
            log.warn("El usuario {} ha intentado añadir una receta al evento {} sin ser organizador",
                    user.getUsername(), event.getTitle());

            return "redirect:/recipe";
        }

        // Limite maximo de recetas asociadas a un evento
        int maxRecipesPerEvent = 10;

        // Evitar duplicados
        boolean alreadyAdded = event.getRecipes().stream()
                .anyMatch(r -> r.getId() == recipe.getId());

        if (!alreadyAdded && event.getRecipes().size() >= maxRecipesPerEvent) {
            log.warn("El usuario {} ha intentado añadir una receta al evento {}, pero ya tiene el máximo de {} recetas.",
                    user.getUsername(), event.getTitle(), maxRecipesPerEvent);

            return "redirect:/recipe/addToCommunity/" + recipeId + "?error=maxRecipes";
        }

        if (!alreadyAdded) {
            event.getRecipes().add(recipe);
        }

        entityManager.merge(event);
        entityManager.flush();

        return "redirect:/reservation/" + event.getId();
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
                    //File f = localData.getFile("../src/main/resources/static/img/recipes", "" + id + "_" + entry.getKey() + ".jpg");

                    File f = localData.getFile("recipes", id + "_" + entry.getKey() + ".jpg");


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


    // Endpoint para proporcionar la imagen del evento desde la carpeta externa iwdata
    @GetMapping("/{id}/pic/{imageName}")
    @ResponseBody
    public void getRecipePhoto(@PathVariable long id, @PathVariable String imageName, HttpServletResponse response) throws IOException {

        File f = localData.getFile("recipes", id + "_" + imageName + ".jpg");

        if (f.exists() && f.canRead()) {
            // Si hay foto subida por el usuario, se proporciona al navegador
            response.setContentType("image/jpeg");
            Files.copy(f.toPath(), response.getOutputStream());
        } else {
            // Si no existe, se envia la imagen de por defecto
            response.sendRedirect("/img/recipes/default.jpg");
        }

    }
    
}
