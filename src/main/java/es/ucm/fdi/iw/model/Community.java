package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

 
/**
 * Communities that users can join to create cooking recipes, interact with
 * other members, assist to community events
 */
@Entity
@Data
@NoArgsConstructor
public class Community {

    private static Logger log = LogManager.getLogger(Message.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @ManyToOne
    private User owner;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 200)
    private String description;

    @ManyToMany
    private List<User> members = new ArrayList<>();

    // @OneToMany
    // private List<Event> events = new ArrayList<>();

}