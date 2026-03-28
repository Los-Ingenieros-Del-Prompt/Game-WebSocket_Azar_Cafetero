package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public class PlayerJoinedEventDTO {
    @JsonProperty("eventType")
    private String eventType = "PLAYER_JOINED";

    @JsonProperty("floorId")
    private UUID floorId;

    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("currentPlayers")
    private Integer currentPlayers;

    @JsonProperty("availableSeats")
    private Integer availableSeats;

    @JsonProperty("timestamp")
    private Instant timestamp;

    public PlayerJoinedEventDTO() {
    }

    public PlayerJoinedEventDTO(UUID floorId, String tableId, String playerName, 
                                Integer currentPlayers, Integer availableSeats) {
        this.floorId = floorId;
        this.tableId = tableId;
        this.playerName = playerName;
        this.currentPlayers = currentPlayers;
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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(Integer currentPlayers) {
        this.currentPlayers = currentPlayers;
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
        return "PlayerJoinedEventDTO{" +
                "eventType='" + eventType + '\'' +
                ", floorId=" + floorId +
                ", tableId='" + tableId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", currentPlayers=" + currentPlayers +
                ", availableSeats=" + availableSeats +
                ", timestamp=" + timestamp +
                '}';
    }
}
