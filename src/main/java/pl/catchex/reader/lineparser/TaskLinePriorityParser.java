package pl.catchex.reader.lineparser;

import pl.catchex.model.Task;
import pl.catchex.reader.PriorityParser;
import pl.catchex.reader.taskpattern.TextPriorityPattern;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskLinePriorityParser extends TaskLineParser {
    private final PriorityParser priorityParser;

    TaskLinePriorityParser(PriorityParser priorityParser){
        this.priorityParser = priorityParser;
    }

    @Override
    Optional<Task> toTask(Matcher matcher) {
        String text = matcher.group(TextPriorityPattern.TEXT_INDEX);
        Task.Builder task = new Task.Builder().task(text);
        priorityParser.parse(matcher.group(TextPriorityPattern.PRIORITY_INDEX)).ifPresent(task::priority);

        return Optional.of(task.build());
    }

    @Override
    Pattern pattern() {
        return TextPriorityPattern.TASK_PATTERN_PRIORITY;
    }
}
