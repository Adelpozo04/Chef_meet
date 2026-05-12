package es.ucm.fdi.iw.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.Message.Transfer;
import es.ucm.fdi.iw.model.User;


@Controller
public class CommunityChatController {

    private static Logger log = LogManager.getLogger(CommunityChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


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

        return t;
    }
}