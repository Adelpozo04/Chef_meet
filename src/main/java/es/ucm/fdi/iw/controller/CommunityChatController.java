package es.ucm.fdi.iw.controller;


import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Message.ComplainType;
import es.ucm.fdi.iw.model.Message.Transfer;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


import es.ucm.fdi.iw.model.Community;
import es.ucm.fdi.iw.model.Complaint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class CommunityChatController {

    private static Logger log = LogManager.getLogger(CommunityChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EntityManager entityManager;


    @Transactional
    @MessageMapping("/community/{id}")
    public Transfer receive(
        @DestinationVariable Long id,
        Message msg,
        SimpMessageHeaderAccessor accessor  // Allow WebSocket access to session variables
    ) {
        // NUEVO
        // Obtener el usuario logueado desde la sesión del WebSocket
         User sessionUser = (User) accessor.getSessionAttributes().get("u");

         // Recargar el usuario desde base de datos para trabajar con una entidad gestionada
    User user = entityManager.find(User.class, sessionUser.getId());

    /*        String username = ((User) accessor.getSessionAttributes().get("u") ).getUsername();
        Transfer t = new Transfer(username, msg.getText());
        messagingTemplate.convertAndSend(
            "/topic/community-" + id,
            t
        ); */


        //User user = entityManager.find(User.class, ((User)accessor.getSessionAttributes().get("u")).getId() );
        LocalDateTime now = LocalDateTime.now();
        Message message = new Message();
        message.setRecipient(null);
        message.setSender(user);
        message.setComplainType(ComplainType.CHAT);
        message.setReferenceId(id);
        message.setText(msg.getText());
        message.setDateSent(now);

        // Guardar primero el mensaje para que tenga id
        entityManager.persist(message);
        entityManager.flush();

        Community community = entityManager.find(Community.class, id);

        try {

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode rootNode = mapper.createObjectNode();

                rootNode.put("type", "NEW_CHAT_MESSAGE");
                // Texto de la notificacion
                rootNode.put("text", message.getSender().getUsername() + " ha enviado un mensaje por el chat de " + community.getTitle());
                // Convertir JSON a texto
                String json = mapper.writeValueAsString(rootNode);

                // Publicar en el canal del evento
                messagingTemplate.convertAndSend("/topic/community-"+ id, json);
                
            } catch (Exception e) {

               log.error("Error al publicar en el canal del evento", e );
            }

        // NUEVO
        // Crear el objeto que se enviará al frontend.
        // Incluye el id del mensaje, necesario para poder denunciarlo.
        Transfer t = new Transfer(message.getId(), user.getUsername(), message.getText());

        // Enviar el mensaje a todos los usuarios suscritos al canal de la comunidad
        messagingTemplate.convertAndSend(
            "/topic/community-" + id,
            t
        );
        
        return t;
    }
}