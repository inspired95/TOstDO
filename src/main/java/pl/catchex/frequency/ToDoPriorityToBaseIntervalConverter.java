package pl.catchex.frequency;

import pl.catchex.config.reader.reminder.BaseIntervalConfiguration;
import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.model.ToDoIntervalMinutes;
import pl.catchex.model.ToDoItem;

public class ToDoPriorityToBaseIntervalConverter {
    private ToDoPriorityToBaseIntervalConverter() {
    }

    public static ToDoIntervalMinutes convert(ToDoItem.Priority priority, ReminderConfiguration reminderConfiguration){
        final int DEFAULT_HIGH = 10;
        final int DEFAULT_MEDIUM = 15;
        final int DEFAULT_LOW = 20;

        int high = DEFAULT_HIGH;
        int medium = DEFAULT_MEDIUM;
        int low = DEFAULT_LOW;

        if (reminderConfiguration != null) {
            BaseIntervalConfiguration cfg = reminderConfiguration.getBaseIntervalMinutes();
            if (cfg != null) {
                if (cfg.getHigh() > 0) high = cfg.getHigh();
                if (cfg.getMedium() > 0) medium = cfg.getMedium();
                if (cfg.getLow() > 0) low = cfg.getLow();
            }
        }

        return switch (priority){
            case LOW -> new ToDoIntervalMinutes(low);
            case MEDIUM -> new ToDoIntervalMinutes(medium);
            case HIGH -> new ToDoIntervalMinutes(high);
        };
    }
}
