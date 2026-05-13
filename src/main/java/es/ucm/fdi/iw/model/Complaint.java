package es.ucm.fdi.iw.model;

import java.util.Map;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * Complaints made by users to the administrators about others users, recipes, communities, events, etc...
 */
@Entity
@Data
@NoArgsConstructor
public class Complaint implements Transferable<Complaint.Transfer> {

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

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Column(name = "description", length = 300, nullable = false)
    private String description;

    @Column(name = "type")
    private Integer type;

    @Column(name = "resolved")
    private boolean resolved;

    @Column(name = "reference_id")
    private long referenceId;   // ID del objeto al que se refiere -> Si type="USER" y ID=5 : la queja se refiere al usuario con ID 5

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String title;
        private String description;
        private Integer type;
        private boolean resolved;
        private long referenceId;
    }
    
    @Override
    public Transfer toTransfer() {

        return new Transfer(id, title, description, type, resolved, referenceId);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

}