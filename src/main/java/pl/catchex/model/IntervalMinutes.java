package pl.catchex.model;

public record IntervalMinutes(int value) {

    @Override
    public String toString() {
        return "IntervalMinutes{" +
                "value=" + value +
                '}';
    }
}
