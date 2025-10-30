package pl.catchex.config.reader.todoitem;

public class PriorityConfiguration {
    private SymbolConfiguration symbolConfiguration;

    public PriorityConfiguration() {
        // public comment required by snakeyaml
    }

    public void setSymbol(SymbolConfiguration symbolConfiguration) {
        this.symbolConfiguration = symbolConfiguration;
    }

    public SymbolConfiguration getSymbol() { return symbolConfiguration; }
}
