package pl.catchex.frequency;

import org.junit.jupiter.api.Test;
import pl.catchex.config.reader.reminder.PeriodFactorConfiguration;
import pl.catchex.config.reader.reminder.PeriodThresholdConfiguration;
import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.IntervalMinutes;
import pl.catchex.model.Task;
import pl.catchex.testutil.MutableClock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class TaskFrequencyServiceTest {

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
        TaskFrequencyService svc = new TaskFrequencyService(clock, cfg);

        Task item = new Task.Builder().task("task").priority(Task.Priority.LOW).dueDate(null).build();

        // when
        IntervalMinutes result = svc.calculateTaskInterval(item);

        // then
        IntervalMinutes expected = new IntervalMinutes(20); // default low priority base interval
        assertEquals(expected.value(), result.value());
    }

    @Test
    void dueDateWithinCritical_appliesCriticalFactor() {
        // given
        MutableClock clock = new MutableClock(Instant.parse("2025-11-02T00:00:00Z"), ZoneId.of("UTC"));
        ReminderConfiguration cfg = createConfig(0.5, 0.8, 3, 7);
        TaskFrequencyService svc = new TaskFrequencyService(clock, cfg);

        LocalDate due = LocalDate.of(2025, 11, 4); // 2 days from base date -> < criticalThreshold(3)
        Task item = new Task.Builder().task("t").priority(Task.Priority.MEDIUM).dueDate(due).build();

        // when
        IntervalMinutes result = svc.calculateTaskInterval(item);

        // then
        int expected = (int) Math.round(new IntervalMinutes(15).value() * 0.5); // default medium = 15

        assertEquals(expected, result.value());
    }

    @Test
    void dueDateBetweenCriticalAndUrgent_appliesUrgentFactor() {
        // given
        MutableClock clock = new MutableClock(Instant.parse("2025-11-02T00:00:00Z"), ZoneId.of("UTC"));
        ReminderConfiguration cfg = createConfig(0.4, 0.75, 1, 5);
        TaskFrequencyService svc = new TaskFrequencyService(clock, cfg);

        LocalDate due = LocalDate.of(2025, 11, 5); // 3 days from base date -> >=1 and <5 -> urgent
        Task item = new Task.Builder().task("t").priority(Task.Priority.HIGH).dueDate(due).build();

        // when
        IntervalMinutes result = svc.calculateTaskInterval(item);

        // then
        int expected = (int) Math.round(new IntervalMinutes(10).value() * 0.75); // default high = 10

        assertEquals(expected, result.value());
    }

    @Test
    void dueDateBeyondThreshold_returnsBaseInterval() {
        // given
        MutableClock clock = new MutableClock(Instant.parse("2025-11-02T00:00:00Z"), ZoneId.of("UTC"));
        ReminderConfiguration cfg = createConfig(0.5, 0.8, 1, 5);
        TaskFrequencyService svc = new TaskFrequencyService(clock, cfg);

        LocalDate due = LocalDate.of(2026, 1, 15); // far future
        Task item = new Task.Builder().task("t").priority(Task.Priority.HIGH).dueDate(due).build();

        // when
        IntervalMinutes result = svc.calculateTaskInterval(item);

        // then
        IntervalMinutes expected = new IntervalMinutes(10); // default high priority base interval

        assertEquals(expected.value(), result.value());
    }
}
