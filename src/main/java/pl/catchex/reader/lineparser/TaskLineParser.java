package pl.catchex.reader.lineparser;

import pl.catchex.model.Task;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class TaskLineParser {
    boolean isTaskTextValid(String text){
        return Objects.nonNull(text) && !text.isBlank();
    }

    Optional<Task> parse(String line){
        Matcher matcher = pattern().matcher(line);
        if (matcher.matches() && isTaskTextValid(matcher.group(1))) {
            return toTask(matcher);
        }
        return Optional.empty();
    }

    abstract Optional<Task> toTask(Matcher matcher);

    abstract Pattern pattern();
}
