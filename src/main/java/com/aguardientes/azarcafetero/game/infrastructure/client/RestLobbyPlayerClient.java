package com.aguardientes.azarcafetero.game.infrastructure.client;

import com.aguardientes.azarcafetero.game.application.port.out.LobbyPlayerClient;
import com.aguardientes.azarcafetero.game.infrastructure.client.dto.LobbyPlayerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestLobbyPlayerClient implements LobbyPlayerClient {

    private final RestTemplate restTemplate;
    private final String lobbyServiceUrl;

    public RestLobbyPlayerClient(
            RestTemplate restTemplate,
            @Value("${lobby.service.url:http://localhost:8082}") String lobbyServiceUrl) {
        this.restTemplate = restTemplate;
        this.lobbyServiceUrl = lobbyServiceUrl;
    }

    @Override
    public LobbyPlayerDTO getPlayerById(String playerId) {
        String url = lobbyServiceUrl + "/api/player/" + playerId + "/internal";
        
        try {
            return restTemplate.getForObject(url, LobbyPlayerDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Player not found in Lobby service: " + playerId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch player from Lobby service: " + e.getMessage(), e);
        }
    }
}
