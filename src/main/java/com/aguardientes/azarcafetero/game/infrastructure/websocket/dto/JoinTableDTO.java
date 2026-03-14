package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinTableDTO {
    @JsonProperty("playerId")
    private String playerId;

    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("tableId")
    private String tableId;

    public JoinTableDTO() {
    }

    public JoinTableDTO(String playerId, String playerName, String tableId) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.tableId = tableId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @Override
    public String toString() {
        return "JoinTableDTO{" +
                "playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", tableId='" + tableId + '\'' +
                '}';
    }
}
