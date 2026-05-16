package com.aguardientes.azarcafetero.game.service;

import com.aguardientes.azarcafetero.game.application.port.out.MessagePublisher;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.application.port.service.BroadcastMessageService;
import com.aguardientes.azarcafetero.game.application.port.service.CreateTableService;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableMessage;
import com.aguardientes.azarcafetero.game.infrastructure.messaging.TableSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Service unit tests")
class ServiceUnitTest {

    // ── CreateTableService ────────────────────────────────────────────────────

    private TableSessionRepository mockRepo;
    private CreateTableService createTableService;

    @BeforeEach
    void setUp() {
        mockRepo = mock(TableSessionRepository.class);
        createTableService = new CreateTableService(mockRepo);
    }

    @Test
    @DisplayName("CreateTableService — null name throws")
    void createTable_nullNameThrows() {
        assertThrows(NullPointerException.class,
                () -> createTableService.createTable(null, 10, 4, "floor-1"));
    }

    @Test
    @DisplayName("CreateTableService — blank name throws")
    void createTable_blankNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> createTableService.createTable("  ", 10, 4, "floor-1"));
    }

    @Test
    @DisplayName("CreateTableService — null floorId throws")
    void createTable_nullFloorThrows() {
        assertThrows(NullPointerException.class,
                () -> createTableService.createTable("Table", 10, 4, null));
    }

    @Test
    @DisplayName("CreateTableService — blank floorId throws")
    void createTable_blankFloorThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> createTableService.createTable("Table", 10, 4, "  "));
    }

    @Test
    @DisplayName("CreateTableService — zero bet throws")
    void createTable_zeroBetThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> createTableService.createTable("Table", 0, 4, "floor-1"));
    }

    @Test
    @DisplayName("CreateTableService — zero maxPlayers throws")
    void createTable_zeroMaxPlayersThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> createTableService.createTable("Table", 10, 0, "floor-1"));
    }

    @Test
    @DisplayName("CreateTableService — valid args creates and saves table")
    void createTable_validArgs() {
        Table result = createTableService.createTable("Poker Room", 50, 6, "floor-1");
        assertNotNull(result);
        assertEquals("Poker Room", result.getName());
        assertEquals(50, result.getRequiredBet());
        assertEquals(6, result.getMaxPlayers());
        assertEquals("floor-1", result.getFloorId());
        verify(mockRepo, times(1)).save(any());
    }

    // ── BroadcastMessageService ───────────────────────────────────────────────

    @Test
    @DisplayName("BroadcastMessageService — null message throws")
    void broadcastMessage_nullThrows() {
        MessagePublisher publisher = mock(MessagePublisher.class);
        BroadcastMessageService service = new BroadcastMessageService(publisher);
        assertThrows(NullPointerException.class, () -> service.broadcastMessage(null));
    }

    @Test
    @DisplayName("BroadcastMessageService — delegates to publisher")
    void broadcastMessage_delegates() {
        MessagePublisher publisher = mock(MessagePublisher.class);
        BroadcastMessageService service = new BroadcastMessageService(publisher);
        TableMessage msg = new TableMessage("p1", "t1", "hi", Instant.now());
        service.broadcastMessage(msg);
        verify(publisher, times(1)).publishToTable(msg);
    }

    // ── TableSessionManager ───────────────────────────────────────────────────

    @Test
    @DisplayName("TableSessionManager — save null throws")
    void sessionManager_saveNullThrows() {
        TableSessionManager mgr = new TableSessionManager();
        assertThrows(IllegalArgumentException.class, () -> mgr.save(null));
    }

    @Test
    @DisplayName("TableSessionManager — findById null throws")
    void sessionManager_findByIdNullThrows() {
        TableSessionManager mgr = new TableSessionManager();
        assertThrows(IllegalArgumentException.class, () -> mgr.findById(null));
        assertThrows(IllegalArgumentException.class, () -> mgr.findById("  "));
    }

    @Test
    @DisplayName("TableSessionManager — deleteById null throws")
    void sessionManager_deleteByIdNullThrows() {
        TableSessionManager mgr = new TableSessionManager();
        assertThrows(IllegalArgumentException.class, () -> mgr.deleteById(null));
        assertThrows(IllegalArgumentException.class, () -> mgr.deleteById("  "));
    }

    @Test
    @DisplayName("TableSessionManager — existsById null throws")
    void sessionManager_existsByIdNullThrows() {
        TableSessionManager mgr = new TableSessionManager();
        assertThrows(IllegalArgumentException.class, () -> mgr.existsById(null));
    }

    @Test
    @DisplayName("TableSessionManager — getOrCreate null tableId throws")
    void sessionManager_getOrCreateNullIdThrows() {
        TableSessionManager mgr = new TableSessionManager();
        Table table = new Table("t1", "T", "f1");
        assertThrows(IllegalArgumentException.class, () -> mgr.getOrCreate(null, table));
        assertThrows(IllegalArgumentException.class, () -> mgr.getOrCreate("  ", table));
    }

    @Test
    @DisplayName("TableSessionManager — getOrCreate null table throws")
    void sessionManager_getOrCreateNullTableThrows() {
        TableSessionManager mgr = new TableSessionManager();
        assertThrows(IllegalArgumentException.class, () -> mgr.getOrCreate("t1", null));
    }

    @Test
    @DisplayName("TableSessionManager — getAllSessions, getActiveTables, clearAllSessions, findAll")
    void sessionManager_utilitMethods() {
        TableSessionManager mgr = new TableSessionManager();
        Table table = new Table("t1", "T", "f1");
        mgr.getOrCreate("t1", table);

        assertEquals(1, mgr.getActiveTables());
        assertEquals(1, mgr.getAllSessions().size());
        assertEquals(1, mgr.findAll().size());

        mgr.clearAllSessions();
        assertEquals(0, mgr.getActiveTables());
        assertTrue(mgr.findAll().isEmpty());
    }
}
