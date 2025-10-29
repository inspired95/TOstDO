package pl.catchex.config.reader;

import pl.catchex.config.reader.todoitem.TodoItemConfiguration;

public class ReaderConfiguration {
    private TodoItemConfiguration todoItemConfiguration;
    private String toDoFilePath;

    // public comment required by snakeyaml
    public ReaderConfiguration() {}

    public void setTodoItem(TodoItemConfiguration todoItemConfiguration) {
        this.todoItemConfiguration = todoItemConfiguration;
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
}
