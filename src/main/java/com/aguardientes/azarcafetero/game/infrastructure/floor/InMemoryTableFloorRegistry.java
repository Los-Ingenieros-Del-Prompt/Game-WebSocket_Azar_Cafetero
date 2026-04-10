package com.aguardientes.azarcafetero.game.infrastructure.floor;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryTableFloorRegistry {
    private final ConcurrentMap<String, UUID> tableToFloor = new ConcurrentHashMap<>();

    public void register(String tableId, UUID floorId) {
        if (tableId == null || tableId.isBlank()) {
            throw new IllegalArgumentException("Table id cannot be null or blank");
        }
        if (floorId == null) {
            throw new IllegalArgumentException("Floor id cannot be null");
        }
        tableToFloor.put(tableId, floorId);
    }

    public Optional<UUID> findFloorId(String tableId) {
        if (tableId == null || tableId.isBlank()) {
            throw new IllegalArgumentException("Table id cannot be null or blank");
        }
        return Optional.ofNullable(tableToFloor.get(tableId));
    }

    public void remove(String tableId) {
        if (tableId == null || tableId.isBlank()) {
            throw new IllegalArgumentException("Table id cannot be null or blank");
        }
        tableToFloor.remove(tableId);
    }
}
