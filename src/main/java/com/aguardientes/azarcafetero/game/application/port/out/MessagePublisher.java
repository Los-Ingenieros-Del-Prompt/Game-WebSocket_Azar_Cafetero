package com.aguardientes.azarcafetero.game.application.port.out;

import com.aguardientes.azarcafetero.game.domain.TableMessage;

public interface MessagePublisher {
    void publishToTable(TableMessage message);
}
