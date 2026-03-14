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
import com.aguardientes.azarcafetero.game.domain.Player;
import com.aguardientes.azarcafetero.game.domain.TableMessage;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.JoinTableDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableMessageDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.SubscribeToFloorDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableCreatedEventRequest;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.PlayerJoinedEventRequest;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableClosedEventRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Objects;
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

    public TableWebSocketController(
            JoinTableUseCase joinTableUseCase,
            LeaveTableUseCase leaveTableUseCase,
            SendMessageUseCase sendMessageUseCase,
            BroadcastMessageUseCase broadcastMessageUseCase,
            NotifyTableCreatedUseCase notifyTableCreatedUseCase,
            NotifyPlayerJoinedUseCase notifyPlayerJoinedUseCase,
            NotifyTableClosedUseCase notifyTableClosedUseCase,
            SubscribeToFloorUseCase subscribeToFloorUseCase,
            UnsubscribeFromFloorUseCase unsubscribeFromFloorUseCase) {
        this.joinTableUseCase = Objects.requireNonNull(joinTableUseCase, "JoinTableUseCase cannot be null");
        this.leaveTableUseCase = Objects.requireNonNull(leaveTableUseCase, "LeaveTableUseCase cannot be null");
        this.sendMessageUseCase = Objects.requireNonNull(sendMessageUseCase, "SendMessageUseCase cannot be null");
        this.broadcastMessageUseCase = Objects.requireNonNull(broadcastMessageUseCase, "BroadcastMessageUseCase cannot be null");
        this.notifyTableCreatedUseCase = Objects.requireNonNull(notifyTableCreatedUseCase, "NotifyTableCreatedUseCase cannot be null");
        this.notifyPlayerJoinedUseCase = Objects.requireNonNull(notifyPlayerJoinedUseCase, "NotifyPlayerJoinedUseCase cannot be null");
        this.notifyTableClosedUseCase = Objects.requireNonNull(notifyTableClosedUseCase, "NotifyTableClosedUseCase cannot be null");
        this.subscribeToFloorUseCase = Objects.requireNonNull(subscribeToFloorUseCase, "SubscribeToFloorUseCase cannot be null");
        this.unsubscribeFromFloorUseCase = Objects.requireNonNull(unsubscribeFromFloorUseCase, "UnsubscribeFromFloorUseCase cannot be null");
    }

    @MessageMapping("/table/{tableId}/join")
    @SendTo("/topic/table/{tableId}")
    public TableMessageDTO handleJoinTable(
            JoinTableDTO joinRequest,
            @DestinationVariable String tableId) {
        
        Player player = new Player(
                joinRequest.getPlayerId(),
                joinRequest.getPlayerName(),
                joinRequest.getPlayerId()
        );
        
        joinTableUseCase.joinTable(tableId, player);
        
        return new TableMessageDTO(
                "SYSTEM",
                tableId,
                joinRequest.getPlayerName() + " joined the table",
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
}
