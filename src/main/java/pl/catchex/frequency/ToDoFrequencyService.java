package pl.catchex.frequency;

import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.ToDoIntervalMinutes;
import pl.catchex.model.ToDoItem;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

public class ToDoFrequencyService {

    private final Clock clock;

    private final double periodCriticalFactor;
    private final double periodUrgentFactor;

    private final int periodCriticalThreshold;
    private final int periodUrgentThreshold;

    private final ReminderConfiguration reminderConfig;

    /**
     * Create a ToDoFrequencyService using the provided clock and reminder configuration.
     *
     * @param clock         clock used to determine "now" (useful for tests)
     * @param configuration reminder configuration containing factors and thresholds
     */
    public ToDoFrequencyService(Clock clock, ReminderConfiguration configuration){
        this.clock = clock;
        this.reminderConfig = configuration;
        periodCriticalFactor = configuration.getPeriodFactor().getCritical();
        periodUrgentFactor = configuration.getPeriodFactor().getUrgent();
        periodCriticalThreshold = configuration.getPeriodThreshold().getCritical();
        periodUrgentThreshold = configuration.getPeriodThreshold().getUrgent();
    }

    /**
     * Calculate the reminder interval for a given {@link ToDoItem}.
     * The calculation is based on the item's priority (base interval) and
     * may be expedited if the item's due date is approaching according to
     * the configured thresholds and factors.
     *
     * @param toDoItem the {@link ToDoItem} to calculate interval for
     * @return calculated ToDoIntervalMinutes representing minutes between reminders
     */
    public ToDoIntervalMinutes calculateToDoInterval(ToDoItem toDoItem){
        ToDoIntervalMinutes baseInterval = ToDoPriorityToBaseIntervalConverter.convert(toDoItem.priority(), reminderConfig);
        return expediteIfTimeLow(toDoItem, baseInterval);
    }

    private ToDoIntervalMinutes expediteIfTimeLow(ToDoItem todo, ToDoIntervalMinutes baseInterval) {
        if(todo.dueDate() == null){
            return baseInterval;
        }
        LocalDate today = LocalDate.now(clock);

        Period period = Period.between(today, todo.dueDate());

        if(period.getDays() < periodCriticalThreshold){
            return expedite(baseInterval, periodCriticalFactor);
        }
        if(period.getDays() < periodUrgentThreshold){
            return expedite(baseInterval, periodUrgentFactor);
        }
        return baseInterval;
    }

    private ToDoIntervalMinutes expedite(ToDoIntervalMinutes baseInterval, double factor){
        int intervalMinutes = baseInterval.value();
        int roundedInterval = round(intervalMinutes * factor);
        return new ToDoIntervalMinutes(roundedInterval);
    }

    private int round(double valueToRound){
        return (int) Math.round(valueToRound);
    }
}