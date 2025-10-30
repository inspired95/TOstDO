package pl.catchex.model;

public record ToDoIntervalMinutes(int value) {

    @Override
    public String toString() {
        return "ToDoIntervalMinutes{" +
                "value=" + value +
                '}';
    }
}
