package pl.catchex.config.reader.taskConfiguration;

public class SymbolConfiguration {
    private String low;
    private String medium;
    private String high;

    public SymbolConfiguration() {
        // public comment required by snakeyaml
    }

    public void setLow(String low) { this.low = low; }
    public void setMedium(String medium) { this.medium = medium; }
    public void setHigh(String high) { this.high = high; }

    public String getLow() { return low; }
    public String getMedium() { return medium; }
    public String getHigh() { return high; }
}
