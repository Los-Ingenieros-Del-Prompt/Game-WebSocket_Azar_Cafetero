package com.aguardientes.azarcafetero.game.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExtraDomainCoverageTest {

    // ── Player ────────────────────────────────────────────────────────────────

    @Test
    void player_negativeBalanceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("p1", "Alice", "s1", -1.0));
    }

    @Test
    void player_zeroBalanceAllowed() {
        Player p = new Player("p1", "Alice", "s1", 0.0);
        assertEquals(0.0, p.getBalance());
    }

    @Test
    void player_positiveBalance() {
        Player p = new Player("p1", "Alice", "s1", 250.5);
        assertEquals(250.5, p.getBalance());
    }

    @Test
    void player_notEqualToNullOrDifferentType() {
        Player p = new Player("p1", "Alice", "s1");
        assertNotEquals(p, null);
        assertNotEquals(p, "string");
    }

    @Test
    void player_notEqualWhenIdsDiffer() {
        Player a = new Player("p1", "Alice", "s1");
        Player b = new Player("p2", "Alice", "s1");
        assertNotEquals(a, b);
    }

    @Test
    void player_toStringContainsFields() {
        Player p = new Player("p1", "Alice", "s1", 100.0);
        String s = p.toString();
        assertTrue(s.contains("p1"));
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("s1"));
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    @Test
    void table_negativeBetThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Table("t1", "T", -1, 4, "f1"));
    }

    @Test
    void table_zeroOrNegativeMaxPlayersThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Table("t1", "T", 10, 0, "f1"));
        assertThrows(IllegalArgumentException.class,
                () -> new Table("t1", "T", 10, -1, "f1"));
    }

    @Test
    void table_twoArgConstructorDefaults() {
        Table t = new Table("t1", "T", "f1");
        assertEquals(0, t.getRequiredBet());
        assertEquals(6, t.getMaxPlayers());
    }

    @Test
    void table_threeArgWithBetConstructor() {
        Table t = new Table("t1", "T", 50.0, "f1");
        assertEquals(50.0, t.getRequiredBet());
        assertEquals(6, t.getMaxPlayers());
    }

    @Test
    void table_notEqualToNullOrDifferentType() {
        Table t = new Table("t1", "T", "f1");
        assertNotEquals(t, null);
        assertNotEquals(t, "string");
    }

    @Test
    void table_toStringContainsFields() {
        Table t = new Table("t1", "Tablename", 25, 4, "f1");
        String s = t.toString();
        assertTrue(s.contains("t1"));
        assertTrue(s.contains("Tablename"));
        assertTrue(s.contains("25"));
    }

    // ── FloorEvent ────────────────────────────────────────────────────────────

    @Test
    void floorEvent_nullArgsThrow() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        assertThrows(NullPointerException.class,
                () -> new FloorEvent(null, "t1", FloorEvent.Type.TABLE_CREATED, "m", now, 5, 1));
        assertThrows(NullPointerException.class,
                () -> new FloorEvent(id, null, FloorEvent.Type.TABLE_CREATED, "m", now, 5, 1));
        assertThrows(NullPointerException.class,
                () -> new FloorEvent(id, "t1", null, "m", now, 5, 1));
        assertThrows(NullPointerException.class,
                () -> new FloorEvent(id, "t1", FloorEvent.Type.TABLE_CREATED, null, now, 5, 1));
        assertThrows(NullPointerException.class,
                () -> new FloorEvent(id, "t1", FloorEvent.Type.TABLE_CREATED, "m", null, 5, 1));
    }

    @Test
    void floorEvent_allGetters() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        FloorEvent e = new FloorEvent(id, "t1", FloorEvent.Type.PLAYER_JOINED, "msg", now, 5, 1);
        assertEquals(id, e.getFloorId());
        assertEquals("t1", e.getTableId());
        assertEquals(FloorEvent.Type.PLAYER_JOINED, e.getType());
        assertEquals("msg", e.getMessage());
        assertEquals(now, e.getTimestamp());
        assertEquals(5, e.getAvailableSeats());
        assertEquals(1, e.getTotalPlayers());
    }

    @Test
    void floorEvent_notEqualToNullOrDifferentType() {
        FloorEvent e = new FloorEvent(UUID.randomUUID(), "t1",
                FloorEvent.Type.TABLE_CREATED, "m", Instant.now(), 5, 1);
        assertNotEquals(e, null);
        assertNotEquals(e, "string");
    }

    @Test
    void floorEvent_notEqualWhenTableIdDiffers() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        FloorEvent a = new FloorEvent(id, "t1", FloorEvent.Type.TABLE_CREATED, "m", now, 5, 1);
        FloorEvent b = new FloorEvent(id, "t2", FloorEvent.Type.TABLE_CREATED, "m", now, 5, 1);
        assertNotEquals(a, b);
    }

    @Test
    void floorEvent_toStringContainsFields() {
        UUID id = UUID.randomUUID();
        FloorEvent e = new FloorEvent(id, "t1", FloorEvent.Type.TABLE_CLOSED, "m", Instant.now(), null, null);
        String s = e.toString();
        assertTrue(s.contains("t1"));
        assertTrue(s.contains("TABLE_CLOSED"));
    }
}