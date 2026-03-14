package com.aguardientes.azarcafetero.game.application.port.in;

import java.util.UUID;

public interface NotifyPlayerJoinedUseCase {
    void notifyPlayerJoined(UUID floorId, String tableId, String playerName, Integer currentPlayers, Integer availableSeats);
}
