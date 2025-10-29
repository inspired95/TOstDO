package pl.catchex.config;

import pl.catchex.config.reader.ReaderConfiguration;

public class AppConfiguration {
    private ReaderConfiguration readerConfiguration;

    // public comment required by snakeyaml
    public AppConfiguration() {}

    public void setConfiguration(ReaderConfiguration readerConfiguration) {
        this.readerConfiguration = readerConfiguration;
    }

    public ReaderConfiguration getConfiguration() { return readerConfiguration; }
}
