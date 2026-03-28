package com.aguardientes.azarcafetero.game.application.port.out;

import com.aguardientes.azarcafetero.game.domain.TableSession;

import java.util.Optional;

public interface TableSessionRepository {
    void save(TableSession session);
    Optional<TableSession> findById(String tableId);
    void deleteById(String tableId);
    boolean existsById(String tableId);
}
