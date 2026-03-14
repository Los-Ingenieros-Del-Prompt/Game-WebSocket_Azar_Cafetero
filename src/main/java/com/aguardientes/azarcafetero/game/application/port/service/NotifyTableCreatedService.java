package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableCreatedUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.FloorEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class NotifyTableCreatedService implements NotifyTableCreatedUseCase {
    private final FloorEventPublisher floorEventPublisher;

    public NotifyTableCreatedService(FloorEventPublisher floorEventPublisher) {
        this.floorEventPublisher = Objects.requireNonNull(floorEventPublisher, "FloorEventPublisher cannot be null");
    }

    @Override
    public void notifyTableCreated(UUID floorId, String tableId, String tableName, Integer maxPlayers) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(tableId, "Table id cannot be null");
        Objects.requireNonNull(tableName, "Table name cannot be null");
        Objects.requireNonNull(maxPlayers, "Max players cannot be null");

        floorEventPublisher.publishTableCreated(floorId, tableId, tableName, maxPlayers);
    }
}
