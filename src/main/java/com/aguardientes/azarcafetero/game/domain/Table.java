package com.aguardientes.azarcafetero.game.domain;

import java.util.Objects;

public class Table {
    private final String id;
    private final String name;
    private final double requiredBet;
    private final int maxPlayers;
    private final String floorId;

    public Table(String id, String name, String floorId) {
        this(id, name, 0, 6, floorId);
    }

    public Table(String id, String name, double requiredBet, String floorId) {
        this(id, name, requiredBet, 6, floorId);
    }

    public Table(String id, String name, double requiredBet, int maxPlayers, String floorId) {
        this.id = Objects.requireNonNull(id, "Table id cannot be null");
        this.name = Objects.requireNonNull(name, "Table name cannot be null");
        this.floorId = Objects.requireNonNull(floorId, "Floor id cannot be null");
        if (requiredBet < 0) throw new IllegalArgumentException("Required bet must be positive");
        if (maxPlayers <= 0) throw new IllegalArgumentException("Max players must be positive");
        this.requiredBet = requiredBet;
        this.maxPlayers = maxPlayers;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getRequiredBet() { 
        return requiredBet; 
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public String getFloorId() {
        return floorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(id, table.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", requiredBet=" + requiredBet +
                ", maxPlayers=" + maxPlayers +
                ", floorId='" + floorId + '\'' +
                '}';
    }


}
