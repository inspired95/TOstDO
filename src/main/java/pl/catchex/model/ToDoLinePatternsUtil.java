package pl.catchex.model;

import pl.catchex.todopattern.TextPattern;
import pl.catchex.todopattern.TextPriorityDueDatePattern;
import pl.catchex.todopattern.TextPriorityPattern;

public class ToDoLinePatternsUtil {

    private ToDoLinePatternsUtil(){}

    public static boolean isTodoPatternPriorityDueDate(String line){
        return TextPriorityDueDatePattern.matches(line);
    }

    public static boolean isTodoPatternPriority(String line){
        return TextPriorityPattern.matches(line);
    }

    public static boolean isTodoPattern(String line){
        return TextPattern.matches(line);
    }
}
