package pl.catchex.config.reader.todoitem;

import pl.catchex.config.reader.todoitem.priority.PriorityConfiguration;

public class TodoItemConfiguration {
    private String dateFormat;
    private PriorityConfiguration priorityConfiguration;

    // public comment required by snakeyaml
    public TodoItemConfiguration() {}

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setPriority(PriorityConfiguration priorityConfiguration) {
        this.priorityConfiguration = priorityConfiguration;
    }

    public String getDateFormat() { return dateFormat; }
    public PriorityConfiguration getPriority() { return priorityConfiguration; }
}
