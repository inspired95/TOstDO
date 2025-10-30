package pl.catchex.reader.todopattern;

import java.util.regex.Pattern;

public class TextPriorityDueDatePattern {
    @SuppressWarnings("java:S5852") // Known false positive – controlled input, no chance for ReDoS
    public static final Pattern TODO_PATTERN_PRIORITY_DUE_DATE = Pattern.compile(
            "- \\[ ] (.*?) \\[(.*?)] \\[(.*?)]"
    );

    private TextPriorityDueDatePattern(){}

    public static final int TEXT_INDEX = 1;
    public static final int PRIORITY_INDEX = 2;
    public static final int DUE_DATE_INDEX = 3;
}
