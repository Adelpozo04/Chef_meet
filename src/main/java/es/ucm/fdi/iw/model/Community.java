package es.ucm.fdi.iw.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

/**
 * Communities that users can join to create cooking recipes, interact with
 * other members, assist to community events
 */
@Entity
@Data
public class Community {

    private static Logger log = LogManager.getLogger(Message.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;
    private long creatorId;

    private String name;
    private String description;

}