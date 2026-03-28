package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.LeaveTableUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Player;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LeaveTableService implements LeaveTableUseCase {
    private final TableSessionRepository sessionRepository;

    public LeaveTableService(TableSessionRepository sessionRepository) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "SessionRepository cannot be null");
    }

    @Override
    public void leaveTable(String tableId, Player player) {
        Objects.requireNonNull(tableId, "Table id cannot be null");
        Objects.requireNonNull(player, "Player cannot be null");

        TableSession session = sessionRepository.findById(tableId).orElse(null);
        if (session != null) {
            session.removePlayer(player);
            if (session.isEmpty()) {
                sessionRepository.deleteById(tableId);
            }
        }
    }
}
