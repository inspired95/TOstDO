package pl.catchex.reader.lineparser;

import pl.catchex.model.Task;
import pl.catchex.reader.taskpattern.TextPattern;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskLineBasicParser extends TaskLineParser {
    TaskLineBasicParser(){
    }


    @Override
    Optional<Task> toTask(Matcher matcher) {
        String text = matcher.group(TextPattern.TEXT_INDEX);
        Task task = new Task.Builder().task(text).build();

        return Optional.of(task);
    }

    @Override
    Pattern pattern() {
        return TextPattern.TASK_PATTERN;
    }
}
