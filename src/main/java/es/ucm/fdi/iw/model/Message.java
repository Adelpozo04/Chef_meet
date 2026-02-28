package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Mensaje que los usuarios pueden usar para enviarse entre ellos, en chats de las comunidades,
 * o enviar quejas/reportes al administrador.
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "Message.countUnread", query = "SELECT COUNT(m) FROM Message m "
				+ "WHERE m.recipient.id = :userId AND m.dateRead = null")
})
@Data
@NoArgsConstructor
public class Message implements Transferable<Message.Transfer> {

	private static Logger log = LogManager.getLogger(Message.class);

	// Enum para identificar el tipo de queja
	public enum ComplainType {
		NONE,		// Mensaje normal 
		USER,		// Queja hacia un usuario
		RECIPE,		// Queja hacia una receta
		COMMENT,	// Queja hacia un comentario
		EVENT		// Queja hacia un evento
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
	@SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

	@ManyToOne(targetEntity = User.class)
	private User sender;	// El que envia el mensaje
	@ManyToOne(targetEntity = User.class)
	private User recipient;	// Null para chat de la comunidad. Si es queja, puede ser el admin.
	/*@ManyToOne(targetEntity = Community.class )
	private Community community;*/	// Un chat agrupa todos los mensajes de una comunidad
	
	@ManyToOne
	private Topic topic; // Chat de la comunidad al que pertenece

	@Column(nullable = false)
	private String text;

	private LocalDateTime dateSent;
	private LocalDateTime dateRead;

	// Campos especificos para quejas
	@Enumerated(EnumType.STRING)
	private ComplainType complainType = ComplainType.NONE;

	// ID elemento reportado. Long para que pueda ser null en la base de datos
	// en caso de que sea un mensaje normal
	private Long referenceId;


	/**
	 * Objeto para persistir a/de JSON
	 */
	@Getter
	@AllArgsConstructor
	public static class Transfer {
		private String from;
		private String to;
		//private String community;
		private String sent;
		private String received;
		private String topic;
		private String text;
		private String complainType;
		private Long referenceId;
		long id;

		public Transfer(Message m) {
			this.from = m.getSender() != null ? m.getSender().getUsername() : "Desconocido";
			this.to = m.getRecipient() == null ? "null" : m.getRecipient().getUsername();
			//this.community = m.getCommunity() == null ? "null" : m.getCommunity().getTitle();
			this.topic = m.getTopic() == null ? "null" : m.getTopic().getName();
			this.sent = m.getDateSent() == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateSent());
			this.received = m.getDateRead() == null ? null
					: DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateRead());
			this.text = m.getText();
			this.complainType = m.getComplainType() != null ? m.getComplainType().toString() : "NONE";
			this.referenceId = m.getReferenceId();
			this.id = m.getId();
		}
	}

	@Override
	public Transfer toTransfer() {
		return new Transfer(this);
	}
}
