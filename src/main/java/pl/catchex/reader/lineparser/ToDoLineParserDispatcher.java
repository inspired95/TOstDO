package pl.catchex.reader.lineparser;

import pl.catchex.common.DateParser;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.PriorityParser;

import java.util.Optional;

public class ToDoLineParserDispatcher {

    private static final String TO_DO_ITEM_LINE_PREFIX = "- [ ]";

    private final ToDoLinePriorityDueDateParser toDoLinePriorityDueDateParser;
    private final ToDoLinePriorityParser toDoLinePriorityParser;
    private final ToDoLineBasicParser toDoLineBasicParser;

    public ToDoLineParserDispatcher(PriorityParser priorityParser, DateParser dateParser){
        this.toDoLinePriorityDueDateParser = new ToDoLinePriorityDueDateParser(priorityParser, dateParser);
        this.toDoLinePriorityParser = new ToDoLinePriorityParser(priorityParser);
        this.toDoLineBasicParser = new ToDoLineBasicParser();
    }

    public Optional<ToDoItem> parse(String line){
        if(isNotToDoLine(line)){
            return Optional.empty();
        }

        return toDoLinePriorityDueDateParser.parse(line)
                .or(() -> toDoLinePriorityParser.parse(line))
                .or(() -> toDoLineBasicParser.parse(line));
    }

    private boolean isNotToDoLine(String line){
        return !line.startsWith(TO_DO_ITEM_LINE_PREFIX);
    }
}
