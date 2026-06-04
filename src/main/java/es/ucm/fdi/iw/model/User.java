package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An authorized user of the system.
 */
@Entity
@Data
@NoArgsConstructor
@NamedQueries({
		@NamedQuery(
			name = "User.byUsername", 
			query = "SELECT u FROM User u WHERE u.username = :username AND u.enabled = TRUE"
		),
		@NamedQuery(
			name = "User.hasUsername", 
			query = "SELECT COUNT(u) FROM User u WHERE u.username = :username"
		),
		@NamedQuery(
			name = "User.topics",
			query = "SELECT t.key FROM Topic t JOIN t.members u WHERE u.id = :id"
		),
		@NamedQuery(
			name = "User.ownedCommunities", 
			query = "SELECT c FROM Community c WHERE c.owner.id = :id"
		),
		@NamedQuery(
			name = "User.joinedCommunities", 
			query = "SELECT c FROM Community c JOIN c.members m WHERE m.id = :id"
		)
})
@Table(name = "IWUser")
public class User implements Transferable<User.Transfer> {

	public enum Role {
		USER, // normal users
		ADMIN, // admin users
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
	@SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	private String firstName;
	private String lastName;

	private boolean enabled;
	private String roles; // split by ',' to separate roles

	// Numero de veces que el usuario ha iniciado sesion correctamente.
	private int loginCount = 0;

	@OneToMany(mappedBy = "owner")
	private List<Community> ownedCommunities = new ArrayList<>();

	@ManyToMany(targetEntity = Community.class, mappedBy = "members")
	private List<Community> joinedCommunities = new ArrayList<>();

	@OneToMany
	@JoinColumn(name = "sender_id")
	private List<Message> sent = new ArrayList<>();

	@OneToMany
	@JoinColumn(name = "recipient_id")
	private List<Message> received = new ArrayList<>();

	@OneToMany
	@JoinColumn(name = "author")
	private List<Recipe> recipes = new ArrayList<>();

	@OneToMany(mappedBy = "owner")
	private List<Complaint> complaints = new ArrayList<>();

	@ManyToMany(targetEntity = Topic.class, mappedBy = "members")
	private List<Topic> topics = new ArrayList<>();

	/*
	 * // Eventos que ha creado el usuario
	 * 
	 * @OneToMany (mappedBy = "organizer")
	 * private List<Event> organizedEvents = new ArrayList<>();
	 * 
	 * // Reservas de eventos a los que va a asistir el usuario
	 * 
	 * @OneToMany(mappedBy = "attendee")
	 * private List<Reserve> myReserves = new ArrayList<>();
	 */
	@Getter
	@AllArgsConstructor
	public static class Transfer {
		private long id;
		private String username;
		private int totalReceived;
		private int totalSent;
		private String recipes;
		private String complaints;
	}

	@Override
	public Transfer toTransfer() {

		StringBuilder recips = new StringBuilder();
		for (Recipe r : recipes) {
			recips.append(r.getTitle()).append(", ");
		}

		StringBuilder comp = new StringBuilder();
		for (Complaint c : complaints) {
			comp.append(c.getTitle()).append(", ");
		}

		return new Transfer(id, username, received.size(), sent.size(), recips.toString(), comp.toString());
	}

	/**
	 * Checks whether this user has a given role.
	 * 
	 * @param role to check
	 * @return true iff this user has that role.
	 */
	public boolean hasRole(Role role) {
		String roleName = role.name();
		return Arrays.asList(roles.split(",")).contains(roleName);
	}

}