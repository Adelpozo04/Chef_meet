package main.java.es.ucm.fdi.iw.model;

import es.ucm.fdi.iw.model.Topic;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe implements Transferable<Recipe.Transfer> {

    // Son las variables simples de la receta.
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
    // @ManyToMany(mappedBy = "recipes")
    // private List<Ingredient> ingredients = new ArrayList<>();
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
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(title, time, difficulty, steps, calories);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

}
