package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
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
@NamedQueries({
    @NamedQuery(name = "Community.selectAll", query = "SELECT c FROM Community c"),
    @NamedQuery(name = "Community.selectWhereMeMember", query = "SELECT c FROM Community c WHERE c.owner.id = :userId"),
    @NamedQuery(name = "Community.delete", query = "DELETE FROM Community c WHERE c.id = :id")
})
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 200)
    private String description;

    @ManyToOne
    private User owner;
    
    @ManyToOne
    private Country country;

    @ManyToMany
    @JoinTable(
        name = "community_members",
        joinColumns = @JoinColumn(name = "community_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "community")
    private List<Event> events = new ArrayList<>();

    @ManyToMany
    private List<Recipe> recipes = new ArrayList<>();
}