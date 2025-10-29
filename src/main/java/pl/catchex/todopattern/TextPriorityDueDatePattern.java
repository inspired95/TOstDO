package pl.catchex.todopattern;

import java.util.regex.Pattern;

public class TextPriorityDueDatePattern {
    public static final Pattern TODO_PATTERN_PRIORITY_DUE_DATE = Pattern.compile(
            "- \\[ \\] (.*?) \\[(.*?)\\] \\[(.*?)\\]"
    );

    private TextPriorityDueDatePattern(){}

    public static final int TEXT_INDEX = 1;
    public static final int PRIORITY_INDEX = 2;
    public static final int DUE_DATE_INDEX = 3;

    public static boolean matches(String line){
        return TODO_PATTERN_PRIORITY_DUE_DATE.matcher(line).matches();
    }
}
