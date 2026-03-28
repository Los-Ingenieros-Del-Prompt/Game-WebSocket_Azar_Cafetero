package com.aguardientes.azarcafetero.game;

import com.aguardientes.azarcafetero.game.application.port.in.NotifyPlayerJoinedUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableClosedUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableCreatedUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.SubscribeToFloorUseCase;
import com.aguardientes.azarcafetero.game.application.port.in.UnsubscribeFromFloorUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.FloorSubscriptionRepository;
import com.aguardientes.azarcafetero.game.domain.FloorEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FloorEventIntegrationTest {

    @Autowired
    private FloorSubscriptionRepository floorSubscriptionRepository;

    @Autowired
    private SubscribeToFloorUseCase subscribeToFloorUseCase;

    @Autowired
    private UnsubscribeFromFloorUseCase unsubscribeFromFloorUseCase;

    @Autowired
    private NotifyTableCreatedUseCase notifyTableCreatedUseCase;

    @Autowired
    private NotifyPlayerJoinedUseCase notifyPlayerJoinedUseCase;

    @Autowired
    private NotifyTableClosedUseCase notifyTableClosedUseCase;

    private UUID testFloorId;
    private String testPlayerSessionId;

    @BeforeEach
    public void setUp() {
        testFloorId = UUID.randomUUID();
        testPlayerSessionId = "session-test-" + UUID.randomUUID();
    }

    @Test
    public void testPlayerSubscribesToFloor() {
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, testPlayerSessionId);

        Set<String> subscribers = floorSubscriptionRepository.getSubscribers(testFloorId);
        assertTrue(subscribers.contains(testPlayerSessionId), "Player should be subscribed to floor");
        assertEquals(1, subscribers.size(), "Should have exactly 1 subscriber");
    }

    @Test
    public void testMultiplePlayersSubscribeToFloor() {
        String session1 = "session-1";
        String session2 = "session-2";
        String session3 = "session-3";

        subscribeToFloorUseCase.subscribeToFloor(testFloorId, session1);
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, session2);
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, session3);

        Set<String> subscribers = floorSubscriptionRepository.getSubscribers(testFloorId);
        assertEquals(3, subscribers.size(), "Should have 3 subscribers");
        assertTrue(subscribers.contains(session1));
        assertTrue(subscribers.contains(session2));
        assertTrue(subscribers.contains(session3));
    }

    @Test
    public void testPlayerUnsubscribesFromFloor() {
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, testPlayerSessionId);
        assertTrue(floorSubscriptionRepository.getSubscribers(testFloorId).contains(testPlayerSessionId));

        unsubscribeFromFloorUseCase.unsubscribeFromFloor(testFloorId, testPlayerSessionId);
        assertFalse(floorSubscriptionRepository.getSubscribers(testFloorId).contains(testPlayerSessionId));
    }

    @Test
    public void testPartialUnsubscriptionKeepsFloor() {
        String session1 = "session-1";
        String session2 = "session-2";

        subscribeToFloorUseCase.subscribeToFloor(testFloorId, session1);
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, session2);

        unsubscribeFromFloorUseCase.unsubscribeFromFloor(testFloorId, session1);

        Set<String> subscribers = floorSubscriptionRepository.getSubscribers(testFloorId);
        assertEquals(1, subscribers.size(), "Should have 1 remaining subscriber");
        assertTrue(subscribers.contains(session2));
    }

    @Test
    public void testLastUnsubscriptionRemovesFloor() {
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, testPlayerSessionId);
        unsubscribeFromFloorUseCase.unsubscribeFromFloor(testFloorId, testPlayerSessionId);

        Set<String> subscribers = floorSubscriptionRepository.getSubscribers(testFloorId);
        assertTrue(subscribers.isEmpty(), "Floor should be removed after last unsubscription");
    }

    @Test
    public void testNotifyTableCreated() {
        String tableId = "table-1";
        String tableName = "Poker Table 1";
        Integer maxPlayers = 6;

        assertDoesNotThrow(() -> 
            notifyTableCreatedUseCase.notifyTableCreated(testFloorId, tableId, tableName, maxPlayers),
            "Should not throw exception when notifying table created"
        );
    }

    @Test
    public void testNotifyPlayerJoined() {
        String tableId = "table-1";
        String playerName = "Alice";
        Integer currentPlayers = 2;
        Integer availableSeats = 4;

        assertDoesNotThrow(() -> 
            notifyPlayerJoinedUseCase.notifyPlayerJoined(testFloorId, tableId, playerName, currentPlayers, availableSeats),
            "Should not throw exception when notifying player joined"
        );
    }

    @Test
    public void testNotifyTableClosed() {
        String tableId = "table-1";

        assertDoesNotThrow(() -> 
            notifyTableClosedUseCase.notifyTableClosed(testFloorId, tableId),
            "Should not throw exception when notifying table closed"
        );
    }

    @Test
    public void testFloorEventProperties() {
        String tableId = "table-1";
        Integer availableSeats = 3;
        Integer totalPlayers = 3;

        FloorEvent event = new FloorEvent(
            testFloorId, 
            tableId, 
            FloorEvent.Type.PLAYER_JOINED, 
            "Player joined", 
            java.time.Instant.now(),
            availableSeats,
            totalPlayers
        );

        assertEquals(testFloorId, event.getFloorId());
        assertEquals(tableId, event.getTableId());
        assertEquals(FloorEvent.Type.PLAYER_JOINED, event.getType());
        assertEquals(availableSeats, event.getAvailableSeats());
        assertEquals(totalPlayers, event.getTotalPlayers());
    }

    @Test
    public void testFloorEventEquality() {
        java.time.Instant now = java.time.Instant.now();
        
        FloorEvent event1 = new FloorEvent(testFloorId, "table-1", FloorEvent.Type.TABLE_CREATED, 
                "Created", now, 6, 0);
        FloorEvent event2 = new FloorEvent(testFloorId, "table-1", FloorEvent.Type.TABLE_CREATED, 
                "Created", now, 6, 0);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    public void testNullValidationFloorSubscription() {
        assertThrows(NullPointerException.class, () -> 
            subscribeToFloorUseCase.subscribeToFloor(null, "session-1"));
        
        assertThrows(NullPointerException.class, () -> 
            subscribeToFloorUseCase.subscribeToFloor(testFloorId, null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            subscribeToFloorUseCase.subscribeToFloor(testFloorId, ""));
    }

    @Test
    public void testNullValidationFloorNotification() {
        assertThrows(NullPointerException.class, () -> 
            notifyTableCreatedUseCase.notifyTableCreated(null, "table-1", "Table", 6));
        
        assertThrows(NullPointerException.class, () -> 
            notifyTableCreatedUseCase.notifyTableCreated(testFloorId, null, "Table", 6));
        
        assertThrows(NullPointerException.class, () -> 
            notifyTableCreatedUseCase.notifyTableCreated(testFloorId, "table-1", null, 6));
        
        assertThrows(NullPointerException.class, () -> 
            notifyTableCreatedUseCase.notifyTableCreated(testFloorId, "table-1", "Table", null));
    }

    @Test
    public void testDuplicateSubscriptionIgnored() {
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, testPlayerSessionId);
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, testPlayerSessionId);

        Set<String> subscribers = floorSubscriptionRepository.getSubscribers(testFloorId);
        assertEquals(1, subscribers.size(), "Duplicate subscription should be ignored");
    }

    @Test
    public void testClearFloorSubscriptions() {
        String session1 = "session-1";
        String session2 = "session-2";

        subscribeToFloorUseCase.subscribeToFloor(testFloorId, session1);
        subscribeToFloorUseCase.subscribeToFloor(testFloorId, session2);
        assertEquals(2, floorSubscriptionRepository.getSubscribers(testFloorId).size());

        floorSubscriptionRepository.clearFloorSubscriptions(testFloorId);
        assertTrue(floorSubscriptionRepository.getSubscribers(testFloorId).isEmpty());
    }

    @Test
    public void testMultipleFloorsIndependence() {
        UUID floor1 = UUID.randomUUID();
        UUID floor2 = UUID.randomUUID();
        String session1 = "session-1";
        String session2 = "session-2";

        subscribeToFloorUseCase.subscribeToFloor(floor1, session1);
        subscribeToFloorUseCase.subscribeToFloor(floor2, session2);

        Set<String> floor1Subscribers = floorSubscriptionRepository.getSubscribers(floor1);
        Set<String> floor2Subscribers = floorSubscriptionRepository.getSubscribers(floor2);

        assertEquals(1, floor1Subscribers.size());
        assertEquals(1, floor2Subscribers.size());
        assertTrue(floor1Subscribers.contains(session1));
        assertTrue(floor2Subscribers.contains(session2));
        assertFalse(floor1Subscribers.contains(session2));
        assertFalse(floor2Subscribers.contains(session1));
    }

    @Test
    public void testFloorEventTypeEnum() {
        assertEquals(3, FloorEvent.Type.values().length);
        assertEquals(FloorEvent.Type.TABLE_CREATED, FloorEvent.Type.valueOf("TABLE_CREATED"));
        assertEquals(FloorEvent.Type.PLAYER_JOINED, FloorEvent.Type.valueOf("PLAYER_JOINED"));
        assertEquals(FloorEvent.Type.TABLE_CLOSED, FloorEvent.Type.valueOf("TABLE_CLOSED"));
    }
}
