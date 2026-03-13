package es.ucm.fdi.iw.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(name = "Country.selectAll", query = "SELECT c FROM Country c"),
    @NamedQuery(name = "Country.selectByID", query = "SELECT c FROM Country c WHERE c.id = :id"),
    @NamedQuery(name = "Country.selectByName", query = "SELECT c FROM Country c WHERE c.countryName = :name")
})
public class Country {
 
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    private long id;

    @Column(name = "country_name", length = 50, nullable = false)
    private String countryName;

    @Column(name = "country_emoji", length = 10)
    private String emoji;

}