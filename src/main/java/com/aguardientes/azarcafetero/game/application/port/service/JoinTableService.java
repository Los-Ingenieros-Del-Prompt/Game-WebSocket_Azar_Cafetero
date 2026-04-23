package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.JoinTableUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Player;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class JoinTableService implements JoinTableUseCase {
    private final TableSessionRepository sessionRepository;

    public JoinTableService(TableSessionRepository sessionRepository) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "SessionRepository cannot be null");
    }

    @Override
    public void joinTable(String tableId, Player player) {
        Objects.requireNonNull(tableId, "Table id cannot be null");
        Objects.requireNonNull(player, "Player cannot be null");

        // Atomically get or create the session to prevent race conditions
        Table table = new Table(tableId, "Table-" + tableId, "unknown");
        TableSession session = sessionRepository.getOrCreate(tableId, table);

        // Synchronize on the session to prevent concurrent modifications
        synchronized (session) {
            if (session.isFull()) {
                throw new IllegalStateException("Table is full");
            }

            double requiredBet = session.getTable().getRequiredBet();
            if (player.getBalance() < requiredBet) {
                throw new IllegalStateException(
                    String.format("Insufficient balance. Required: %.2f, Available: %.2f", 
                        requiredBet, player.getBalance())
                );
            }

            session.addPlayer(player);
        }
    }
}
