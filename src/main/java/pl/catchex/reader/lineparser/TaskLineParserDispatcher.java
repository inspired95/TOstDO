package pl.catchex.reader.lineparser;

import pl.catchex.common.DateParser;
import pl.catchex.model.Task;
import pl.catchex.reader.PriorityParser;

import java.util.Optional;

public class TaskLineParserDispatcher {

    private static final String TASK_ITEM_LINE_PREFIX = "- [ ]";

    private final TaskLinePriorityDueDateParser taskLinePriorityDueDateParser;
    private final TaskLinePriorityParser taskLinePriorityParser;
    private final TaskLineBasicParser taskLineBasicParser;

    /**
     * Create a dispatcher that will attempt several line parsers in order
     * to extract a {@link Task} from a text line.
     *
     * @param priorityParser parser for priority tokens
     * @param dateParser     parser for date tokens
     */
    public TaskLineParserDispatcher(PriorityParser priorityParser, DateParser dateParser){
        this.taskLinePriorityDueDateParser = new TaskLinePriorityDueDateParser(priorityParser, dateParser);
        this.taskLinePriorityParser = new TaskLinePriorityParser(priorityParser);
        this.taskLineBasicParser = new TaskLineBasicParser();
    }

    /**
     * Try to parse a single line into a {@link Task}. Lines that do not start with
     * the expected prefix are ignored.
     *
     * @param line the text line to parse
     * @return Optional containing a {@link Task} when parsing succeeded, otherwise empty
     */
    public Optional<Task> parse(String line){
        if(isNotTaskLine(line)){
            return Optional.empty();
        }

        return taskLinePriorityDueDateParser.parse(line)
                .or(() -> taskLinePriorityParser.parse(line))
                .or(() -> taskLineBasicParser.parse(line));
    }

    private boolean isNotTaskLine(String line){
        return !line.startsWith(TASK_ITEM_LINE_PREFIX);
    }
}
