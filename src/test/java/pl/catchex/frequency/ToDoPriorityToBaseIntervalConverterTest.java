package pl.catchex.frequency;

import org.junit.jupiter.api.Test;
import pl.catchex.config.reader.reminder.BaseIntervalConfiguration;
import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.ToDoIntervalMinutes;
import pl.catchex.model.ToDoItem;

import static org.junit.jupiter.api.Assertions.*;

class ToDoPriorityToBaseIntervalConverterTest {

    @Test
    void returnsDefaultValuesWhenConfigurationIsNull() {
        ToDoIntervalMinutes low = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.LOW, null);
        ToDoIntervalMinutes med = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.MEDIUM, null);
        ToDoIntervalMinutes high = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.HIGH, null);

        assertEquals(20, low.value());
        assertEquals(15, med.value());
        assertEquals(10, high.value());
    }

    @Test
    void usesConfigurationValuesWhenProvided() {
        BaseIntervalConfiguration cfg = new BaseIntervalConfiguration();
        cfg.setLow(30);
        cfg.setMedium(25);
        cfg.setHigh(5);

        ReminderConfiguration rc = new ReminderConfiguration();
        rc.setBaseIntervalMinutes(cfg);

        ToDoIntervalMinutes low = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.LOW, rc);
        ToDoIntervalMinutes med = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.MEDIUM, rc);
        ToDoIntervalMinutes high = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.HIGH, rc);

        assertEquals(30, low.value());
        assertEquals(25, med.value());
        assertEquals(5, high.value());
    }

    @Test
    void partialConfigurationUsesDefaultsForMissingOrInvalid() {
        BaseIntervalConfiguration cfg = new BaseIntervalConfiguration();
        cfg.setLow(0); // invalid -> fallback to default 20
        cfg.setMedium(18);
        // high left as 0 (invalid)

        ReminderConfiguration rc = new ReminderConfiguration();
        rc.setBaseIntervalMinutes(cfg);

        ToDoIntervalMinutes low = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.LOW, rc);
        ToDoIntervalMinutes med = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.MEDIUM, rc);
        ToDoIntervalMinutes high = ToDoPriorityToBaseIntervalConverter.convert(ToDoItem.Priority.HIGH, rc);

        assertEquals(20, low.value());
        assertEquals(18, med.value());
        assertEquals(10, high.value());
    }
}

