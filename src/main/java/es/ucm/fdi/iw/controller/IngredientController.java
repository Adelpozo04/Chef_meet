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
import es.ucm.fdi.iw.model.Ingredient;
import es.ucm.fdi.iw.model.Recipe;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.*;
import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/recipe")
public class IngredientController {

    private static final Logger log = LogManager.getLogger(IngredientController.class);

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/ingredients")
    @ResponseBody
    public List<Ingredient> getIngredients() {
        return entityManager
                .createQuery("SELECT i FROM Ingredient i", Ingredient.class)
                .getResultList();
    }

}