package pl.catchex.config.reader.todoitem;

import pl.catchex.config.reader.todoitem.priority.PriorityConfiguration;

public class TodoItemConfiguration {
    private String dateFormat;
    private PriorityConfiguration priorityConfiguration;

    public TodoItemConfiguration() {}

    public void setDate_format(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setPriority(PriorityConfiguration priorityConfiguration) {
        this.priorityConfiguration = priorityConfiguration;
    }

    public String getDateFormat() { return dateFormat; }
    public PriorityConfiguration getPriority() { return priorityConfiguration; }
}
