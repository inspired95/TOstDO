package pl.catchex.frequency;

import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.BaseInterval;
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

    public ToDoFrequencyService(Clock clock, ReminderConfiguration configuration){
        this.clock = clock;
        periodCriticalFactor = configuration.getPeriodFactor().getCritical();
        periodUrgentFactor = configuration.getPeriodFactor().getUrgent();
        periodCriticalThreshold = configuration.getPeriodThreshold().getCritical();
        periodUrgentThreshold = configuration.getPeriodThreshold().getUrgent();
    }

    public ToDoIntervalMinutes calculateToDoInterval(ToDoItem todo){
        BaseInterval baseInterval = ToDoPriorityToBaseIntervalConverter.convert(todo.priority());
        return expediteIfTimeLow(todo, baseInterval);
    }

    private ToDoIntervalMinutes expediteIfTimeLow(ToDoItem todo, BaseInterval baseInterval) {
        if(todo.dueDate() == null){
            return baseInterval.getInterval();
        }
        LocalDate today = LocalDate.now(clock);

        Period period = Period.between(today, todo.dueDate());

        if(period.getDays() < periodCriticalThreshold){
            return expedite(baseInterval, periodCriticalFactor);
        }
        if(period.getDays() < periodUrgentThreshold){
            return expedite(baseInterval, periodUrgentFactor);
        }
        return baseInterval.getInterval();
    }

    private ToDoIntervalMinutes expedite(BaseInterval baseInterval, double factor){
        int intervalMinutes = baseInterval.getInterval().value();
        int roundedInterval = round(intervalMinutes * factor);
        return new ToDoIntervalMinutes(roundedInterval);
    }

    private int round(double valueToRound){
        return (int) Math.round(valueToRound);
    }
}