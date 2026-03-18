package com.aguardientes.azarcafetero.game.application.port.in;

import com.aguardientes.azarcafetero.game.domain.Table;

public interface CreateTableUseCase {
    Table createTable(String tableName, double requiredBet);
}
