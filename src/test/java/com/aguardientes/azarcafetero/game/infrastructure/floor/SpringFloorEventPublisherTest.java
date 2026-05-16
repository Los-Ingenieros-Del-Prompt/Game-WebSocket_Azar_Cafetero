package com.aguardientes.azarcafetero.game.infrastructure.floor;

import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.PlayerJoinedEventDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableClosedEventDTO;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableCreatedEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SpringFloorEventPublisherTest {

    private SimpMessagingTemplate template;
    private SpringFloorEventPublisher publisher;

    @BeforeEach
    void setUp() {
        template = mock(SimpMessagingTemplate.class);
        publisher = new SpringFloorEventPublisher(template);
    }

    @Test
    void constructor_nullTemplateThrows() {
        assertThrows(NullPointerException.class, () -> new SpringFloorEventPublisher(null));
    }

    @Test
    void publishTableCreated_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class,
                () -> publisher.publishTableCreated(null, "t1", "T", 6));
        assertThrows(NullPointerException.class,
                () -> publisher.publishTableCreated(id, null, "T", 6));
    }

    @Test
    void publishTableCreated_sendsCorrectEvent() {
        UUID floorId = UUID.randomUUID();
        publisher.publishTableCreated(floorId, "t1", "Poker", 6);

        ArgumentCaptor<TableCreatedEventDTO> captor = ArgumentCaptor.forClass(TableCreatedEventDTO.class);
        verify(template, times(1)).convertAndSend(eq("/topic/floor/" + floorId), captor.capture());

        TableCreatedEventDTO event = captor.getValue();
        assertEquals(floorId, event.getFloorId());
        assertEquals("t1", event.getTableId());
        assertEquals("Poker", event.getTableName());
        assertEquals(6, event.getMaxPlayers());
    }

    @Test
    void publishPlayerJoined_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class,
                () -> publisher.publishPlayerJoined(null, "t1", "Alice", 1, 5));
        assertThrows(NullPointerException.class,
                () -> publisher.publishPlayerJoined(id, null, "Alice", 1, 5));
    }

    @Test
    void publishPlayerJoined_sendsCorrectEvent() {
        UUID floorId = UUID.randomUUID();
        publisher.publishPlayerJoined(floorId, "t1", "Alice", 2, 4);

        ArgumentCaptor<PlayerJoinedEventDTO> captor = ArgumentCaptor.forClass(PlayerJoinedEventDTO.class);
        verify(template, times(1)).convertAndSend(eq("/topic/floor/" + floorId), captor.capture());

        PlayerJoinedEventDTO event = captor.getValue();
        assertEquals(floorId, event.getFloorId());
        assertEquals("t1", event.getTableId());
        assertEquals("Alice", event.getPlayerName());
        assertEquals(2, event.getCurrentPlayers());
        assertEquals(4, event.getAvailableSeats());
    }

    @Test
    void publishTableClosed_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class,
                () -> publisher.publishTableClosed(null, "t1"));
        assertThrows(NullPointerException.class,
                () -> publisher.publishTableClosed(id, null));
    }

    @Test
    void publishTableClosed_sendsCorrectEvent() {
        UUID floorId = UUID.randomUUID();
        publisher.publishTableClosed(floorId, "t1");

        ArgumentCaptor<TableClosedEventDTO> captor = ArgumentCaptor.forClass(TableClosedEventDTO.class);
        verify(template, times(1)).convertAndSend(eq("/topic/floor/" + floorId), captor.capture());

        TableClosedEventDTO event = captor.getValue();
        assertEquals(floorId, event.getFloorId());
        assertEquals("t1", event.getTableId());
    }
}