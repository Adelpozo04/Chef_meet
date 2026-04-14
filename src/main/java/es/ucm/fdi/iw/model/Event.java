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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
public class Event implements Transferable<Event.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false)
    private String description;

    private String theme;
    // LocalDateTime para tener fecha y hora
    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime date;
    @NotBlank(message = "Debes indicar una ubicación")
    private String location;
    @NotNull(message = "El precio no puede estar vacío")
    @Min( value = 0, message = "El precio no puede ser negativo")
    private Double price;
    @NotNull(message = "El aforo no puede estar vacío")
    @Min( value = 1, message = "El evento debe tener al menos 1 plaza")
    private Integer capacity;
    private String imagePath;
    //private String organizerEmail;
    private boolean isPrivate;

    // Conexiones entre las distintas tablas (usuarios y comunidades)
    @ManyToOne(targetEntity = User.class) // Usuario que organiza el evento
    private User organizer;
    
    @ManyToOne
    private Community community;

    // Un evento tiene muchas reservas asociadas
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Reservation> attendees = new ArrayList<>();

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
        private String community;
        /*public Transfer(Event e) {
            this.id = e.getId();
        }*/
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
            id, title, description, theme,
            date == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date),
            location, 
            price != null ? price: 0.0, 
            capacity != null ?  capacity: 0,
            organizer != null ? organizer.getUsername() : "Anónimo",
            community != null ? community.getTitle() : "Público"
        );
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }
}
