package pl.catchex.reader.lineparser;

import pl.catchex.model.ToDoItem;
import pl.catchex.reader.PriorityParser;
import pl.catchex.todopattern.TextPriorityPattern;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToDoLinePriorityParser extends ToDoLineParser {
    private final PriorityParser priorityParser;

    ToDoLinePriorityParser(PriorityParser priorityParser){
        this.priorityParser = priorityParser;
    }

    @Override
    Optional<ToDoItem> toDoItem(Matcher matcher) {
        String text = matcher.group(TextPriorityPattern.TEXT_INDEX);
        ToDoItem.Builder task = new ToDoItem.Builder().task(text);
        priorityParser.parse(matcher.group(TextPriorityPattern.PRIORITY_INDEX)).ifPresent(task::priority);

        return Optional.of(task.build());
    }

    @Override
    Pattern pattern() {
        return TextPriorityPattern.TODO_PATTERN_PRIORITY;
    }
}
