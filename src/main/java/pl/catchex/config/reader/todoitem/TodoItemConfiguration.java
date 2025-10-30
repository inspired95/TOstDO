package pl.catchex.config.reader.todoitem;

public class TodoItemConfiguration {
    private String dateFormat;
    private PriorityConfiguration priorityConfiguration;

    public TodoItemConfiguration() {
        // public comment required by snakeyaml
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setPriority(PriorityConfiguration priorityConfiguration) {
        this.priorityConfiguration = priorityConfiguration;
    }

    public String getDateFormat() { return dateFormat; }
    public PriorityConfiguration getPriority() { return priorityConfiguration; }
}
