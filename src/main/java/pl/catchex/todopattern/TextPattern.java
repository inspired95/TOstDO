package pl.catchex.todopattern;

import java.util.regex.Pattern;

public class TextPattern {
    public static final Pattern TODO_PATTERN = Pattern.compile(
            "- \\[ \\] (.*?)"
    );

    private TextPattern(){}

    public static final int TEXT_INDEX = 1;

    public static boolean matches(String line){
        return TODO_PATTERN.matcher(line).matches();
    }
}
