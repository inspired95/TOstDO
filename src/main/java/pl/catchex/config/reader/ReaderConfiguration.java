package pl.catchex.config.reader;

import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.config.reader.taskConfiguration.TaskConfiguration;

public class ReaderConfiguration {
    private TaskConfiguration taskConfiguration;
    private ReminderConfiguration reminderConfiguration;
    private String tasksFilePath;

    public ReaderConfiguration() {
        // public comment required by snakeyaml
    }

    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = taskConfiguration;
    }

    public TaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }

    public void setReminder(ReminderConfiguration reminderConfiguration){
        this.reminderConfiguration = reminderConfiguration;
    }

    public void setTasksFilePath(String tasksFilePath){
        this.tasksFilePath = tasksFilePath;
    }

    public String getTasksFilePath(){
        return tasksFilePath;
    }

    public ReminderConfiguration getReminderConfiguration(){
        return reminderConfiguration;
    }
}
