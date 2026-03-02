package es.ucm.fdi.iw.model;


import java.util.List;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Event implements Transferable<Event.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private String theme;
    // LocalDateTime para tener fecha y hora
    private LocalDateTime date;
    private String location;
    private double price;
    private int capacity;
    private String imagePath;
    //private String organizerEmail;
    private boolean isPrivate;

    // Conexiones entre las distintas tablas (usuarios y comunidades)
    @ManyToOne(targetEntity = User.class) // Usuario que organiza el evento
    private User organizer;
    
    // @ManyToOne
    // private Community community;

    // Un evento tiene muchas reservas asociadas
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Reserve> attendees = new ArrayList<>();

    // Objeto para persistir a/de JSON
    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String title;
        private String description;
        private String theme;
        private String date;
        private String location;
        private double price;
        private int capacity;
        private String organizer;
        /*public Transfer(Event e) {
            this.id = e.getId();
        }*/
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
            id, title, description, theme,
            date == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date),
            location, price, capacity,
            organizer != null ? organizer.getUsername() : "Anónimo"
        );
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }
}
