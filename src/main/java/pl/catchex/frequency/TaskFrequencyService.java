package pl.catchex.frequency;

import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.IntervalMinutes;
import pl.catchex.model.Task;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

public class TaskFrequencyService {

    private final Clock clock;

    private final double periodCriticalFactor;
    private final double periodUrgentFactor;

    private final int periodCriticalThreshold;
    private final int periodUrgentThreshold;

    private final ReminderConfiguration reminderConfig;

    /**
     * Create a TaskFrequencyService using the provided clock and reminder configuration.
     *
     * @param clock         clock used to determine "now" (useful for tests)
     * @param configuration reminder configuration containing factors and thresholds
     */
    public TaskFrequencyService(Clock clock, ReminderConfiguration configuration){
        this.clock = clock;
        this.reminderConfig = configuration;
        periodCriticalFactor = configuration.getPeriodFactor().getCritical();
        periodUrgentFactor = configuration.getPeriodFactor().getUrgent();
        periodCriticalThreshold = configuration.getPeriodThreshold().getCritical();
        periodUrgentThreshold = configuration.getPeriodThreshold().getUrgent();
    }

    /**
     * Calculate the reminder interval for a given {@link Task}.
     * The calculation is based on the item's priority (base interval) and
     * may be expedited if the item's due date is approaching according to
     * the configured thresholds and factors.
     *
     * @param task the {@link Task} to calculate interval for
     * @return calculated  {@link IntervalMinutes} representing minutes between reminders
     */
    public IntervalMinutes calculateTaskInterval(Task task){
        IntervalMinutes baseInterval = TaskPriorityToBaseIntervalConverter.convert(task.priority(), reminderConfig);
        return expediteIfTimeLow(task, baseInterval);
    }

    private IntervalMinutes expediteIfTimeLow(Task task, IntervalMinutes baseInterval) {
        if(task.dueDate() == null){
            return baseInterval;
        }
        LocalDate today = LocalDate.now(clock);

        Period period = Period.between(today, task.dueDate());

        if(period.getDays() < periodCriticalThreshold){
            return expedite(baseInterval, periodCriticalFactor);
        }
        if(period.getDays() < periodUrgentThreshold){
            return expedite(baseInterval, periodUrgentFactor);
        }
        return baseInterval;
    }

    private IntervalMinutes expedite(IntervalMinutes baseInterval, double factor){
        int intervalMinutes = baseInterval.value();
        int roundedInterval = round(intervalMinutes * factor);
        return new IntervalMinutes(roundedInterval);
    }

    private int round(double valueToRound){
        return (int) Math.round(valueToRound);
    }
}