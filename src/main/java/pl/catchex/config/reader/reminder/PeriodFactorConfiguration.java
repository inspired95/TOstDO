package pl.catchex.config.reader.reminder;

public class PeriodFactorConfiguration {
    private double critical;
    private double urgent;

    public PeriodFactorConfiguration() {
        // public comment required by snakeyaml
    }

    public double getCritical() {
        return critical;
    }

    public void setCritical(double critical) {
        this.critical = critical;
    }

    public double getUrgent() {
        return urgent;
    }

    public void setUrgent(double urgent) {
        this.urgent = urgent;
    }
}