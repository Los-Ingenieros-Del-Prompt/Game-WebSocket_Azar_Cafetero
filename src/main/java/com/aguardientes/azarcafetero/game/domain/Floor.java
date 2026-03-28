package com.aguardientes.azarcafetero.game.domain;

import java.util.Objects;
import java.util.UUID;

public class Floor {
    private final UUID floorId;
    private final String name;

    public Floor(UUID floorId, String name) {
        this.floorId = Objects.requireNonNull(floorId, "Floor id cannot be null");
        this.name = Objects.requireNonNull(name, "Floor name cannot be null");
    }

    public UUID getFloorId() {
        return floorId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return Objects.equals(floorId, floor.floorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(floorId);
    }

    @Override
    public String toString() {
        return "Floor{" +
                "floorId=" + floorId +
                ", name='" + name + '\'' +
                '}';
    }
}
