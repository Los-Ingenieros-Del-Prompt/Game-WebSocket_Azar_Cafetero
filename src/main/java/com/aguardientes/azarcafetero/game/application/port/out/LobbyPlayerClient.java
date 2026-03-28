package com.aguardientes.azarcafetero.game.application.port.out;

import com.aguardientes.azarcafetero.game.infrastructure.client.dto.LobbyPlayerDTO;

public interface LobbyPlayerClient {
    LobbyPlayerDTO getPlayerById(String playerId);
}
