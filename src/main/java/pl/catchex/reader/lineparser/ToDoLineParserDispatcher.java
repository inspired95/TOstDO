package pl.catchex.reader.lineparser;

import pl.catchex.common.DateParser;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.PriorityParser;

import java.util.Optional;
import static pl.catchex.model.ToDoLinePatternsUtil.isTodoPatternPriorityDueDate;
import static pl.catchex.model.ToDoLinePatternsUtil.isTodoPatternPriority;
import static pl.catchex.model.ToDoLinePatternsUtil.isTodoPattern;
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
        if(isToDoLine(line)){
            if(isTodoPatternPriorityDueDate(line)){
                return toDoLinePriorityDueDateParser.parse(line);
            }
            if(isTodoPatternPriority(line)){
                return toDoLinePriorityParser.parse(line);
            }
            if(isTodoPattern(line)){
                return toDoLineBasicParser.parse(line);
            }
        }
        return Optional.empty();
    }

    private boolean isToDoLine(String line){
        return line.startsWith(TO_DO_ITEM_LINE_PREFIX);
    }
}
