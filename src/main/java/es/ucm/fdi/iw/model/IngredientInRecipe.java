package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Clase que representa la relación entre un ingrediente y una receta en la base de datos. Se crea debido a que cada receta se establece una cantidad de un ingrediente especifica
 */
@Entity
@Data
@NoArgsConstructor
public class IngredientInRecipe implements Transferable<IngredientInRecipe.Transfer>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    //Cantidad de los ingredientes que hay en una receta
    @Column(nullable = false)
    private String quantity;

    // Conexiones entre las distintas tablas de la base de datos.

    //Conexion entre la receta a la que pertenece y el ingrediente que representa. La relacion es de ManyToOne ya que cada receta establece cantidades distintas que diferencian a estos, por lo que no se pueden agrupar en un ManyToMany
    @ManyToOne
    private Recipe recipeUsed;
    @ManyToOne
    private Ingredient ingredientUsed;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String quantity;
    }
    
    @Override
    public Transfer toTransfer() {

        return new Transfer(id, quantity);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

}
