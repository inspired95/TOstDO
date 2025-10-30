package pl.catchex.reader.lineparser;

import pl.catchex.common.DateParser;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.PriorityParser;
import pl.catchex.reader.todopattern.TextPriorityDueDatePattern;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToDoLinePriorityDueDateParser extends ToDoLineParser{

    private final PriorityParser priorityParser;
    private final DateParser dateParser;

    ToDoLinePriorityDueDateParser(PriorityParser priorityParser, DateParser dateParser){
        this.priorityParser = priorityParser;
        this.dateParser = dateParser;
    }

    Optional<ToDoItem> toDoItem(Matcher matcher){
        String text = matcher.group(TextPriorityDueDatePattern.TEXT_INDEX);
        ToDoItem.Builder task = new ToDoItem.Builder().task(text);
        priorityParser.parse(matcher.group(TextPriorityDueDatePattern.PRIORITY_INDEX)).ifPresent(task::priority);
        dateParser.parse(matcher.group(TextPriorityDueDatePattern.DUE_DATE_INDEX)).ifPresent(task::dueDate);

        return Optional.of(task.build());
    }

    @Override
    Pattern pattern() {
        return TextPriorityDueDatePattern.TODO_PATTERN_PRIORITY_DUE_DATE;
    }
}
