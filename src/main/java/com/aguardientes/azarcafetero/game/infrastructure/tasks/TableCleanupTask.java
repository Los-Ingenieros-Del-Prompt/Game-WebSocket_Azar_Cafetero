package com.aguardientes.azarcafetero.game.infrastructure.tasks;

import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableClosedUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TableCleanupTask {
    private static final Logger logger = LoggerFactory.getLogger(TableCleanupTask.class);
    private static final long EMPTY_TABLE_TIMEOUT_MS = 60000; // 1 minute

    private final TableSessionRepository sessionRepository;
    private final NotifyTableClosedUseCase notifyTableClosedUseCase;

    public TableCleanupTask(TableSessionRepository sessionRepository, NotifyTableClosedUseCase notifyTableClosedUseCase) {
        this.sessionRepository = sessionRepository;
        this.notifyTableClosedUseCase = notifyTableClosedUseCase;
    }

    /**
     * Runs every minute to clean up empty tables that have been abandoned.
     */
    @Scheduled(fixedRate = 60000)
    public void cleanupEmptyTables() {
        logger.debug("Running empty table cleanup task...");
        List<TableSession> allSessions = sessionRepository.findAll();
        long now = System.currentTimeMillis();

        for (TableSession session : allSessions) {
            if (session.isEmpty()) {
                long idleTime = now - session.getCreatedAt();
                // We only delete if it's been empty for a while, 
                // to avoid deleting a table that was JUST created but hasn't been joined yet.
                if (idleTime > EMPTY_TABLE_TIMEOUT_MS) {
                    logger.info("Cleaning up abandoned empty table: {} (Idle for {}ms)", 
                            session.getTableId(), idleTime);
                    
                    String tableId = session.getTableId();
                    String floorId = session.getTable().getFloorId();
                    
                    sessionRepository.deleteById(tableId);
                    notifyTableClosedUseCase.notifyTableClosed(UUID.fromString(floorId), tableId);
                }
            }
        }
    }
}
