package pl.catchex.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DateParserTest {
    @Test
    void parseValidDate() {
        // given
        DateParser parser = new DateParser("yyyy-MM-dd");

        // when
        Optional<LocalDate> d = parser.parse("2025-11-02");

        // then
        assertTrue(d.isPresent());
        assertEquals(LocalDate.of(2025,11,2), d.get());
    }

    @Test
    void parseInvalidDateReturnsEmpty() {
        // given
        DateParser parser = new DateParser("yyyy-MM-dd");

        // when
        Optional<LocalDate> d = parser.parse("not-a-date");

        // then
        assertTrue(d.isEmpty());
    }
}
