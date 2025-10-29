package pl.catchex.todopattern;

import java.util.regex.Pattern;

public class TextPriorityPattern {
    public static final Pattern TODO_PATTERN_PRIORITY = Pattern.compile(
            "- \\[ \\] (.*?) \\[(.*?)\\]"
    );

    private TextPriorityPattern(){}

    public static final int TEXT_INDEX = 1;
    public static final int PRIORITY_INDEX = 2;

    public static boolean matches(String line){
        return TODO_PATTERN_PRIORITY.matcher(line).matches();
    }
}
