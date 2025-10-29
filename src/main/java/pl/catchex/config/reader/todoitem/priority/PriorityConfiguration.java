package pl.catchex.config.reader.todoitem.priority;

import pl.catchex.config.reader.todoitem.priority.symbol.SymbolConfiguration;

public class PriorityConfiguration {
    private SymbolConfiguration symbolConfiguration;

    // public comment required by snakeyaml
    public PriorityConfiguration() {}

    public void setSymbol(SymbolConfiguration symbolConfiguration) {
        this.symbolConfiguration = symbolConfiguration;
    }

    public SymbolConfiguration getSymbol() { return symbolConfiguration; }
}
