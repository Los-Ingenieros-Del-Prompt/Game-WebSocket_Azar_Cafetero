package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.out.MessagePublisher;
import com.aguardientes.azarcafetero.game.domain.TableMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SendMessageServiceTest {

    private MessagePublisher publisher;
    private SendMessageService service;

    @BeforeEach
    void setUp() {
        publisher = mock(MessagePublisher.class);
        service = new SendMessageService(publisher);
    }

    @Test
    void constructor_nullPublisherThrows() {
        assertThrows(NullPointerException.class, () -> new SendMessageService(null));
    }

    @Test
    void sendMessage_nullThrows() {
        assertThrows(NullPointerException.class, () -> service.sendMessage(null));
    }

    @Test
    void sendMessage_delegatesToPublisher() {
        TableMessage msg = new TableMessage("p1", "t1", "hi", Instant.now());
        service.sendMessage(msg);
        verify(publisher, times(1)).publishToTable(msg);
    }
}