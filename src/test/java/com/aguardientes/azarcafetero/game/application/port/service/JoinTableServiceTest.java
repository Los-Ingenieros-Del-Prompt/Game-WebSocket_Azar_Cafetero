package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Player;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JoinTableServiceTest {

    private TableSessionRepository sessionRepository;
    private JoinTableService service;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(TableSessionRepository.class);
        service = new JoinTableService(sessionRepository);
    }

    @Test
    void constructor_nullRepositoryThrows() {
        assertThrows(NullPointerException.class, () -> new JoinTableService(null));
    }

    @Test
    void joinTable_nullTableIdThrows() {
        Player player = new Player("p1", "Alice", "s1");
        assertThrows(NullPointerException.class, () -> service.joinTable(null, player));
    }

    @Test
    void joinTable_nullPlayerThrows() {
        assertThrows(NullPointerException.class, () -> service.joinTable("t1", null));
    }

    @Test
    void joinTable_addsPlayerSuccessfully() {
        Table table = new Table("t1", "Table 1", "floor-1");
        TableSession session = new TableSession(table);
        when(sessionRepository.getOrCreate(eq("t1"), any(Table.class))).thenReturn(session);

        Player player = new Player("p1", "Alice", "s1", 100.0);
        service.joinTable("t1", player);

        assertEquals(1, session.getPlayerCount());
        assertTrue(session.hasPlayer("p1"));
    }

    @Test
    void joinTable_throwsWhenTableIsFull() {
        Table table = new Table("t1", "Table 1", 0, 2, "floor-1");
        TableSession session = new TableSession(table);
        session.addPlayer(new Player("p1", "Alice", "s1"));
        session.addPlayer(new Player("p2", "Bob", "s2"));
        when(sessionRepository.getOrCreate(eq("t1"), any(Table.class))).thenReturn(session);

        Player player = new Player("p3", "Charlie", "s3");
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.joinTable("t1", player));
        assertTrue(ex.getMessage().contains("full"));
    }

    @Test
    void joinTable_throwsWhenInsufficientBalance() {
        Table table = new Table("t1", "Table 1", 100.0, 4, "floor-1");
        TableSession session = new TableSession(table);
        when(sessionRepository.getOrCreate(eq("t1"), any(Table.class))).thenReturn(session);

        Player player = new Player("p1", "Alice", "s1", 50.0);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.joinTable("t1", player));
        assertTrue(ex.getMessage().contains("Insufficient balance"));
    }

    @Test
    void joinTable_acceptsPlayerWithExactBalance() {
        Table table = new Table("t1", "Table 1", 100.0, 4, "floor-1");
        TableSession session = new TableSession(table);
        when(sessionRepository.getOrCreate(eq("t1"), any(Table.class))).thenReturn(session);

        Player player = new Player("p1", "Alice", "s1", 100.0);
        assertDoesNotThrow(() -> service.joinTable("t1", player));
        assertEquals(1, session.getPlayerCount());
    }
}