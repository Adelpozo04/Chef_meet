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

        String username = ((User) accessor.getSessionAttributes().get("u") ).getUsername();
        Transfer t = new Transfer(username, msg.getText());
        messagingTemplate.convertAndSend(
            "/topic/community-" + id,
            t
        );

        User user = entityManager.find(User.class, ((User)accessor.getSessionAttributes().get("u")).getId() );
        LocalDateTime now = LocalDateTime.now();
        Message message = new Message();
        message.setRecipient(null);
        message.setSender(user);
        message.setComplainType(ComplainType.CHAT);
        message.setReferenceId(id);
        message.setText(msg.getText());
        message.setDateSent(now);

        entityManager.persist(message);
        entityManager.flush();

        return t;
    }
}