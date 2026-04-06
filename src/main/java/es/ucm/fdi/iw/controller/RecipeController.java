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
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
    
    private static final Logger log = LogManager.getLogger(RecipeController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LocalData localData;

    //Cuando se pulse el boton de crear receta se dirige a la pagina para rellenar el formulario con los datos de esta
    @GetMapping("/create")
    public String createRecipe(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "recipe/create";
    }

    @Transactional
    @PostMapping("/create")
    public String createRecipe(
            @ModelAttribute Recipe recipe,
            @ModelAttribute User edited,
            @RequestParam Map<String, MultipartFile> allParams,
            @RequestParam List<Long> ingredientIds,
            @RequestParam List<String> quantities,
            Model model,
            HttpSession session) {

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

        // Set community owner and add it as member
        recipe.setAuthor(author);
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

    @GetMapping("/addToCommunity/{id}")
    public String showAddToCommunityPage(@PathVariable long id, Model model, HttpSession session) {
        // Buscar la info de la receta en la base de datos usando el id que viene en la url
        Recipe recipe = entityManager.find(Recipe.class, id);

        // Si el id no existe, se redirige a eventos
        if (recipe == null) {
            return "redirect:/recipe";
        }

        User sessionUser = (User) session.getAttribute("u");

        User user = entityManager.find(User.class, sessionUser.getId());

        List<Community> communities = new ArrayList<>();

        communities.addAll(user.getJoinedCommunities());
        communities.addAll(user.getOwnedCommunities());

        model.addAttribute("recipe", recipe);
        model.addAttribute("communities", communities);

        return "recipe/addToCommunity";
    }

    @PostMapping("/addToCommunity")
    public String addRecipe(@RequestParam Long communityId,
                            @RequestParam Long recipeId){

        Community community = entityManager.find(Community.class, communityId);
        Recipe recipe = entityManager.find(Recipe.class, recipeId);

        community.getRecipes().add(recipe);
        recipe.getCommunities().add(community);

        entityManager.persist(community);
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

    // Borrar el evento en la base de datos
    @Transactional
    @PostMapping("/{id}/delete") 
    public String deleteEvent(@PathVariable long id) {
        // Buscar el evento en la base de datos pot su id
        Recipe recipe = entityManager.find(Recipe.class, id);

        if(recipe != null) {
            // Eliminar de la base de datos
            entityManager.remove(recipe);
            log.info("El administrador ha borrado la receta: {}", recipe.getTitle());
        }

        return "redirect:/recipe";
    } 
    
}
