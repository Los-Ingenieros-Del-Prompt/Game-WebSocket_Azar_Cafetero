package com.aguardientes.azarcafetero.game.infrastructure.websocket;

import com.aguardientes.azarcafetero.game.application.port.in.LeaveTableUseCase;
import com.aguardientes.azarcafetero.game.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Objects;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final LeaveTableUseCase leaveTableUseCase;

    public WebSocketEventListener(LeaveTableUseCase leaveTableUseCase) {
        this.leaveTableUseCase = Objects.requireNonNull(leaveTableUseCase, "LeaveTableUseCase cannot be null");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes == null) return;

        String playerId = (String) sessionAttributes.get("playerId");
        String playerName = (String) sessionAttributes.get("playerName");
        String tableId = (String) sessionAttributes.get("tableId");

        if (playerId != null && tableId != null) {
            logger.info("Player {} ({}) disconnected from table {}", playerName, playerId, tableId);
            
            Player player = new Player(playerId, playerName != null ? playerName : "Unknown", playerId);
            leaveTableUseCase.leaveTable(tableId, player);
        }
    }
}
