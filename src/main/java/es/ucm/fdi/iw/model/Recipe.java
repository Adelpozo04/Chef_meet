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

@Entity
@Data
@NoArgsConstructor
public class Recipe implements Transferable<Recipe.Transfer> {

    // Son las variables simples de la receta.
    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
	@SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    private String calories;

    @Column(nullable = false)
    private boolean publicRecipe;

    @Column(nullable = false)
    private String[] steps;

    // Conexiones entre las distintas tablas de la base de datos.
    @OneToMany(mappedBy = "recipeUsed", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IngredientInRecipe> recipeIngredients = new ArrayList<>();

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

        return new Transfer(title, time, difficulty, calories, publicRecipe, steps, ingr.toString(), id);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

    public Set<String> getAllergens() {
    Set<String> allergens = new HashSet<>();

    for (IngredientInRecipe ri : recipeIngredients) {
        allergens.addAll(Arrays.asList(ri.getIngredientUsed().getAllergens()));
    }

    return allergens;
}

}
