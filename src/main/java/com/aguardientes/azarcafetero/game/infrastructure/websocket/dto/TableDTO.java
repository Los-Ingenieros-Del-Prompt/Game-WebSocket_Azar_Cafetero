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

    public TableDTO() {
    }

    public TableDTO(String tableId, String tableName, Integer playerCount, Long createdAt) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.playerCount = playerCount;
        this.createdAt = createdAt;
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

    @Override
    public String toString() {
        return "TableDTO{" +
                "tableId='" + tableId + '\'' +
                ", tableName='" + tableName + '\'' +
                ", playerCount=" + playerCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
