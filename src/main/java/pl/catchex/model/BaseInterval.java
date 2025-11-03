package pl.catchex.model;

public enum BaseInterval {
    HIGH_PRIORITY_BASE_INTERVAL(10),
    MEDIUM_PRIORITY_BASE_INTERVAL(15),
    LOW_PRIORITY_BASE_INTERVAL(20);

    private final ToDoIntervalMinutes interval;

    BaseInterval(int minutes){
        interval = new ToDoIntervalMinutes(minutes);
    }

    public ToDoIntervalMinutes getInterval() {
        return interval;
    }
}
