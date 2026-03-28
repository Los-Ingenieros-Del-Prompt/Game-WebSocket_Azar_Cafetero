package com.aguardientes.azarcafetero.game.application.port.in;

import java.util.UUID;

public interface UnsubscribeFromFloorUseCase {
    void unsubscribeFromFloor(UUID floorId, String playerSessionId);
}
