package es.ucm.fdi.iw;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

import jakarta.servlet.http.HttpSession;

/**
 * Basic STOMP-powered websocket support
 * 
 * @see https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#websocket
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
            .addInterceptors(new UserHandshakeInterceptor())
            .setAllowedOrigins("*");
        // allowedOrigins allows proxying; see https://stackoverflow.com/questions/33977803
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Ruta de suscripcion para clientes
        config.enableSimpleBroker("/topic", "/queue");

        // Ruta de recepcion de mensajes de clientes
        config.setApplicationDestinationPrefixes("/app");
    }
}