package pl.catchex.config.reader;

import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.config.reader.todoitem.TodoItemConfiguration;

public class ReaderConfiguration {
    private TodoItemConfiguration todoItemConfiguration;
    private ReminderConfiguration reminderConfiguration;
    private String toDoFilePath;

    public ReaderConfiguration() {
        // public comment required by snakeyaml
    }

    public void setTodoItem(TodoItemConfiguration todoItemConfiguration) {
        this.todoItemConfiguration = todoItemConfiguration;
    }

    public void setReminder(ReminderConfiguration reminderConfiguration){
        this.reminderConfiguration = reminderConfiguration;
    }

    public TodoItemConfiguration getTodoItem(){
        return todoItemConfiguration;
    }

    public void setTodoFilePath(String toDoFilePath){
        this.toDoFilePath = toDoFilePath;
    }

    public String getToDoFilePath(){
        return toDoFilePath;
    }

    public ReminderConfiguration getReminderConfiguration(){
        return reminderConfiguration;
    }
}
