package pl.catchex.config.reader.reminder;

public class PeriodThresholdConfiguration {
    private int critical;
    private int urgent;

    public PeriodThresholdConfiguration() {
        // public comment required by snakeyaml
    }

    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }
}