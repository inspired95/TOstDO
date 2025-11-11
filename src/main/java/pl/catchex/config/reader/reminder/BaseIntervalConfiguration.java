package pl.catchex.config.reader.reminder;

public class BaseIntervalConfiguration {
    private int low;
    private int medium;
    private int high;

    public BaseIntervalConfiguration() {
        // public constructor for SnakeYAML
    }

    public int getLow() { return low; }
    public void setLow(int low) { this.low = low; }

    public int getMedium() { return medium; }
    public void setMedium(int medium) { this.medium = medium; }

    public int getHigh() { return high; }
    public void setHigh(int high) { this.high = high; }
}

