package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Complaints made by users to the administrators about others users, recipes, communities, events, etc...
 */
@Entity
@Data
@NoArgsConstructor
public class Complaint implements Transferable<Complaint.Transfer> {

    //Tipos de queja que se pueden hacer, se usa el map para relacionar el string que se muestra al usuario con un numero entero que se guarda en la base de datos para facilitar las consultas
    public static Map<String, Integer> typeMap = Map.of(
        "USER", 0,
        "RECIPE", 1,
        "COMMUNITY", 2,
        "EVENT", 3,
        "MESSAGE", 4
    );

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    //Usuario que hace la queja
    @ManyToOne
    private User owner;

    //Titulo de la queja
    @Column(name = "title", length = 300, nullable = false)
    private String title;

    //Descripcion de la queja
    @Column(name = "description", length = 300, nullable = false)
    private String description;

    //Tipo de queja, puede ser contra un usuario, una receta, una comunidad o un evento
    @Column(name = "type")
    private Integer type;

    //Indica si la queja ya a sido resuelta o no, lo marca el admin solamente
    @Column(name = "resolved")
    private boolean resolved;

    @Column(name = "date")
    private LocalDateTime date;

    //Id del elemento al que se refiere la queja
    @Column(name = "reference_id")
    private Long referenceId;   // ID del objeto al que se refiere -> Si type="USER" y ID=5 : la queja se refiere al usuario con ID 5
    
    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String title;
        private String description;
        private Integer type;
        private boolean resolved;
        private Long referenceId;
        private LocalDateTime date;
    }
    
    @Override
    public Transfer toTransfer() {

        return new Transfer(id, title, description, type, resolved, referenceId, date);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }
}