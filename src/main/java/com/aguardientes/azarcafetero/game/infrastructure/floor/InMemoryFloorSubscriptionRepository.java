package com.aguardientes.azarcafetero.game.infrastructure.floor;

import com.aguardientes.azarcafetero.game.application.port.out.FloorSubscriptionRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFloorSubscriptionRepository implements FloorSubscriptionRepository {
    private final Map<UUID, Set<String>> floorSubscriptions = new ConcurrentHashMap<>();

    @Override
    public void subscribe(UUID floorId, String playerSessionId) {
        if (floorId == null || playerSessionId == null || playerSessionId.isBlank()) {
            throw new IllegalArgumentException("Floor id and player session id cannot be null or blank");
        }
        floorSubscriptions.computeIfAbsent(floorId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(playerSessionId);
    }

    @Override
    public void unsubscribe(UUID floorId, String playerSessionId) {
        if (floorId == null || playerSessionId == null || playerSessionId.isBlank()) {
            throw new IllegalArgumentException("Floor id and player session id cannot be null or blank");
        }
        Set<String> subscribers = floorSubscriptions.get(floorId);
        if (subscribers != null) {
            subscribers.remove(playerSessionId);
            if (subscribers.isEmpty()) {
                floorSubscriptions.remove(floorId);
            }
        }
    }

    @Override
    public Set<String> getSubscribers(UUID floorId) {
        if (floorId == null) {
            throw new IllegalArgumentException("Floor id cannot be null");
        }
        Set<String> subscribers = floorSubscriptions.get(floorId);
        if (subscribers == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new ConcurrentHashMap<String, Boolean>() {{
            subscribers.forEach(s -> put(s, true));
        }}.keySet());
    }

    @Override
    public void clearFloorSubscriptions(UUID floorId) {
        if (floorId == null) {
            throw new IllegalArgumentException("Floor id cannot be null");
        }
        floorSubscriptions.remove(floorId);
    }

    public Map<UUID, Set<String>> getAllSubscriptions() {
        return Collections.unmodifiableMap(new ConcurrentHashMap<>(floorSubscriptions));
    }
}
