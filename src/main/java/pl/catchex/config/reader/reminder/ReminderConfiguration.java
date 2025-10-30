package pl.catchex.config.reader.reminder;

public class ReminderConfiguration {
    private PeriodFactorConfiguration periodFactor;
    private PeriodThresholdConfiguration periodThreshold;

    public ReminderConfiguration() {
        // public comment required by snakeyaml
    }

    public PeriodFactorConfiguration getPeriodFactor() {
        return periodFactor;
    }

    public void setPeriodFactor(PeriodFactorConfiguration periodFactor) {
        this.periodFactor = periodFactor;
    }

    public PeriodThresholdConfiguration getPeriodThreshold() {
        return periodThreshold;
    }

    public void setPeriodThreshold(PeriodThresholdConfiguration periodThreshold) {
        this.periodThreshold = periodThreshold;
    }
}
