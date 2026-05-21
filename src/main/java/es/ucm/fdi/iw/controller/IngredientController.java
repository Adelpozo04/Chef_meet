package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Ingredient;
import jakarta.persistence.EntityManager;

import java.util.List;

//Controlador de los ingredientes, se usa principalmente en la pagina de crear receta para la carga de la base de datos en el dropdown
@Controller
@RequestMapping("/recipe")
public class IngredientController {

    private static final Logger log = LogManager.getLogger(IngredientController.class);

    @Autowired
    private EntityManager entityManager;

    //Devuelve toda la base de datos de los ingredientes
    @GetMapping("/ingredients")
    @ResponseBody
    public List<Ingredient> getIngredients() {
        return entityManager
                .createQuery("SELECT i FROM Ingredient i", Ingredient.class)
                .getResultList();
    }

}