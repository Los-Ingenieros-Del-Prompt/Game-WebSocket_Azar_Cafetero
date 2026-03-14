package com.aguardientes.azarcafetero.game.application.port.in;

import com.aguardientes.azarcafetero.game.domain.Player;

public interface LeaveTableUseCase {
    void leaveTable(String tableId, Player player);
}
