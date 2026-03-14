package com.aguardientes.azarcafetero.game.application.port.out;

import java.util.Set;
import java.util.UUID;

public interface FloorSubscriptionRepository {
    void subscribe(UUID floorId, String playerSessionId);
    void unsubscribe(UUID floorId, String playerSessionId);
    Set<String> getSubscribers(UUID floorId);
    void clearFloorSubscriptions(UUID floorId);
}
