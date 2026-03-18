package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateTableRequest {
    @JsonProperty("tableName")
    private String tableName;

    @JsonProperty("requiredBet")
    private Double requiredBet;

    public CreateTableRequest() {
    }

    public CreateTableRequest(String tableName, Double requiredBet) {
        this.tableName = tableName;
        this.requiredBet = requiredBet;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Double getRequiredBet() {
        return requiredBet;
    }

    public void setRequiredBet(Double requiredBet) {
        this.requiredBet = requiredBet;
    }

    @Override
    public String toString() {
        return "CreateTableRequest{" +
                "tableName='" + tableName + '\'' +
                ", requiredBet=" + requiredBet +
                '}';
    }
}
