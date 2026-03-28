package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableClosedEventRequest {
    @JsonProperty("tableId")
    private String tableId;

    public TableClosedEventRequest() {
    }

    public TableClosedEventRequest(String tableId) {
        this.tableId = tableId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
