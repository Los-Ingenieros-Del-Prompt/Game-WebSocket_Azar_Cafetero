package com.aguardientes.azarcafetero.game.infrastructure.websocket;

import com.aguardientes.azarcafetero.game.application.port.in.BroadcastMessageUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.JoinTableUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.LeaveTableUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.SendMessageUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableCreatedUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.NotifyPlayerJoinedUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableClosedUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.SubscribeToFloorUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.UnsubscribeFromFloorUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.LobbyPlayerClient;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Player;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import com.aguardientes.azarcafetero.game.domain.TableMessage;
import com.aguardientes.azarcafetero.game.infrastructure.client.dto.LobbyPlayerDTO;
import com.aguardientes.azarcafetero.game.infrastructure.floor.InMemoryTableFloorRegistry;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.JoinTableDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableMessageDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.SubscribeToFloorDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableCreatedEventRequest;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.PlayerJoinedEventRequest;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableClosedEventRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
public class TableWebSocketController {
    private final JoinTableUseCase joinTableUseCase;
    private final LeaveTableUseCase leaveTableUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final BroadcastMessageUseCase broadcastMessageUseCase;
    private final NotifyTableCreatedUseCase notifyTableCreatedUseCase;
    private final NotifyPlayerJoinedUseCase notifyPlayerJoinedUseCase;
    private final NotifyTableClosedUseCase notifyTableClosedUseCase;
    private final SubscribeToFloorUseCase subscribeToFloorUseCase;
    private final UnsubscribeFromFloorUseCase unsubscribeFromFloorUseCase;
    private final LobbyPlayerClient lobbyPlayerClient;
    private final TableSessionRepository tableSessionRepository;
    private final InMemoryTableFloorRegistry tableFloorRegistry;
    private final UUID defaultBriscaFloorId;

    public TableWebSocketController(
            JoinTableUseCase joinTableUseCase,
            LeaveTableUseCase leaveTableUseCase,
            SendMessageUseCase sendMessageUseCase,
            BroadcastMessageUseCase broadcastMessageUseCase,
            NotifyTableCreatedUseCase notifyTableCreatedUseCase,
            NotifyPlayerJoinedUseCase notifyPlayerJoinedUseCase,
            NotifyTableClosedUseCase notifyTableClosedUseCase,
            SubscribeToFloorUseCase subscribeToFloorUseCase,
            UnsubscribeFromFloorUseCase unsubscribeFromFloorUseCase,
            LobbyPlayerClient lobbyPlayerClient,
            TableSessionRepository tableSessionRepository,
            InMemoryTableFloorRegistry tableFloorRegistry,
            @Value("${game.floor.brisca-id:}") String defaultBriscaFloorId) {
        this.joinTableUseCase = Objects.requireNonNull(joinTableUseCase, "JoinTableUseCase cannot be null");
        this.leaveTableUseCase = Objects.requireNonNull(leaveTableUseCase, "LeaveTableUseCase cannot be null");
        this.sendMessageUseCase = Objects.requireNonNull(sendMessageUseCase, "SendMessageUseCase cannot be null");
        this.broadcastMessageUseCase = Objects.requireNonNull(broadcastMessageUseCase, "BroadcastMessageUseCase cannot be null");
        this.notifyTableCreatedUseCase = Objects.requireNonNull(notifyTableCreatedUseCase, "NotifyTableCreatedUseCase cannot be null");
        this.notifyPlayerJoinedUseCase = Objects.requireNonNull(notifyPlayerJoinedUseCase, "NotifyPlayerJoinedUseCase cannot be null");
        this.notifyTableClosedUseCase = Objects.requireNonNull(notifyTableClosedUseCase, "NotifyTableClosedUseCase cannot be null");
        this.subscribeToFloorUseCase = Objects.requireNonNull(subscribeToFloorUseCase, "SubscribeToFloorUseCase cannot be null");
        this.unsubscribeFromFloorUseCase = Objects.requireNonNull(unsubscribeFromFloorUseCase, "UnsubscribeFromFloorUseCase cannot be null");
        this.lobbyPlayerClient = Objects.requireNonNull(lobbyPlayerClient, "LobbyPlayerClient cannot be null");
        this.tableSessionRepository = Objects.requireNonNull(tableSessionRepository, "TableSessionRepository cannot be null");
        this.tableFloorRegistry = Objects.requireNonNull(tableFloorRegistry, "TableFloorRegistry cannot be null");
        this.defaultBriscaFloorId = parseOptionalFloorId(defaultBriscaFloorId);
    }

    @MessageMapping("/table/{tableId}/join")
    @SendTo("/topic/table/{tableId}")
    public TableMessageDTO handleJoinTable(
            JoinTableDTO joinRequest,
            @DestinationVariable String tableId) {
        
        LobbyPlayerDTO lobbyPlayer = lobbyPlayerClient.getPlayerById(joinRequest.getPlayerId());
        
        Player player = new Player(
                joinRequest.getPlayerId(),
                lobbyPlayer.getDisplayName(),
                joinRequest.getPlayerId(),
                lobbyPlayer.getBalance().doubleValue()
        );
        
        joinTableUseCase.joinTable(tableId, player);

        TableSession session = tableSessionRepository.findById(tableId)
                .orElseThrow(() -> new IllegalStateException("Table session not found after join: " + tableId));

        UUID floorId = resolveFloorId(tableId, joinRequest.getFloorId());
        if (floorId != null) {
            notifyPlayerJoinedUseCase.notifyPlayerJoined(
                    floorId,
                    tableId,
                    lobbyPlayer.getDisplayName(),
                    session.getPlayerCount(),
                    session.getAvailableSeats()
            );
        }
        
        return new TableMessageDTO(
                "SYSTEM",
                tableId,
                lobbyPlayer.getDisplayName() + " joined the table",
                Instant.now()
        );
    }

    @MessageMapping("/table/{tableId}/leave")
    @SendTo("/topic/table/{tableId}")
    public TableMessageDTO handleLeaveTable(
            JoinTableDTO leaveRequest,
            @DestinationVariable String tableId) {
        
        Player player = new Player(
                leaveRequest.getPlayerId(),
                leaveRequest.getPlayerName(),
                leaveRequest.getPlayerId()
        );
        
        leaveTableUseCase.leaveTable(tableId, player);

        UUID floorId = resolveFloorId(tableId, leaveRequest.getFloorId());
        if (floorId != null && !tableSessionRepository.existsById(tableId)) {
            notifyTableClosedUseCase.notifyTableClosed(floorId, tableId);
            tableFloorRegistry.remove(tableId);
        }
        
        return new TableMessageDTO(
                "SYSTEM",
                tableId,
                leaveRequest.getPlayerName() + " left the table",
                Instant.now()
        );
    }

    @MessageMapping("/table/{tableId}/message")
    @SendTo("/topic/table/{tableId}")
    public TableMessageDTO handleTableMessage(
            TableMessageDTO messageDTO,
            @DestinationVariable String tableId) {
        
        TableMessage message = new TableMessage(
                messageDTO.getPlayerId(),
                tableId,
                messageDTO.getContent(),
                Instant.now()
        );
        
        sendMessageUseCase.sendMessage(message);
        
        return messageDTO;
    }

    @MessageMapping("/floor/{floorId}/subscribe")
    public void handleFloorSubscription(
            SubscribeToFloorDTO subscriptionRequest,
            @DestinationVariable UUID floorId) {
        
        subscribeToFloorUseCase.subscribeToFloor(floorId, subscriptionRequest.getPlayerId());
    }

    @MessageMapping("/floor/{floorId}/unsubscribe")
    public void handleFloorUnsubscription(
            SubscribeToFloorDTO unsubscriptionRequest,
            @DestinationVariable UUID floorId) {
        
        unsubscribeFromFloorUseCase.unsubscribeFromFloor(floorId, unsubscriptionRequest.getPlayerId());
    }

    @MessageMapping("/floor/{floorId}/table-created")
    public void handleTableCreated(
            TableCreatedEventRequest request,
            @DestinationVariable UUID floorId) {
        
        notifyTableCreatedUseCase.notifyTableCreated(floorId, request.getTableId(), 
                request.getTableName(), request.getMaxPlayers());
    }

    @MessageMapping("/floor/{floorId}/player-joined")
    public void handlePlayerJoined(
            PlayerJoinedEventRequest request,
            @DestinationVariable UUID floorId) {
        
        notifyPlayerJoinedUseCase.notifyPlayerJoined(floorId, request.getTableId(), 
                request.getPlayerName(), request.getCurrentPlayers(), request.getAvailableSeats());
    }

    @MessageMapping("/floor/{floorId}/table-closed")
    public void handleTableClosed(
            TableClosedEventRequest request,
            @DestinationVariable UUID floorId) {
        
        notifyTableClosedUseCase.notifyTableClosed(floorId, request.getTableId());
    }

    private UUID resolveFloorId(String tableId, UUID floorIdFromRequest) {
        if (floorIdFromRequest != null) {
            tableFloorRegistry.register(tableId, floorIdFromRequest);
            return floorIdFromRequest;
        }

        Optional<UUID> floorIdFromRegistry = tableFloorRegistry.findFloorId(tableId);
        if (floorIdFromRegistry.isPresent()) {
            return floorIdFromRegistry.get();
        }

        return defaultBriscaFloorId;
    }

    private UUID parseOptionalFloorId(String floorId) {
        if (floorId == null || floorId.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(floorId.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid UUID in game.floor.brisca-id");
        }
    }
}
