package com.aguardientes.azarcafetero.game.application.port.in;

import java.util.UUID;

public interface SubscribeToFloorUseCase {
    void subscribeToFloor(UUID floorId, String playerSessionId);
}
