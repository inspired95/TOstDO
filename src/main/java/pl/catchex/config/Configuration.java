package pl.catchex.config;

public class Configuration {

    public static final String LOW_PRIORITY_TEXT_SYMBOL="+";
    public static final String MEDIUM_PRIORITY_TEXT_SYMBOL="++";
    public static final String HIGH_PRIORITY_TEXT_SYMBOL="+++";

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public String getHighPriorityTextSymbol(){
        return HIGH_PRIORITY_TEXT_SYMBOL;
    }

    public String getMediumPriorityTextSymbol(){
        return MEDIUM_PRIORITY_TEXT_SYMBOL;
    }

    public String getLowPriorityTextSymbol(){
        return LOW_PRIORITY_TEXT_SYMBOL;
    }

    public String getDateFormat(){
        return DATE_FORMAT;
    }
}
