package pl.catchex.model;

import java.time.LocalDate;

public record ToDoItem(String task, Priority priority, LocalDate dueDate) {
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public ToDoItem {
        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
    }

    public static class Builder {
        private String task;
        private Priority priority;
        private LocalDate dueDate;

        public Builder task(String task) {
            this.task = task;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public ToDoItem build() {
            return new ToDoItem(task, priority, dueDate);
        }
    }

    @Override
    public String toString() {
        String dueText = (dueDate != null) ? " (due: " + dueDate + ")" : "";
        return "[" + priority + "] " + task + dueText;
    }
}
