package com.aguardientes.azarcafetero.game.application.port.in;

import com.aguardientes.azarcafetero.game.domain.FloorEvent;

import java.util.UUID;

public interface NotifyTableCreatedUseCase {
    void notifyTableCreated(UUID floorId, String tableId, String tableName, Integer maxPlayers);
}
