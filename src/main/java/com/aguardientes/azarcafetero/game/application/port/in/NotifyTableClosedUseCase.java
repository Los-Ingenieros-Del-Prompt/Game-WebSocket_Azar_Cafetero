package com.aguardientes.azarcafetero.game.application.port.in;

import java.util.UUID;

public interface NotifyTableClosedUseCase {
    void notifyTableClosed(UUID floorId, String tableId);
}
