package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;


@Entity
@Data
@NoArgsConstructor
public class IngredientInRecipe implements Transferable<IngredientInRecipe.Transfer>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false)
    private String quantity;

    // Conexiones entre las distintas tablas de la base de datos.
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
