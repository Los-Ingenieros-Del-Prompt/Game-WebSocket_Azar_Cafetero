package com.aguardientes.azarcafetero.game.infrastructure.messaging;

import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TableSessionManager implements TableSessionRepository {
    private final Map<String, TableSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void save(TableSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        sessions.put(session.getTableId(), session);
    }

    @Override
    public Optional<TableSession> findById(String tableId) {
        if (tableId == null || tableId.isBlank()) {
            throw new IllegalArgumentException("Table id cannot be null or blank");
        }
        return Optional.ofNullable(sessions.get(tableId));
    }

    /**
     * Atomically get or create a session for the given table.
     * This prevents race conditions when multiple players try to join a new table simultaneously.
     */
    public TableSession getOrCreate(String tableId, Table table) {
        if (tableId == null || tableId.isBlank()) {
            throw new IllegalArgumentException("Table id cannot be null or blank");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        return sessions.computeIfAbsent(tableId, id -> new TableSession(table));
    }

    @Override
    public void deleteById(String tableId) {
        if (tableId == null || tableId.isBlank()) {
            throw new IllegalArgumentException("Table id cannot be null or blank");
        }
        sessions.remove(tableId);
    }

    @Override
    public boolean existsById(String tableId) {
        if (tableId == null || tableId.isBlank()) {
            throw new IllegalArgumentException("Table id cannot be null or blank");
        }
        return sessions.containsKey(tableId);
    }

    public Map<String, TableSession> getAllSessions() {
        return Collections.unmodifiableMap(new ConcurrentHashMap<>(sessions));
    }

    public int getActiveTables() {
        return sessions.size();
    }

    public void clearAllSessions() {
        sessions.clear();
    }
}
