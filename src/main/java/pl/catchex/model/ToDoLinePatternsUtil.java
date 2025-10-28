package pl.catchex.model;

import pl.catchex.todopattern.TextPriorityDueDatePattern;
import pl.catchex.todopattern.TextPriorityPattern;

import java.util.regex.Pattern;

public class ToDoLinePatternsUtil {
    public static final Pattern TODO_PATTERN = Pattern.compile(
            "- \\[ \\] (.*?)"
    );

    private ToDoLinePatternsUtil(){}

    public static boolean isTodoPatternPriorityDueDate(String line){
        return TextPriorityDueDatePattern.matches(line);
    }

    public static boolean isTodoPatternPriority(String line){
        return TextPriorityPattern.matches(line);
    }

    public static boolean isTodoPattern(String line){
        return matchesPattern(line, TODO_PATTERN);
    }

    private static boolean matchesPattern(String line, Pattern todoPatternPriorityDueDate) {
        return todoPatternPriorityDueDate.matcher(line).matches();
    }
}
