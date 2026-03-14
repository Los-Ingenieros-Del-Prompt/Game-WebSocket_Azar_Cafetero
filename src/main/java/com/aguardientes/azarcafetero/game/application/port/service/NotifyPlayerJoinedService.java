package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.NotifyPlayerJoinedUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.FloorEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class NotifyPlayerJoinedService implements NotifyPlayerJoinedUseCase {
    private final FloorEventPublisher floorEventPublisher;

    public NotifyPlayerJoinedService(FloorEventPublisher floorEventPublisher) {
        this.floorEventPublisher = Objects.requireNonNull(floorEventPublisher, "FloorEventPublisher cannot be null");
    }

    @Override
    public void notifyPlayerJoined(UUID floorId, String tableId, String playerName, Integer currentPlayers, Integer availableSeats) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(tableId, "Table id cannot be null");
        Objects.requireNonNull(playerName, "Player name cannot be null");
        Objects.requireNonNull(currentPlayers, "Current players cannot be null");
        Objects.requireNonNull(availableSeats, "Available seats cannot be null");

        floorEventPublisher.publishPlayerJoined(floorId, tableId, playerName, currentPlayers, availableSeats);
    }
}
