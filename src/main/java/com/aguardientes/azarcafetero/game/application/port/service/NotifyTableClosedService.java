package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableClosedUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.FloorEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class NotifyTableClosedService implements NotifyTableClosedUseCase {
    private final FloorEventPublisher floorEventPublisher;

    public NotifyTableClosedService(FloorEventPublisher floorEventPublisher) {
        this.floorEventPublisher = Objects.requireNonNull(floorEventPublisher, "FloorEventPublisher cannot be null");
    }

    @Override
    public void notifyTableClosed(UUID floorId, String tableId) {
        Objects.requireNonNull(floorId, "Floor id cannot be null");
        Objects.requireNonNull(tableId, "Table id cannot be null");

        floorEventPublisher.publishTableClosed(floorId, tableId);
    }
}
