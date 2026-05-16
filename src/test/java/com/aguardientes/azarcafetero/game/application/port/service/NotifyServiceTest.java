package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.out.FloorEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotifyServiceTest {

    private FloorEventPublisher publisher;
    private NotifyTableCreatedService createdService;
    private NotifyPlayerJoinedService joinedService;
    private NotifyTableClosedService closedService;

    @BeforeEach
    void setUp() {
        publisher = mock(FloorEventPublisher.class);
        createdService = new NotifyTableCreatedService(publisher);
        joinedService = new NotifyPlayerJoinedService(publisher);
        closedService = new NotifyTableClosedService(publisher);
    }

    @Test
    void notifyTableCreatedService_nullPublisherThrows() {
        assertThrows(NullPointerException.class, () -> new NotifyTableCreatedService(null));
    }

    @Test
    void notifyTableCreatedService_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class,
                () -> createdService.notifyTableCreated(null, "t1", "T", 6));
        assertThrows(NullPointerException.class,
                () -> createdService.notifyTableCreated(id, null, "T", 6));
        assertThrows(NullPointerException.class,
                () -> createdService.notifyTableCreated(id, "t1", null, 6));
        assertThrows(NullPointerException.class,
                () -> createdService.notifyTableCreated(id, "t1", "T", null));
    }

    @Test
    void notifyTableCreatedService_delegates() {
        UUID id = UUID.randomUUID();
        createdService.notifyTableCreated(id, "t1", "T", 6);
        verify(publisher, times(1)).publishTableCreated(eq(id), eq("t1"), eq("T"), eq(6));
    }

    @Test
    void notifyPlayerJoinedService_nullPublisherThrows() {
        assertThrows(NullPointerException.class, () -> new NotifyPlayerJoinedService(null));
    }

    @Test
    void notifyPlayerJoinedService_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class,
                () -> joinedService.notifyPlayerJoined(null, "t1", "Alice", 1, 5));
        assertThrows(NullPointerException.class,
                () -> joinedService.notifyPlayerJoined(id, null, "Alice", 1, 5));
        assertThrows(NullPointerException.class,
                () -> joinedService.notifyPlayerJoined(id, "t1", null, 1, 5));
        assertThrows(NullPointerException.class,
                () -> joinedService.notifyPlayerJoined(id, "t1", "Alice", null, 5));
        assertThrows(NullPointerException.class,
                () -> joinedService.notifyPlayerJoined(id, "t1", "Alice", 1, null));
    }

    @Test
    void notifyPlayerJoinedService_delegates() {
        UUID id = UUID.randomUUID();
        joinedService.notifyPlayerJoined(id, "t1", "Alice", 2, 4);
        verify(publisher, times(1)).publishPlayerJoined(eq(id), eq("t1"), eq("Alice"), eq(2), eq(4));
    }

    @Test
    void notifyTableClosedService_nullPublisherThrows() {
        assertThrows(NullPointerException.class, () -> new NotifyTableClosedService(null));
    }

    @Test
    void notifyTableClosedService_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class, () -> closedService.notifyTableClosed(null, "t1"));
        assertThrows(NullPointerException.class, () -> closedService.notifyTableClosed(id, null));
    }

    @Test
    void notifyTableClosedService_delegates() {
        UUID id = UUID.randomUUID();
        closedService.notifyTableClosed(id, "t1");
        verify(publisher, times(1)).publishTableClosed(eq(id), eq("t1"));
    }
}