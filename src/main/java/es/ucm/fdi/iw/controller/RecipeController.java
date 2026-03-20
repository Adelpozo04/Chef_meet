package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.model.User.Role;

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
import es.ucm.fdi.iw.controller.UserController.NoEsTuPerfilException;
import es.ucm.fdi.iw.model.Recipe;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.*;
import java.util.Map;

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
            Model model,
            HttpSession session) {

        if (recipe.getTitle().isBlank() || recipe.getDifficulty().isBlank() || 
            recipe.getTime().isBlank()) {
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

        try{
            setPic(allParams, recipe.getId(), null, session, model);
        }
        catch(IOException e){
            log.warn("Error uploading photo for recipe {} ", recipe.getId(), e);
        }

        log.info("New recipe created by: {}", recipe.getAuthor().getUsername());
        log.info("New recipe created with title: {}", recipe.getTitle());
        log.info("New recipe created with description: {}", recipe.getSteps()[0]);
        return "recipe";
        
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
                    File f = localData.getFile("recipe", "" + id + "_" + entry.getKey() + ".jpg");

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
    
}
