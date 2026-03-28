package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerJoinedEventRequest {
    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("currentPlayers")
    private Integer currentPlayers;

    @JsonProperty("availableSeats")
    private Integer availableSeats;

    public PlayerJoinedEventRequest() {
    }

    public PlayerJoinedEventRequest(String tableId, String playerName, Integer currentPlayers, Integer availableSeats) {
        this.tableId = tableId;
        this.playerName = playerName;
        this.currentPlayers = currentPlayers;
        this.availableSeats = availableSeats;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(Integer currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }
}
