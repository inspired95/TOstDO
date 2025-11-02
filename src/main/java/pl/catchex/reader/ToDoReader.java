package pl.catchex.reader;

import pl.catchex.config.AppConfiguration;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.lineparser.ToDoLineParserDispatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import pl.catchex.common.DateParser;

public class ToDoReader {

    private final ToDoLineParserDispatcher toDoLineParserDispatcher;

    private final Path todoListPath;

    public ToDoReader(AppConfiguration configuration){
        PriorityParser priorityParser = new PriorityParser(configuration.getConfiguration().getTodoItem().getPriority().getSymbol());
        DateParser dateParser = new DateParser(configuration.getConfiguration().getTodoItem().getDateFormat());
        this.toDoLineParserDispatcher = new ToDoLineParserDispatcher(priorityParser, dateParser);
        this.todoListPath = Paths.get(configuration.getConfiguration().getToDoFilePath());
    }

    public List<ToDoItem> read() throws IOException {
        return read(this.todoListPath);
    }

    private List<ToDoItem> read(Path path) throws IOException {
        List<ToDoItem> todos = new ArrayList<>();
        Files.readAllLines(path).forEach(line ->
            toDoLineParserDispatcher.parse(line).ifPresent(todos::add)
        );
        return todos;
    }

}
