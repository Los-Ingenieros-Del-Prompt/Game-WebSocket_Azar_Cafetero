package com.aguardientes.azarcafetero.game.application.port.in;

import com.aguardientes.azarcafetero.game.domain.Player;

public interface JoinTableUseCase {
    void joinTable(String tableId, Player player);
}
