package pl.catchex.config.reader.taskConfiguration;

public class TaskConfiguration {
    private String dateFormat;
    private PriorityConfiguration priorityConfiguration;

    public TaskConfiguration() {
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
