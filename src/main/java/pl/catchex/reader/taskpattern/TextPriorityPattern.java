package pl.catchex.reader.taskpattern;

import java.util.regex.Pattern;

public class TextPriorityPattern {
    @SuppressWarnings("java:S5852") // Known false positive – controlled input, no chance for ReDoS
    public static final Pattern TASK_PATTERN_PRIORITY = Pattern.compile(
            "- \\[ ] (.*?) \\[(.*?)]"
    );

    private TextPriorityPattern(){}

    public static final int TEXT_INDEX = 1;
    public static final int PRIORITY_INDEX = 2;
}
