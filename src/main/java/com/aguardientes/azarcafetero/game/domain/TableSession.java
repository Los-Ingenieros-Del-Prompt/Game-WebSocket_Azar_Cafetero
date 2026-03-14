package com.aguardientes.azarcafetero.game.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TableSession {
    private final Table table;
    private final Set<Player> players;
    private final long createdAt;

    public TableSession(Table table) {
        this.table = Objects.requireNonNull(table, "Table cannot be null");
        this.players = Collections.synchronizedSet(new HashSet<>());
        this.createdAt = System.currentTimeMillis();
    }

    public Table getTable() {
        return table;
    }

    public String getTableId() {
        return table.getId();
    }

    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        players.add(player);
    }

    public void removePlayer(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        players.remove(player);
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players));
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean hasPlayer(String playerId) {
        return players.stream()
                .anyMatch(p -> p.getId().equals(playerId));
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableSession that = (TableSession) o;
        return Objects.equals(table, that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table);
    }

    @Override
    public String toString() {
        return "TableSession{" +
                "table=" + table +
                ", playerCount=" + players.size() +
                ", createdAt=" + createdAt +
                '}';
    }
}
