package com.aguardientes.azarcafetero.game;

import com.aguardientes.azarcafetero.game.application.port.in.JoinTableUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.LeaveTableUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.SendMessageUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Player;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableMessage;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.JoinTableDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableMessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TableWebSocketIntegrationTest {

    @Autowired
    private TableSessionRepository tableSessionRepository;

    @Autowired
    private JoinTableUseCase joinTableUseCase;

    @Autowired
    private LeaveTableUseCase leaveTableUseCase;

    @Autowired
    private SendMessageUseCase sendMessageUseCase;

    private BlockingQueue<TableMessageDTO> messageQueue;

    @BeforeEach
    public void setUp() {
        messageQueue = new LinkedBlockingQueue<>();
        if (tableSessionRepository != null) {
            // Clear any existing sessions
        }
    }

    @Test
    public void testPlayerJoinsTable() {
        String tableId = "table-1";
        String playerId = "player-1";
        String playerName = "Alice";

        Player player = new Player(playerId, playerName, playerId);
        joinTableUseCase.joinTable(tableId, player);

        assertTrue(tableSessionRepository.existsById(tableId), "Table session should exist after player joins");
        
        TableSession session = tableSessionRepository.findById(tableId).orElse(null);
        assertNotNull(session, "Table session should not be null");
        assertEquals(1, session.getPlayerCount(), "Table should have 1 player");
        assertTrue(session.hasPlayer(playerId), "Player should be in table session");
    }

    @Test
    public void testMultiplePlayersJoinTable() {
        String tableId = "table-2";

        Player player1 = new Player("player-1", "Alice", "player-1");
        Player player2 = new Player("player-2", "Bob", "player-2");
        Player player3 = new Player("player-3", "Charlie", "player-3");

        joinTableUseCase.joinTable(tableId, player1);
        joinTableUseCase.joinTable(tableId, player2);
        joinTableUseCase.joinTable(tableId, player3);

        TableSession session = tableSessionRepository.findById(tableId).orElse(null);
        assertNotNull(session, "Table session should exist");
        assertEquals(3, session.getPlayerCount(), "Table should have 3 players");
    }

    @Test
    public void testPlayerLeavesTable() {
        String tableId = "table-3";
        Player player = new Player("player-1", "Alice", "player-1");

        joinTableUseCase.joinTable(tableId, player);
        assertTrue(tableSessionRepository.existsById(tableId), "Table should exist after join");

        leaveTableUseCase.leaveTable(tableId, player);
        assertFalse(tableSessionRepository.existsById(tableId), "Table should be deleted when last player leaves");
    }

    @Test
    public void testPartialPlayerLeaveTable() {
        String tableId = "table-4";
        Player player1 = new Player("player-1", "Alice", "player-1");
        Player player2 = new Player("player-2", "Bob", "player-2");

        joinTableUseCase.joinTable(tableId, player1);
        joinTableUseCase.joinTable(tableId, player2);
        assertEquals(2, tableSessionRepository.findById(tableId).orElse(null).getPlayerCount());

        leaveTableUseCase.leaveTable(tableId, player1);
        
        assertTrue(tableSessionRepository.existsById(tableId), "Table should still exist with 1 player");
        assertEquals(1, tableSessionRepository.findById(tableId).orElse(null).getPlayerCount());
    }

    @Test
    public void testSendMessageToTable() {
        String tableId = "table-5";
        String playerId = "player-1";
        String content = "Hello, table!";

        Player player = new Player(playerId, "Alice", playerId);
        joinTableUseCase.joinTable(tableId, player);

        TableMessage message = new TableMessage(playerId, tableId, content, Instant.now());
        assertDoesNotThrow(() -> sendMessageUseCase.sendMessage(message), "Sending message should not throw exception");
    }

    @Test
    public void testMultipleConcurrentTables() {
        String table1Id = "table-6";
        String table2Id = "table-7";
        String table3Id = "table-8";

        Player player1 = new Player("player-1", "Alice", "player-1");
        Player player2 = new Player("player-2", "Bob", "player-2");
        Player player3 = new Player("player-3", "Charlie", "player-3");

        joinTableUseCase.joinTable(table1Id, player1);
        joinTableUseCase.joinTable(table2Id, player2);
        joinTableUseCase.joinTable(table3Id, player3);

        assertTrue(tableSessionRepository.existsById(table1Id));
        assertTrue(tableSessionRepository.existsById(table2Id));
        assertTrue(tableSessionRepository.existsById(table3Id));

        assertEquals(1, tableSessionRepository.findById(table1Id).orElse(null).getPlayerCount());
        assertEquals(1, tableSessionRepository.findById(table2Id).orElse(null).getPlayerCount());
        assertEquals(1, tableSessionRepository.findById(table3Id).orElse(null).getPlayerCount());
    }

    @Test
    public void testTableSessionIsolation() {
        String table1Id = "table-9";
        String table2Id = "table-10";

        Player player1 = new Player("player-1", "Alice", "player-1");
        Player player2 = new Player("player-2", "Bob", "player-2");
        Player player3 = new Player("player-1", "Alice", "player-1");

        joinTableUseCase.joinTable(table1Id, player1);
        joinTableUseCase.joinTable(table1Id, player2);
        joinTableUseCase.joinTable(table2Id, player3);

        TableSession session1 = tableSessionRepository.findById(table1Id).orElse(null);
        TableSession session2 = tableSessionRepository.findById(table2Id).orElse(null);

        assertNotNull(session1);
        assertNotNull(session2);
        assertEquals(2, session1.getPlayerCount(), "Table 1 should have 2 players");
        assertEquals(1, session2.getPlayerCount(), "Table 2 should have 1 player");

        leaveTableUseCase.leaveTable(table1Id, player1);
        
        assertEquals(1, session1.getPlayerCount(), "Table 1 should have 1 player after leaving");
        assertEquals(1, session2.getPlayerCount(), "Table 2 should remain unchanged");
    }

    @Test
    public void testTableMessageProperties() {
        String tableId = "table-11";
        String playerId = "player-1";
        String content = "Test message";
        Instant timestamp = Instant.now();

        TableMessage message = new TableMessage(playerId, tableId, content, timestamp);

        assertEquals(playerId, message.getPlayerId());
        assertEquals(tableId, message.getTableId());
        assertEquals(content, message.getContent());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    public void testPlayerProperties() {
        String playerId = "player-1";
        String playerName = "Alice";
        String sessionId = "session-123";

        Player player = new Player(playerId, playerName, sessionId);

        assertEquals(playerId, player.getId());
        assertEquals(playerName, player.getName());
        assertEquals(sessionId, player.getSessionId());
    }

    @Test
    public void testTableProperties() {
        String tableId = "table-test";
        String tableName = "Test Table";

        Table table = new Table(tableId, tableName);

        assertEquals(tableId, table.getId());
        assertEquals(tableName, table.getName());
    }

    @Test
    public void testNullValidationInPlayer() {
        assertThrows(NullPointerException.class, () -> new Player(null, "Alice", "session-1"));
        assertThrows(NullPointerException.class, () -> new Player("player-1", null, "session-1"));
        assertThrows(NullPointerException.class, () -> new Player("player-1", "Alice", null));
    }

    @Test
    public void testNullValidationInTable() {
        assertThrows(NullPointerException.class, () -> new Table(null, "Table"));
        assertThrows(NullPointerException.class, () -> new Table("table-1", null));
    }

    @Test
    public void testNullValidationInTableMessage() {
        Instant now = Instant.now();
        assertThrows(NullPointerException.class, () -> new TableMessage(null, "table-1", "content", now));
        assertThrows(NullPointerException.class, () -> new TableMessage("player-1", null, "content", now));
        assertThrows(NullPointerException.class, () -> new TableMessage("player-1", "table-1", null, now));
        assertThrows(NullPointerException.class, () -> new TableMessage("player-1", "table-1", "content", null));
    }

    @Test
    public void testTableSessionInitialization() {
        Table table = new Table("table-1", "Test Table");
        TableSession session = new TableSession(table);

        assertNotNull(session.getTable());
        assertEquals("table-1", session.getTableId());
        assertEquals(0, session.getPlayerCount());
        assertTrue(session.isEmpty());
    }

    @Test
    public void testPlayerEquality() {
        Player player1 = new Player("player-1", "Alice", "session-1");
        Player player2 = new Player("player-1", "Alice", "session-1");

        assertEquals(player1, player2);
        assertEquals(player1.hashCode(), player2.hashCode());
    }

    @Test
    public void testTableEquality() {
        Table table1 = new Table("table-1", "Table A");
        Table table2 = new Table("table-1", "Table B");

        assertEquals(table1, table2);
    }
}
