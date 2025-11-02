package pl.catchex.reader;

import org.junit.jupiter.api.Test;
import pl.catchex.config.reader.todoitem.SymbolConfiguration;
import pl.catchex.model.ToDoItem.Priority;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PriorityParserTest {
    @Test
    void parseKnownSymbols() {
        SymbolConfiguration cfg = new SymbolConfiguration();
        cfg.setLow("-");
        cfg.setMedium("*");
        cfg.setHigh("!");

        PriorityParser parser = new PriorityParser(cfg);
        assertEquals(Optional.of(Priority.LOW), parser.parse("-"));
        assertEquals(Optional.of(Priority.MEDIUM), parser.parse("*"));
        assertEquals(Optional.of(Priority.HIGH), parser.parse("!"));
    }

    @Test
    void parseUnknownReturnsEmpty() {
        SymbolConfiguration cfg = new SymbolConfiguration();
        cfg.setLow("-");
        cfg.setMedium("*");
        cfg.setHigh("!");

        PriorityParser parser = new PriorityParser(cfg);
        assertTrue(parser.parse("unknown").isEmpty());
    }
}

