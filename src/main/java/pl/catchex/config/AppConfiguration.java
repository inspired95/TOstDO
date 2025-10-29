package pl.catchex.config;

import pl.catchex.config.reader.ReaderConfiguration;

public class AppConfiguration {
    private ReaderConfiguration readerConfiguration;

    public AppConfiguration() {}

    public void setConfiguration(ReaderConfiguration readerConfiguration) {
        this.readerConfiguration = readerConfiguration;
    }

    public ReaderConfiguration getConfiguration() { return readerConfiguration; }
}
