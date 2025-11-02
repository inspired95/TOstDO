package pl.catchex.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DateParserTest {
    @Test
    void parseValidDate() {
        DateParser parser = new DateParser("yyyy-MM-dd");
        Optional<LocalDate> d = parser.parse("2025-11-02");
        assertTrue(d.isPresent());
        assertEquals(LocalDate.of(2025,11,2), d.get());
    }

    @Test
    void parseInvalidDateReturnsEmpty() {
        DateParser parser = new DateParser("yyyy-MM-dd");
        Optional<LocalDate> d = parser.parse("not-a-date");
        assertTrue(d.isEmpty());
    }
}

