package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableCreatedEventRequest {
    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("tableName")
    private String tableName;

    @JsonProperty("maxPlayers")
    private Integer maxPlayers;

    public TableCreatedEventRequest() {
    }

    public TableCreatedEventRequest(String tableId, String tableName, Integer maxPlayers) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.maxPlayers = maxPlayers;
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

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
