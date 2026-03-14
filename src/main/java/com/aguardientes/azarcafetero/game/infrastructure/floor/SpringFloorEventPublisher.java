package com.aguardientes.azarcafetero.game.infrastructure.floor;

import com.aguardientes.azarcafetero.game.application.port.out.FloorEventPublisher;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.PlayerJoinedEventDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableClosedEventDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableCreatedEventDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class SpringFloorEventPublisher implements FloorEventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public SpringFloorEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = Objects.requireNonNull(messagingTemplate, "MessagingTemplate cannot be null");
    }

    @Override
    public void publishTableCreated(UUID floorId, String tableId, String tableName, Integer maxPlayers) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(tableId, "Table id cannot be null");
        
        TableCreatedEventDTO event = new TableCreatedEventDTO(floorId, tableId, tableName, maxPlayers, maxPlayers);
        String destination = "/topic/floor/" + floorId;
        messagingTemplate.convertAndSend(destination, event);
    }

    @Override
    public void publishPlayerJoined(UUID floorId, String tableId, String playerName, Integer currentPlayers, Integer availableSeats) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(tableId, "Table id cannot be null");
        
        PlayerJoinedEventDTO event = new PlayerJoinedEventDTO(floorId, tableId, playerName, currentPlayers, availableSeats);
        String destination = "/topic/floor/" + floorId;
        messagingTemplate.convertAndSend(destination, event);
    }

    @Override
    public void publishTableClosed(UUID floorId, String tableId) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(tableId, "Table id cannot be null");
        
        TableClosedEventDTO event = new TableClosedEventDTO(floorId, tableId);
        String destination = "/topic/floor/" + floorId;
        messagingTemplate.convertAndSend(destination, event);
    }
}
