package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class SubscribeToFloorDTO {
    @JsonProperty("floorId")
    private UUID floorId;

    @JsonProperty("playerId")
    private String playerId;

    public SubscribeToFloorDTO() {
    }

    public SubscribeToFloorDTO(UUID floorId, String playerId) {
        this.floorId = floorId;
        this.playerId = playerId;
    }

    public UUID getFloorId() {
        return floorId;
    }

    public void setFloorId(UUID floorId) {
        this.floorId = floorId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "SubscribeToFloorDTO{" +
                "floorId=" + floorId +
                ", playerId='" + playerId + '\'' +
                '}';
    }
}
