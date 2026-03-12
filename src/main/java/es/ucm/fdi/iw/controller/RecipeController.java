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
    public String createCommunity(
            @ModelAttribute Recipe recipe,
            @ModelAttribute User edited,
            Model model,
            HttpSession session) {

        if (recipe.getTitle().isBlank() || recipe.getDifficulty().isBlank() || 
            recipe.getTime().isBlank() || recipe.getSteps().length == 0) {
            model.addAttribute("createError", true);
            log.info("ERROR AL INTENTAR CREAR RECETA");
            return "communities/create";
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

        log.info("New recipe created by: {}", recipe.getAuthor().getUsername());
        log.info("New recipe created with title: {}", recipe.getTitle());
        log.info("New recipe created with description: {}", recipe.getSteps()[0]);
        return "redirect:/recipe";
        
    }

   /**
   * Uploads a profile pic for a user id
   * 
   * @param id
   * @return
   * @throws IOException
   */
  @PostMapping("{id}/pic")
  @ResponseBody
  public String setPic(@RequestParam(value="cover", required=false) MultipartFile cover,
                      @RequestParam Map<String, MultipartFile> stepPhotos,
                      @PathVariable long id,
                      HttpServletResponse response, 
                      HttpSession session, Model model) throws IOException {

    User target = entityManager.find(User.class, id);
    model.addAttribute("user", target);

    // check permissions
    User requester = (User) session.getAttribute("u");
    if (requester.getId() != target.getId() &&
        !requester.hasRole(Role.ADMIN)) {
      throw new NoEsTuPerfilException();
    }

    log.info("Updating photo for recipe {}", id);

    File f = localData.getFile("recipe", "" + id + "_cover.jpg");

    if (cover.isEmpty()) {
      log.info("failed to upload photo: emtpy file?");
    } else {
      try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
        byte[] bytes = cover.getBytes();
        stream.write(bytes);
        log.info("Uploaded photo for {} into {}!", id, f.getAbsolutePath());
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.warn("Error uploading " + id + " ", e);
      }
    }

    for(int i = 0; i < stepPhotos.size(); i++){

        f = localData.getFile("recipe", "" + id + "_step" + i + ".jpg");

        if (stepPhotos.get("step" + i).isEmpty()) {
        log.info("failed to upload photo: emtpy file?");
        } else {
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
            byte[] bytes = stepPhotos.get("step" + i).getBytes();
            stream.write(bytes);
            log.info("Uploaded photo for {} into {}!", id, f.getAbsolutePath());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.warn("Error uploading " + id + " ", e);
        }
        }

    }

    return "{\"status\":\"photo uploaded correctly\"}";
  }
    
}
