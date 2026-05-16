package com.aguardientes.azarcafetero.game.application.port.service;

import com.aguardientes.azarcafetero.game.application.port.in.NotifyTableClosedUseCase;
import com.aguardientes.azarcafetero.game.application.port.out.TableSessionRepository;
import com.aguardientes.azarcafetero.game.domain.Player;
import com.aguardientes.azarcafetero.game.domain.Table;
import com.aguardientes.azarcafetero.game.domain.TableSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LeaveTableServiceTest {

    private TableSessionRepository sessionRepository;
    private NotifyTableClosedUseCase notifyTableClosedUseCase;
    private LeaveTableService service;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(TableSessionRepository.class);
        notifyTableClosedUseCase = mock(NotifyTableClosedUseCase.class);
        service = new LeaveTableService(sessionRepository, notifyTableClosedUseCase);
    }

    @Test
    void constructor_nullRepositoryThrows() {
        assertThrows(NullPointerException.class,
                () -> new LeaveTableService(null, notifyTableClosedUseCase));
    }

    @Test
    void constructor_nullNotifyThrows() {
        assertThrows(NullPointerException.class,
                () -> new LeaveTableService(sessionRepository, null));
    }

    @Test
    void leaveTable_nullTableIdThrows() {
        Player player = new Player("p1", "Alice", "s1");
        assertThrows(NullPointerException.class, () -> service.leaveTable(null, player));
    }

    @Test
    void leaveTable_nullPlayerThrows() {
        assertThrows(NullPointerException.class, () -> service.leaveTable("t1", null));
    }

    @Test
    void leaveTable_doesNothingWhenSessionNotFound() {
        when(sessionRepository.findById("t1")).thenReturn(Optional.empty());

        Player player = new Player("p1", "Alice", "s1");
        service.leaveTable("t1", player);

        verify(sessionRepository, never()).deleteById(anyString());
        verifyNoInteractions(notifyTableClosedUseCase);
    }

    @Test
    void leaveTable_removesPlayerButKeepsSessionWhenNotEmpty() {
        Table table = new Table("t1", "T", "floor-1");
        TableSession session = new TableSession(table);
        session.addPlayer(new Player("p1", "Alice", "s1"));
        session.addPlayer(new Player("p2", "Bob", "s2"));
        when(sessionRepository.findById("t1")).thenReturn(Optional.of(session));

        service.leaveTable("t1", new Player("p1", "Alice", "s1"));

        assertEquals(1, session.getPlayerCount());
        verify(sessionRepository, never()).deleteById(anyString());
        verifyNoInteractions(notifyTableClosedUseCase);
    }

    @Test
    void leaveTable_deletesSessionAndNotifiesWhenLastPlayerLeaves() {
        UUID floorId = UUID.randomUUID();
        Table table = new Table("t1", "T", floorId.toString());
        TableSession session = new TableSession(table);
        session.addPlayer(new Player("p1", "Alice", "s1"));
        when(sessionRepository.findById("t1")).thenReturn(Optional.of(session));

        service.leaveTable("t1", new Player("p1", "Alice", "s1"));

        assertTrue(session.isEmpty());
        verify(sessionRepository, times(1)).deleteById("t1");
        verify(notifyTableClosedUseCase, times(1)).notifyTableClosed(eq(floorId), eq("t1"));
    }

    @Test
    void leaveTable_skipsNotificationWhenFloorIdIsNotValidUuid() {
        Table table = new Table("t1", "T", "unknown");
        TableSession session = new TableSession(table);
        session.addPlayer(new Player("p1", "Alice", "s1"));
        when(sessionRepository.findById("t1")).thenReturn(Optional.of(session));

        service.leaveTable("t1", new Player("p1", "Alice", "s1"));

        verify(sessionRepository, times(1)).deleteById("t1");
        verifyNoInteractions(notifyTableClosedUseCase);
    }
}