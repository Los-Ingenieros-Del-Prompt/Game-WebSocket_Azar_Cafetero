package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public class TableCreatedEventDTO {
    @JsonProperty("eventType")
    private String eventType = "TABLE_CREATED";

    @JsonProperty("floorId")
    private UUID floorId;

    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("tableName")
    private String tableName;

    @JsonProperty("maxPlayers")
    private Integer maxPlayers;

    @JsonProperty("availableSeats")
    private Integer availableSeats;

    @JsonProperty("timestamp")
    private Instant timestamp;

    public TableCreatedEventDTO() {
    }

    public TableCreatedEventDTO(UUID floorId, String tableId, String tableName, 
                                Integer maxPlayers, Integer availableSeats) {
        this.floorId = floorId;
        this.tableId = tableId;
        this.tableName = tableName;
        this.maxPlayers = maxPlayers;
        this.availableSeats = availableSeats;
        this.timestamp = Instant.now();
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public UUID getFloorId() {
        return floorId;
    }

    public void setFloorId(UUID floorId) {
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

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TableCreatedEventDTO{" +
                "eventType='" + eventType + '\'' +
                ", floorId=" + floorId +
                ", tableId='" + tableId + '\'' +
                ", tableName='" + tableName + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", timestamp=" + timestamp +
                '}';
    }
}
