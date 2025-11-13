package pl.catchex.reader.lineparser;

import pl.catchex.common.DateParser;
import pl.catchex.model.Task;
import pl.catchex.reader.PriorityParser;
import pl.catchex.reader.taskpattern.TextPriorityDueDatePattern;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskLinePriorityDueDateParser extends TaskLineParser {

    private final PriorityParser priorityParser;
    private final DateParser dateParser;

    TaskLinePriorityDueDateParser(PriorityParser priorityParser, DateParser dateParser){
        this.priorityParser = priorityParser;
        this.dateParser = dateParser;
    }

    Optional<Task> toTask(Matcher matcher){
        String text = matcher.group(TextPriorityDueDatePattern.TEXT_INDEX);
        Task.Builder task = new Task.Builder().task(text);
        priorityParser.parse(matcher.group(TextPriorityDueDatePattern.PRIORITY_INDEX)).ifPresent(task::priority);
        dateParser.parse(matcher.group(TextPriorityDueDatePattern.DUE_DATE_INDEX)).ifPresent(task::dueDate);

        return Optional.of(task.build());
    }

    @Override
    Pattern pattern() {
        return TextPriorityDueDatePattern.TASK_PATTERN_PRIORITY_DUE_DATE;
    }
}
