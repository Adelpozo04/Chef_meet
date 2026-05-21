
package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Clase que representa un ingrediente en la base de datos.
 */
@Entity
@Data
@NoArgsConstructor
public class Ingredient implements Transferable<Ingredient.Transfer>  {
    
    // Son las variables simples de la receta.

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    //Nombre del ingrediente
    @Column(nullable = false)
    private String name;

    //Lista de alergenos que tiene el ingrediente
    @Column(nullable = false)
    private String[] allergens;

    // Conexiones entre las distintas tablas de la base de datos.

    //Recetas que poseen este ingrediente, como hay un paso medio que es ingrediente en receta no es un ManyToMany sino un OneToMany ya que cada receta establece cantidades distintas que diferencian a estos
    @OneToMany(mappedBy = "ingredientUsed", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IngredientInRecipe> ingredientsInRecipes = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private String name;
        private long id;
        private String[] allergens;
        private String ingredientsInRecipes;
    }
    
    @Override
    public Transfer toTransfer() {

        StringBuilder recips = new StringBuilder();

        for (IngredientInRecipe r : ingredientsInRecipes) {
            recips.append(r.getRecipeUsed().getTitle()).append(", ");
        } 

        return new Transfer(name, id, allergens, recips.toString());
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }
}

