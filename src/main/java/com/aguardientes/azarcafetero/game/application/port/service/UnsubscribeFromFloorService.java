package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.UnsubscribeFromFloorUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.FloorSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UnsubscribeFromFloorService implements UnsubscribeFromFloorUseCase {
    private final FloorSubscriptionRepository subscriptionRepository;

    public UnsubscribeFromFloorService(FloorSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = Objects.requireNonNull(subscriptionRepository, "SubscriptionRepository cannot be null");
    }

    @Override
    public void unsubscribeFromFloor(UUID floorId, String playerSessionId) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(playerSessionId, "Player session id cannot be null");

        subscriptionRepository.unsubscribe(floorId, playerSessionId);
    }
}
