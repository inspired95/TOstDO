package pl.catchex.model;

public interface ToDoRepositoryListener {
    void onToDoAdded(ToDoItem item);
    void onToDoRemoved(ToDoItem item);
}

