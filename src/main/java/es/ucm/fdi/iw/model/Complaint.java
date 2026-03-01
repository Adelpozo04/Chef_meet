package es.ucm.fdi.iw.model;

import java.util.Map;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Complaints made by users to the administrators about others users, recipes, communities, events, etc...
 */
@Entity
@Data
@NoArgsConstructor
public class Complaint {

    private static Map<Integer, String> typeMap = Map.of(
        0, "USER",
        1, "RECIPE",
        2, "COMMUNITY",
        3, "EVENT"
    );

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @ManyToOne
    private User owner;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "type")
    private int type;

    @Column(name = "reference_id")
    private long referenceId;   // ID del objeto al que se refiere -> Si type="USER" y ID=5 : la queja se refiere al usuario con ID 5
}