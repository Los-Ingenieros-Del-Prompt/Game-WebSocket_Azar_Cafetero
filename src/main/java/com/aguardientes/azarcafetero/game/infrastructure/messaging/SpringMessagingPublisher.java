package com.aguardientes.azarcafetero.game.infrastructure.messaging;

import com.aguardientes.azarcafetero.game.application.port.out.MessagePublisher;
import com.aguardientes.azarcafetero.game.domain.TableMessage;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableMessageDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringMessagingPublisher implements MessagePublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public SpringMessagingPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = Objects.requireNonNull(messagingTemplate, "MessagingTemplate cannot be null");
    }

    @Override
    public void publishToTable(TableMessage message) {
        Objects.requireNonNull(message, "Message cannot be null");
        
        TableMessageDTO dto = new TableMessageDTO(
                message.getPlayerId(),
                message.getTableId(),
                message.getContent(),
                message.getTimestamp()
        );
        
        String destination = "/topic/table/" + message.getTableId();
        messagingTemplate.convertAndSend(destination, dto);
    }
}
