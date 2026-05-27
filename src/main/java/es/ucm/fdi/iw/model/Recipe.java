package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Clase que representa una receta en la base de datos.
 */
@Entity
@Data
@NoArgsConstructor
public class Recipe implements Transferable<Recipe.Transfer> {

    // Son las variables simples de la receta.
    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
	@SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    //Titulo de la receta
    @Column(nullable = false)
    private String title;

    //Tiempo de preparacion de la receta
    @Column(nullable = false)
    private String time;

    //Dificultad de la receta
    @Column(nullable = false)
    private String difficulty;

    //Calorias que tiene la receta
    @Column(nullable = false)
    private String calories;

    //Indica si la receta es publica o privada, es decir, si se puede ver por otros usuarios o no.
    @Column(nullable = false)
    private boolean publicRecipe;

    //Indica si la receta ya a sido evaluada o no (esto es para el calculo de la media de valoraciones)
    @Column(nullable = false)
    private boolean hasRating;

    //Media de las valoraciones a la receta
    @Column(nullable = false)
    private float averageRating;

    //Conjunto de pasos para hacer la receta
    @Column(nullable = true)
    private String[] steps;

    // Conexiones entre las distintas tablas de la base de datos.

    //Conexion con los ingredientes usados en las recetas
    @OneToMany(mappedBy = "recipeUsed", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IngredientInRecipe> recipeIngredients = new ArrayList<>();

    //Conexion con las comunidades en las que estan las recetas
    @ManyToMany(mappedBy = "recipes")
    @JsonIgnore
    private List<Community> communities = new ArrayList<>();

    @ManyToMany(targetEntity = Event.class, mappedBy = "recipes")
	private List<Event> events = new ArrayList<>();

    //Usuario creador de la receta
    @ManyToOne
    private User author;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private String title;
        private String time;
        private String difficulty;
        private String calories;
        private boolean publicRecipe;
        private boolean hasRating;
        private float averageRating;
        private String[] steps;
        private String ingredients;
        long id;
    }

    @Override
    public Transfer toTransfer() {

        StringBuilder ingr = new StringBuilder();

        for (IngredientInRecipe i : recipeIngredients) {
            ingr.append(i.getIngredientUsed().getName()).append(", ");
        } 

        return new Transfer(title, time, difficulty, calories, publicRecipe, hasRating, averageRating, steps, ingr.toString(), id);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

    //Saca los alergenos de la receta al revisar los ingredientes que tiene esta
    public Set<String> getAllergens() {
        Set<String> allergens = new HashSet<>();

        for (IngredientInRecipe ri : recipeIngredients) {
            allergens.addAll(Arrays.asList(ri.getIngredientUsed().getAllergens()));
        }

        return allergens;
    }

    //Permite añadir una valoracion a la receta y que esta se calcule y afecte a la media de valoraciones de esta
    public void addRating(float rating){
        if(hasRating){
            averageRating = (averageRating + rating) / 2;
        }
        else{
            averageRating = rating;
            hasRating = true;
        }
    }

}
