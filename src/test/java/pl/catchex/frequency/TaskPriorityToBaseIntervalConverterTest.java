package pl.catchex.frequency;

import org.junit.jupiter.api.Test;
import pl.catchex.config.reader.reminder.BaseIntervalConfiguration;
import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.IntervalMinutes;
import pl.catchex.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskPriorityToBaseIntervalConverterTest {

    @Test
    void returnsDefaultValuesWhenConfigurationIsNull() {
        IntervalMinutes low = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.LOW, null);
        IntervalMinutes med = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.MEDIUM, null);
        IntervalMinutes high = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.HIGH, null);

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

        IntervalMinutes low = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.LOW, rc);
        IntervalMinutes med = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.MEDIUM, rc);
        IntervalMinutes high = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.HIGH, rc);

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

        IntervalMinutes low = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.LOW, rc);
        IntervalMinutes med = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.MEDIUM, rc);
        IntervalMinutes high = TaskPriorityToBaseIntervalConverter.convert(Task.Priority.HIGH, rc);

        assertEquals(20, low.value());
        assertEquals(18, med.value());
        assertEquals(10, high.value());
    }
}

