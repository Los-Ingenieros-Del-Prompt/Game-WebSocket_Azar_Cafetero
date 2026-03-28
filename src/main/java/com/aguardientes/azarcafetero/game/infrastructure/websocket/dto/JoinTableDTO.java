package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinTableDTO {
    @JsonProperty("playerId")
    private String playerId;

    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("balance")
    private Double balance;

    public JoinTableDTO() {
    }

    public JoinTableDTO(String playerId, String playerName, String tableId) {
        this(playerId, playerName, tableId, 0.0);
    }

    public JoinTableDTO(String playerId, String playerName, String tableId, Double balance) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.tableId = tableId;
        this.balance = balance;
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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "JoinTableDTO{" +
                "playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", tableId='" + tableId + '\'' +
                ", balance=" + balance +
                '}';
    }
}
