package pl.catchex.frequency;

import org.junit.jupiter.api.Test;
import pl.catchex.config.reader.reminder.PeriodFactorConfiguration;
import pl.catchex.config.reader.reminder.PeriodThresholdConfiguration;
import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.BaseInterval;
import pl.catchex.model.ToDoIntervalMinutes;
import pl.catchex.model.ToDoItem;
import pl.catchex.testutil.MutableClock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class ToDoFrequencyServiceTest {

    private ReminderConfiguration createConfig(double criticalFactor, double urgentFactor, int criticalThreshold, int urgentThreshold) {
        PeriodFactorConfiguration pf = new PeriodFactorConfiguration();
        pf.setCritical(criticalFactor);
        pf.setUrgent(urgentFactor);

        PeriodThresholdConfiguration pt = new PeriodThresholdConfiguration();
        pt.setCritical(criticalThreshold);
        pt.setUrgent(urgentThreshold);

        ReminderConfiguration rc = new ReminderConfiguration();
        rc.setPeriodFactor(pf);
        rc.setPeriodThreshold(pt);
        return rc;
    }

    @Test
    void dueDateNull_returnsBaseInterval() {
        // given
        MutableClock clock = new MutableClock(Instant.parse("2025-11-02T00:00:00Z"), ZoneId.of("UTC"));
        ReminderConfiguration cfg = createConfig(0.5, 0.75, 1, 3);
        ToDoFrequencyService svc = new ToDoFrequencyService(clock, cfg);

        ToDoItem item = new ToDoItem.Builder().task("task").priority(ToDoItem.Priority.LOW).dueDate(null).build();

        // when
        ToDoIntervalMinutes result = svc.calculateToDoInterval(item);

        // then
        ToDoIntervalMinutes expected = BaseInterval.LOW_PRIORITY_BASE_INTERVAL.getInterval();
        assertEquals(expected.value(), result.value());
    }

    @Test
    void dueDateWithinCritical_appliesCriticalFactor() {
        // given
        MutableClock clock = new MutableClock(Instant.parse("2025-11-02T00:00:00Z"), ZoneId.of("UTC"));
        ReminderConfiguration cfg = createConfig(0.5, 0.8, 3, 7);
        ToDoFrequencyService svc = new ToDoFrequencyService(clock, cfg);

        LocalDate due = LocalDate.of(2025, 11, 4); // 2 days from base date -> < criticalThreshold(3)
        ToDoItem item = new ToDoItem.Builder().task("t").priority(ToDoItem.Priority.MEDIUM).dueDate(due).build();

        // when
        ToDoIntervalMinutes result = svc.calculateToDoInterval(item);

        // then
        int expected = (int) Math.round(BaseInterval.MEDIUM_PRIORITY_BASE_INTERVAL.getInterval().value() * 0.5);

        assertEquals(expected, result.value());
    }

    @Test
    void dueDateBetweenCriticalAndUrgent_appliesUrgentFactor() {
        // given
        MutableClock clock = new MutableClock(Instant.parse("2025-11-02T00:00:00Z"), ZoneId.of("UTC"));
        ReminderConfiguration cfg = createConfig(0.4, 0.75, 1, 5);
        ToDoFrequencyService svc = new ToDoFrequencyService(clock, cfg);

        LocalDate due = LocalDate.of(2025, 11, 5); // 3 days from base date -> >=1 and <5 -> urgent
        ToDoItem item = new ToDoItem.Builder().task("t").priority(ToDoItem.Priority.HIGH).dueDate(due).build();

        // when
        ToDoIntervalMinutes result = svc.calculateToDoInterval(item);

        // then
        int expected = (int) Math.round(BaseInterval.HIGH_PRIORITY_BASE_INTERVAL.getInterval().value() * 0.75);

        assertEquals(expected, result.value());
    }

    @Test
    void dueDateBeyondThreshold_returnsBaseInterval() {
        // given
        MutableClock clock = new MutableClock(Instant.parse("2025-11-02T00:00:00Z"), ZoneId.of("UTC"));
        ReminderConfiguration cfg = createConfig(0.5, 0.8, 1, 5);
        ToDoFrequencyService svc = new ToDoFrequencyService(clock, cfg);

        LocalDate due = LocalDate.of(2026, 1, 15); // far future
        ToDoItem item = new ToDoItem.Builder().task("t").priority(ToDoItem.Priority.HIGH).dueDate(due).build();

        // when
        ToDoIntervalMinutes result = svc.calculateToDoInterval(item);

        // then
        ToDoIntervalMinutes expected = BaseInterval.HIGH_PRIORITY_BASE_INTERVAL.getInterval();

        assertEquals(expected.value(), result.value());
    }
}
