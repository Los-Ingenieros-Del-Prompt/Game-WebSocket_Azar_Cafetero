package com.aguardientes.azarcafetero.game.infrastructure.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class TableMessageDTO {
    @JsonProperty("playerId")
    private String playerId;

    @JsonProperty("tableId")
    private String tableId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("timestamp")
    private Instant timestamp;

    public TableMessageDTO() {
    }

    public TableMessageDTO(String playerId, String tableId, String content, Instant timestamp) {
        this.playerId = playerId;
        this.tableId = tableId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TableMessageDTO{" +
                "playerId='" + playerId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
