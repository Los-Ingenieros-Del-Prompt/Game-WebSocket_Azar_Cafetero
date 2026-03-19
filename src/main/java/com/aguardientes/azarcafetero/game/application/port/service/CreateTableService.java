package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.CreateTableUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class CreateTableService implements CreateTableUseCase {
    private final TableSessionRepository sessionRepository;

    public CreateTableService(TableSessionRepository sessionRepository) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "SessionRepository cannot be null");
    }

    @Override
    public Table createTable(String tableName, double requiredBet, int maxPlayers) {
        Objects.requireNonNull(tableName, "Table name cannot be null");
        
        if (tableName.isBlank()) {
            throw new IllegalArgumentException("Table name cannot be blank");
        }
        
        if (requiredBet <= 0) {
            throw new IllegalArgumentException("Required bet must be positive");
        }

        if (maxPlayers <= 0) {
            throw new IllegalArgumentException("Max players must be positive");
        }

        String tableId = UUID.randomUUID().toString();
        Table table = new Table(tableId, tableName, requiredBet, maxPlayers);
        TableSession session = new TableSession(table);
        sessionRepository.save(session);

        return table;
    }
}
