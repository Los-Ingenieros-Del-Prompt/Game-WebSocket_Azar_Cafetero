package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableDTO {
    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("tableName")
    private String tableName;

    @JsonProperty("playerCount")
    private Integer playerCount;

    @JsonProperty("createdAt")
    private Long createdAt;

    @JsonProperty("requiredBet")
    private Double requiredBet;

    @JsonProperty("floorId")
    private String floorId;

    public TableDTO() {
    }

    public TableDTO(String tableId, String tableName, Integer playerCount, Long createdAt, String floorId) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.playerCount = playerCount;
        this.createdAt = createdAt;
        this.floorId = floorId;
    }

    public TableDTO(String tableId, String tableName, Integer playerCount, Long createdAt, Double requiredBet, String floorId) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.playerCount = playerCount;
        this.createdAt = createdAt;
        this.requiredBet = requiredBet;
        this.floorId = floorId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Double getRequiredBet() {
        return requiredBet;
    }

    public void setRequiredBet(Double requiredBet) {
        this.requiredBet = requiredBet;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    @Override
    public String toString() {
        return "TableDTO{" +
                "tableId='" + tableId + '\'' +
                ", tableName='" + tableName + '\'' +
                ", playerCount=" + playerCount +
                ", createdAt=" + createdAt +
                ", requiredBet=" + requiredBet +
                ", floorId='" + floorId + '\'' +
                '}';
    }
}
