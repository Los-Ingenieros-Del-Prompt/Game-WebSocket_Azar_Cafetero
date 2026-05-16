package com.aguardientes.azarcafetero.game.infrastructure.messaging;

import com.aguardientes.azarcafetero.game.domain.TableMessage;
import com.aguardientes.azarcafetero.game.infrastructure.websocket.dto.TableMessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SpringMessagingPublisherTest {

    private SimpMessagingTemplate template;
    private SpringMessagingPublisher publisher;

    @BeforeEach
    void setUp() {
        template = mock(SimpMessagingTemplate.class);
        publisher = new SpringMessagingPublisher(template);
    }

    @Test
    void constructor_nullTemplateThrows() {
        assertThrows(NullPointerException.class, () -> new SpringMessagingPublisher(null));
    }

    @Test
    void publishToTable_nullMessageThrows() {
        assertThrows(NullPointerException.class, () -> publisher.publishToTable(null));
    }

    @Test
    void publishToTable_sendsToCorrectDestination() {
        Instant now = Instant.now();
        TableMessage message = new TableMessage("p1", "t1", "hello", now);

        publisher.publishToTable(message);

        ArgumentCaptor<TableMessageDTO> dtoCaptor = ArgumentCaptor.forClass(TableMessageDTO.class);
        verify(template, times(1)).convertAndSend(eq("/topic/table/t1"), dtoCaptor.capture());

        TableMessageDTO sent = dtoCaptor.getValue();
        assertEquals("p1", sent.getPlayerId());
        assertEquals("t1", sent.getTableId());
        assertEquals("hello", sent.getContent());
        assertEquals(now, sent.getTimestamp());
    }
}