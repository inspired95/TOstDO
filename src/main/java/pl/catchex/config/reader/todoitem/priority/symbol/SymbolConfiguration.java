package pl.catchex.config.reader.todoitem.priority.symbol;

public class SymbolConfiguration {
    private String low;
    private String medium;
    private String high;

    // public comment required by snakeyaml
    public SymbolConfiguration() {}

    public void setLow(String low) { this.low = low; }
    public void setMedium(String medium) { this.medium = medium; }
    public void setHigh(String high) { this.high = high; }

    public String getLow() { return low; }
    public String getMedium() { return medium; }
    public String getHigh() { return high; }
}
