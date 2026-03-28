package com.aguardientes.azarcafetero.game.domain;

import java.time.Instant;
import java.util.Objects;

public class TableMessage {
    private final String playerId;
    private final String tableId;
    private final String content;
    private final Instant timestamp;

    public TableMessage(String playerId, String tableId, String content, Instant timestamp) {
        this.playerId = Objects.requireNonNull(playerId, "Player id cannot be null");
        this.tableId = Objects.requireNonNull(tableId, "Table id cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getTableId() {
        return tableId;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableMessage that = (TableMessage) o;
        return Objects.equals(playerId, that.playerId) &&
                Objects.equals(tableId, that.tableId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, tableId, timestamp);
    }

    @Override
    public String toString() {
        return "TableMessage{" +
                "playerId='" + playerId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
