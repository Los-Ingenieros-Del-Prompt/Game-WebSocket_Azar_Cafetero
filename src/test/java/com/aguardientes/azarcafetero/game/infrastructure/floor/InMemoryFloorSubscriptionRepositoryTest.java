package com.aguardientes.azarcafetero.game.infrastructure.floor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFloorSubscriptionRepositoryTest {

    private InMemoryFloorSubscriptionRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryFloorSubscriptionRepository();
    }

    @Test
    void subscribe_nullFloorIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.subscribe(null, "s1"));
    }

    @Test
    void subscribe_nullSessionThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.subscribe(UUID.randomUUID(), null));
    }

    @Test
    void subscribe_blankSessionThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.subscribe(UUID.randomUUID(), "  "));
    }

    @Test
    void subscribe_addsSubscriber() {
        UUID floor = UUID.randomUUID();
        repo.subscribe(floor, "s1");
        assertTrue(repo.getSubscribers(floor).contains("s1"));
    }

    @Test
    void unsubscribe_nullFloorIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.unsubscribe(null, "s1"));
    }

    @Test
    void unsubscribe_nullSessionThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.unsubscribe(UUID.randomUUID(), null));
    }

    @Test
    void unsubscribe_blankSessionThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.unsubscribe(UUID.randomUUID(), " "));
    }

    @Test
    void unsubscribe_removesSubscriberAndCleansUpEmptyFloor() {
        UUID floor = UUID.randomUUID();
        repo.subscribe(floor, "s1");
        repo.unsubscribe(floor, "s1");
        assertTrue(repo.getSubscribers(floor).isEmpty());
    }

    @Test
    void unsubscribe_keepsFloorWithOtherSubscribers() {
        UUID floor = UUID.randomUUID();
        repo.subscribe(floor, "s1");
        repo.subscribe(floor, "s2");
        repo.unsubscribe(floor, "s1");
        Set<String> subs = repo.getSubscribers(floor);
        assertEquals(1, subs.size());
        assertTrue(subs.contains("s2"));
    }

    @Test
    void unsubscribe_nonExistentFloorIsNoop() {
        assertDoesNotThrow(() -> repo.unsubscribe(UUID.randomUUID(), "s1"));
    }

    @Test
    void getSubscribers_nullThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.getSubscribers(null));
    }

    @Test
    void getSubscribers_returnsEmptyWhenNoFloor() {
        assertTrue(repo.getSubscribers(UUID.randomUUID()).isEmpty());
    }

    @Test
    void clearFloorSubscriptions_nullThrows() {
        assertThrows(IllegalArgumentException.class, () -> repo.clearFloorSubscriptions(null));
    }

    @Test
    void clearFloorSubscriptions_removesAll() {
        UUID floor = UUID.randomUUID();
        repo.subscribe(floor, "s1");
        repo.subscribe(floor, "s2");
        repo.clearFloorSubscriptions(floor);
        assertTrue(repo.getSubscribers(floor).isEmpty());
    }

    @Test
    void getAllSubscriptions_returnsSnapshot() {
        UUID floor = UUID.randomUUID();
        repo.subscribe(floor, "s1");
        assertEquals(1, repo.getAllSubscriptions().size());
    }
}