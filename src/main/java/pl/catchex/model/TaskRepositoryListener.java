package pl.catchex.model;

public interface TaskRepositoryListener {
    void onTaskAdded(Task item);
    void onTaskRemoved(Task item);
}

