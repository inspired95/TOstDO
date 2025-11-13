package pl.catchex.reader.taskpattern;

import java.util.regex.Pattern;

public class TextPattern {

    public static final Pattern TASK_PATTERN = Pattern.compile(
            "- \\[ ] (.*?)"
    );

    private TextPattern(){}

    public static final int TEXT_INDEX = 1;
}
