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

    /**
     * Create a dispatcher that will attempt several line parsers in order
     * to extract a {@link ToDoItem} from a text line.
     *
     * @param priorityParser parser for priority tokens
     * @param dateParser     parser for date tokens
     */
    public ToDoLineParserDispatcher(PriorityParser priorityParser, DateParser dateParser){
        this.toDoLinePriorityDueDateParser = new ToDoLinePriorityDueDateParser(priorityParser, dateParser);
        this.toDoLinePriorityParser = new ToDoLinePriorityParser(priorityParser);
        this.toDoLineBasicParser = new ToDoLineBasicParser();
    }

    /**
     * Try to parse a single line into a ToDoItem. Lines that do not start with
     * the expected prefix are ignored.
     *
     * @param line the text line to parse
     * @return Optional containing a ToDoItem when parsing succeeded, otherwise empty
     */
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
