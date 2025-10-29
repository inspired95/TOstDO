package pl.catchex.config.reader;

import pl.catchex.config.reader.todoitem.TodoItemConfiguration;

public class ReaderConfiguration {
    private TodoItemConfiguration todoItemConfiguration;
    private String toDoFilePath;

    public ReaderConfiguration() {}

    public void setTodo_item(TodoItemConfiguration todoItemConfiguration) {
        this.todoItemConfiguration = todoItemConfiguration;
    }

    public TodoItemConfiguration getTodoItem(){
        return todoItemConfiguration;
    }

    public void setTodo_file_path(String toDoFilePath){
        this.toDoFilePath = toDoFilePath;
    }

    public String getToDoFilePath(){
        return toDoFilePath;
    }
}
