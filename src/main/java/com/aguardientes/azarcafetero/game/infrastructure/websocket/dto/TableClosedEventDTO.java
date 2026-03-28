package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public class TableClosedEventDTO {
    @JsonProperty("eventType")
    private String eventType = "TABLE_CLOSED";

    @JsonProperty("floorId")
    private UUID floorId;

    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("timestamp")
    private Instant timestamp;

    public TableClosedEventDTO() {
    }

    public TableClosedEventDTO(UUID floorId, String tableId) {
        this.floorId = floorId;
        this.tableId = tableId;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TableClosedEventDTO{" +
                "eventType='" + eventType + '\'' +
                ", floorId=" + floorId +
                ", tableId='" + tableId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
