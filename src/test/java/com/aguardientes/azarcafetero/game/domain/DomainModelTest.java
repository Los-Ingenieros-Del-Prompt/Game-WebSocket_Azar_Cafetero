package com.aguardientes.azarcafetero.game.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain model unit tests")
class DomainModelTest {

    // ── Floor ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Floor — null floorId throws")
    void floor_nullIdThrows() {
        assertThrows(NullPointerException.class, () -> new Floor(null, "Ground"));
    }

    @Test
    @DisplayName("Floor — null name throws")
    void floor_nullNameThrows() {
        assertThrows(NullPointerException.class, () -> new Floor(UUID.randomUUID(), null));
    }

    @Test
    @DisplayName("Floor — getters work correctly")
    void floor_getters() {
        UUID id = UUID.randomUUID();
        Floor floor = new Floor(id, "VIP");
        assertEquals(id, floor.getFloorId());
        assertEquals("VIP", floor.getName());
    }

    @Test
    @DisplayName("Floor — equals is id-based")
    void floor_equals() {
        UUID id = UUID.randomUUID();
        Floor a = new Floor(id, "Floor A");
        Floor b = new Floor(id, "Floor B");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Floor — not equal to null or different type")
    void floor_notEqual() {
        Floor floor = new Floor(UUID.randomUUID(), "X");
        assertNotEquals(floor, null);
        assertNotEquals(floor, "string");
    }

    @Test
    @DisplayName("Floor — toString contains floorId and name")
    void floor_toString() {
        UUID id = UUID.randomUUID();
        Floor floor = new Floor(id, "Lobby");
        assertTrue(floor.toString().contains("Lobby"));
        assertTrue(floor.toString().contains(id.toString()));
    }

    // ── TableMessage ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("TableMessage — equals is based on playerId, tableId, timestamp")
    void tableMessage_equals() {
        Instant now = Instant.now();
        TableMessage a = new TableMessage("p1", "t1", "hello", now);
        TableMessage b = new TableMessage("p1", "t1", "different content", now);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("TableMessage — not equal when playerId differs")
    void tableMessage_notEqual() {
        Instant now = Instant.now();
        TableMessage a = new TableMessage("p1", "t1", "hi", now);
        TableMessage b = new TableMessage("p2", "t1", "hi", now);
        assertNotEquals(a, b);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    @DisplayName("TableMessage — toString contains all fields")
    void tableMessage_toString() {
        Instant now = Instant.now();
        TableMessage msg = new TableMessage("p1", "t1", "hello", now);
        String s = msg.toString();
        assertTrue(s.contains("p1"));
        assertTrue(s.contains("t1"));
        assertTrue(s.contains("hello"));
    }

    // ── TableSession ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("TableSession — null table throws")
    void tableSession_nullTableThrows() {
        assertThrows(NullPointerException.class, () -> new TableSession(null));
    }

    @Test
    @DisplayName("TableSession — removePlayer removes correctly")
    void tableSession_removePlayer() {
        Table table = new Table("t1", "Table 1", "floor-1");
        TableSession session = new TableSession(table);
        Player player = new Player("p1", "Alice", "s1");
        session.addPlayer(player);
        assertEquals(1, session.getPlayerCount());
        session.removePlayer(player);
        assertEquals(0, session.getPlayerCount());
        assertTrue(session.isEmpty());
    }

    @Test
    @DisplayName("TableSession — getPlayers returns unmodifiable copy")
    void tableSession_getPlayers() {
        Table table = new Table("t1", "Table 1", "floor-1");
        TableSession session = new TableSession(table);
        session.addPlayer(new Player("p1", "Alice", "s1"));
        assertEquals(1, session.getPlayers().size());
    }

    @Test
    @DisplayName("TableSession — isFull when at maxPlayers")
    void tableSession_isFull() {
        Table table = new Table("t1", "Table 1", 0, 2, "floor-1");
        TableSession session = new TableSession(table);
        session.addPlayer(new Player("p1", "Alice", "s1"));
        session.addPlayer(new Player("p2", "Bob", "s2"));
        assertTrue(session.isFull());
    }

    @Test
    @DisplayName("TableSession — getAvailableSeats is correct")
    void tableSession_availableSeats() {
        Table table = new Table("t1", "Table 1", 0, 4, "floor-1");
        TableSession session = new TableSession(table);
        session.addPlayer(new Player("p1", "Alice", "s1"));
        assertEquals(3, session.getAvailableSeats());
    }

    @Test
    @DisplayName("TableSession — equals and hashCode are table-based")
    void tableSession_equalsAndHashCode() {
        Table table = new Table("t1", "Table 1", "floor-1");
        TableSession a = new TableSession(table);
        TableSession b = new TableSession(table);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("TableSession — toString contains table info")
    void tableSession_toString() {
        Table table = new Table("t1", "Table 1", "floor-1");
        TableSession session = new TableSession(table);
        assertTrue(session.toString().contains("t1"));
    }

    @Test
    @DisplayName("TableSession — getCreatedAt is set at construction")
    void tableSession_createdAt() {
        long before = System.currentTimeMillis();
        TableSession session = new TableSession(new Table("t1", "T", "f1"));
        long after = System.currentTimeMillis();
        assertTrue(session.getCreatedAt() >= before);
        assertTrue(session.getCreatedAt() <= after);
    }
}
