package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.out.FloorSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SubscribeUnsubscribeServiceTest {

    private FloorSubscriptionRepository repository;
    private SubscribeToFloorService subscribeService;
    private UnsubscribeFromFloorService unsubscribeService;

    @BeforeEach
    void setUp() {
        repository = mock(FloorSubscriptionRepository.class);
        subscribeService = new SubscribeToFloorService(repository);
        unsubscribeService = new UnsubscribeFromFloorService(repository);
    }

    @Test
    void subscribe_nullRepoThrows() {
        assertThrows(NullPointerException.class, () -> new SubscribeToFloorService(null));
    }

    @Test
    void subscribe_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class, () -> subscribeService.subscribeToFloor(null, "s1"));
        assertThrows(NullPointerException.class, () -> subscribeService.subscribeToFloor(id, null));
    }

    @Test
    void subscribe_delegatesToRepo() {
        UUID id = UUID.randomUUID();
        subscribeService.subscribeToFloor(id, "session-1");
        verify(repository, times(1)).subscribe(eq(id), eq("session-1"));
    }

    @Test
    void unsubscribe_nullRepoThrows() {
        assertThrows(NullPointerException.class, () -> new UnsubscribeFromFloorService(null));
    }

    @Test
    void unsubscribe_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class, () -> unsubscribeService.unsubscribeFromFloor(null, "s1"));
        assertThrows(NullPointerException.class, () -> unsubscribeService.unsubscribeFromFloor(id, null));
    }

    @Test
    void unsubscribe_delegatesToRepo() {
        UUID id = UUID.randomUUID();
        unsubscribeService.unsubscribeFromFloor(id, "session-1");
        verify(repository, times(1)).unsubscribe(eq(id), eq("session-1"));
    }
}