package pl.catchex.reader;

import pl.catchex.config.AppConfiguration;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.lineparser.ToDoLineParserDispatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import pl.catchex.common.DateParser;

public class ToDoReader {

    private final ToDoLineParserDispatcher toDoLineParserDispatcher;

    public ToDoReader(AppConfiguration configuration){
        PriorityParser priorityParser = new PriorityParser(configuration.getConfiguration().getTodoItem().getPriority().getSymbol());
        DateParser dateParser = new DateParser(configuration.getConfiguration().getTodoItem().getDateFormat());
        this.toDoLineParserDispatcher = new ToDoLineParserDispatcher(priorityParser, dateParser);
    }

    public List<ToDoItem> read(Path todoListPath) throws IOException {
        List<ToDoItem> todos = new ArrayList<>();
        Files.readAllLines(todoListPath).forEach(line ->
            toDoLineParserDispatcher.parse(line).ifPresent(todos::add)
        );
        return todos;
    }

}
