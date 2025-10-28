package pl.catchex.reader.lineparser;

import pl.catchex.model.ToDoItem;
import pl.catchex.todopattern.TextPattern;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToDoLineBasicParser extends ToDoLineParser{
    ToDoLineBasicParser(){
    }


    @Override
    Optional<ToDoItem> toDoItem(Matcher matcher) {
        String text = matcher.group(TextPattern.TEXT_INDEX);
        ToDoItem toDoItem = new ToDoItem.Builder().task(text).build();

        return Optional.of(toDoItem);
    }

    @Override
    Pattern pattern() {
        return TextPattern.TODO_PATTERN;
    }
}
