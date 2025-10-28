package pl.catchex.reader.lineparser;

import pl.catchex.model.ToDoItem;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class ToDoLineParser {
    boolean isToDoTextValid(String text){
        return Objects.nonNull(text) && !text.isBlank();
    }

    Optional<ToDoItem> parse(String line){
        Matcher matcher = pattern().matcher(line);
        if (matcher.matches() && isToDoTextValid(matcher.group(1))) {
            return toDoItem(matcher);
        }
        return Optional.empty();
    }

    abstract Optional<ToDoItem> toDoItem(Matcher matcher);

    abstract Pattern pattern();
}
