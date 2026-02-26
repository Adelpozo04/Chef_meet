
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
public class Ingredient implements Transferable<Ingredient.Transfer>  {
    
    // Son las variables simples de la receta.

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String[] allergens;

    // Conexiones entre las distintas tablas de la base de datos.
    @ManyToMany(mappedBy = "ingredients")
    private List<Recipe> recipes = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private String name;
        private long id;
        private String[] allergens;
        private String recipes;
    }
    
    @Override
    public Transfer toTransfer() {

        StringBuilder recips = new StringBuilder();

        for (Recipe r : recipes) {
            recips.append(r.getTitle()).append(", ");
        } 

        return new Transfer(name, id, allergens, recips.toString());
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }
}

