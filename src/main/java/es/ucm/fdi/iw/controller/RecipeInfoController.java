package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.ucm.fdi.iw.model.Event;
import es.ucm.fdi.iw.model.Recipe;
import es.ucm.fdi.iw.model.Reserve;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class RecipeInfoController {
    
    @Autowired
    private EntityManager entityManager;

    @GetMapping("/recipeInfo/{id}")
    public String showRecipeInfoPage(@PathVariable long id, Model model) {
        // Buscar la info de la receta en la base de datos usando el id que viene en la url
        Recipe recipe = entityManager.find(Recipe.class, id);

        // Si el id no existe, se redirige a eventos
        if (recipe == null) {
            return "redirect:/recipe";
        }

        model.addAttribute("recipe", recipe);

        return "recipeInfo";
    }

}
