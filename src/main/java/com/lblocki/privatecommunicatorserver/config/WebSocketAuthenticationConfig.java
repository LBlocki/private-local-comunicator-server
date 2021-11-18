package com.lblocki.privatecommunicatorserver.config;

import com.lblocki.privatecommunicatorserver.security.authentication.WebsocketAuthenticationToken;
import com.lblocki.privatecommunicatorserver.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Optional;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthenticationManager authenticationManager;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        Optional.ofNullable(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class))
                                .orElseThrow(() -> new RuntimeException("Failed to find accessor for stomp header"));

                log.debug("New websocket message. " + accessor);


                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    final String username = accessor.getFirstNativeHeader(SecurityUtils.USERNAME_HEADER);
                    final String password = accessor.getFirstNativeHeader(SecurityUtils.PASSWORD_HEADER);
                    final WebsocketAuthenticationToken token = new WebsocketAuthenticationToken(username, password);
                    final Authentication authentication = authenticationManager.authenticate(token);

                    accessor.setUser(authentication);
                }
                return message;
            }
        });
    }

}
