package com.aguardientes.azarcafetero.game.domain;

import java.util.Objects;

public class Player {
    private final String id;
    private final String name;
    private final String sessionId;
    private final double balance;

    public Player(String id, String name, String sessionId) {
        this(id, name, sessionId, 0.0);
    }

    public Player(String id, String name, String sessionId, double balance) {
        this.id = Objects.requireNonNull(id, "Player id cannot be null");
        this.name = Objects.requireNonNull(name, "Player name cannot be null");
        this.sessionId = Objects.requireNonNull(sessionId, "Session id cannot be null");
        if (balance < 0) {
            throw new IllegalArgumentException("Player balance cannot be negative");
        }
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) &&
                Objects.equals(sessionId, player.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sessionId);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", balance=" + balance +
                '}';
    }
}
