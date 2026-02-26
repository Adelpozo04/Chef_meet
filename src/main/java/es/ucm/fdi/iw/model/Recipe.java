package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    private String[] steps;

    private float calories;

    // Conexiones entre las distintas tablas de la base de datos.
    @ManyToMany
    private List<Ingredient> ingredients = new ArrayList<>();

    @ManyToOne
    private User author;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private String title;
        private String time;
        private String difficulty;
        private String[] steps;
        private float calories;
        private String ingredients;
        long id;
    }

    @Override
    public Transfer toTransfer() {

        StringBuilder ingr = new StringBuilder();

        for (Ingredient i : ingredients) {
            ingr.append(i.getName()).append(", ");
        } 

        return new Transfer(title, time, difficulty, steps, calories, ingr.toString(), id);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

}
