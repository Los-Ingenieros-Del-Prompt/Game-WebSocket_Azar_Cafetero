package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.SubscribeToFloorUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.FloorSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class SubscribeToFloorService implements SubscribeToFloorUseCase {
    private final FloorSubscriptionRepository subscriptionRepository;

    public SubscribeToFloorService(FloorSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = Objects.requireNonNull(subscriptionRepository, "SubscriptionRepository cannot be null");
    }

    @Override
    public void subscribeToFloor(UUID floorId, String playerSessionId) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(playerSessionId, "Player session id cannot be null");

        subscriptionRepository.subscribe(floorId, playerSessionId);
    }
}
