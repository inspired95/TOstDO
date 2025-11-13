package pl.catchex.reader;

import pl.catchex.model.Task;
import pl.catchex.reader.lineparser.TaskLineParserDispatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Component responsible for reading {@link Task} instances from a text file and
 * converting each text line into a {@link Task} using the provided
 * {@link TaskLineParserDispatcher}.
 */
public class TaskReader {

    private final TaskLineParserDispatcher taskLineParserDispatcher;

    private final Path tasksPath;

    /**
     * Create a new TaskReader.
     *
     * @param taskLineParserDispatcher dispatcher used to parse individual lines
     * @param tasksPath             path to file with tasks
     */
    public TaskReader(TaskLineParserDispatcher taskLineParserDispatcher, Path tasksPath){
        this.taskLineParserDispatcher = taskLineParserDispatcher;
        this.tasksPath = tasksPath;
    }

    /**
     * Read all {@link Task} instances from the configured file.
     *
     * @return list of parsed {@link Task} instances (empty list if no valid lines)
     * @throws IOException when reading the file fails
     */
    public List<Task> read() throws IOException {
        return read(this.tasksPath);
    }

    private List<Task> read(Path path) throws IOException {
        List<String> lines = readAllLines(path);
        return parseLines(lines);
    }

    private List<String> readAllLines(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    private List<Task> parseLines(List<String> lines) {
        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            taskLineParserDispatcher.parse(line).ifPresent(tasks::add);
        }
        return tasks;
    }

}
