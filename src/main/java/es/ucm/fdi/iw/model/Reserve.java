package es.ucm.fdi.iw.model;

import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Reserve implements Transferable<Reserve.Transfer> {

    // Toda entidad JPA necesita un ID propio
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    // id asistente
    @ManyToOne(targetEntity = User.class)
    private User attendee;
    // id evento
    @ManyToOne(targetEntity = Event.class)
    private Event event;

	// Objeto para persistir a/de JSON
    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String attendeeName;
        private String eventTitle;
        /*public Transfer(Reserve r) {
            this.id = r.getId();
        }*/
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
            id,
            attendee != null ? attendee.getUsername() : "Desconocido",
            event != null ? event.getTitle() : "Evento desconocido"
        );
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }
}
