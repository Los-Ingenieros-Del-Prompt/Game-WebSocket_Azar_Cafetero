package com.aguardientes.azarcafetero.game.application.port.in;

import com.aguardientes.azarcafetero.game.domain.TableMessage;

public interface SendMessageUseCase {
    void sendMessage(TableMessage message);
}
