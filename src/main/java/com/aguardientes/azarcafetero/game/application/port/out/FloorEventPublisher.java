package com.aguardientes.azarcafetero.game.application.port.out;

import com.aguardientes.azarcafetero.game.domain.FloorEvent;

import java.util.UUID;

public interface FloorEventPublisher {
    void publishTableCreated(UUID floorId, String tableId, String tableName, Integer maxPlayers);
    void publishPlayerJoined(UUID floorId, String tableId, String playerName, Integer currentPlayers, Integer availableSeats);
    void publishTableClosed(UUID floorId, String tableId);
}
