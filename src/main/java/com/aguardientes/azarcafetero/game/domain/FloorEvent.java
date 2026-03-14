package com.aguardientes.azarcafetero.game.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class FloorEvent {
    public enum Type {
        TABLE_CREATED,
        PLAYER_JOINED,
        TABLE_CLOSED
    }

    private final UUID floorId;
    private final String tableId;
    private final Type type;
    private final String message;
    private final Instant timestamp;
    private final Integer availableSeats;
    private final Integer totalPlayers;

    public FloorEvent(UUID floorId, String tableId, Type type, String message, 
                      Instant timestamp, Integer availableSeats, Integer totalPlayers) {
        this.floorId = Objects.requireNonNull(floorId, "Floor id cannot be null");
        this.tableId = Objects.requireNonNull(tableId, "Table id cannot be null");
        this.type = Objects.requireNonNull(type, "Event type cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.availableSeats = availableSeats;
        this.totalPlayers = totalPlayers;
    }

    public UUID getFloorId() {
        return floorId;
    }

    public String getTableId() {
        return tableId;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public Integer getTotalPlayers() {
        return totalPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloorEvent that = (FloorEvent) o;
        return Objects.equals(floorId, that.floorId) &&
                Objects.equals(tableId, that.tableId) &&
                type == that.type &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(floorId, tableId, type, timestamp);
    }

    @Override
    public String toString() {
        return "FloorEvent{" +
                "floorId=" + floorId +
                ", tableId='" + tableId + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}
