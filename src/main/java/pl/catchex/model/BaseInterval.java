package pl.catchex.model;

public enum BaseInterval {
    HIGH_PRIORITY_BASE_INTERVAL(120),
    MEDIUM_PRIORITY_BASE_INTERVAL(240),
    LOW_PRIORITY_BASE_INTERVAL(360);

    private final ToDoIntervalMinutes interval;

    BaseInterval(int minutes){
        interval = new ToDoIntervalMinutes(minutes);
    }

    public ToDoIntervalMinutes getInterval() {
        return interval;
    }
}
