package pl.catchex.synchonizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.filewatcher.FileChangeListener;
import pl.catchex.model.ToDoItem;
import pl.catchex.model.ToDoRepository;
import pl.catchex.reader.ToDoReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToDoRepositorySynchronizer implements FileChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ToDoRepositorySynchronizer.class);

    private final ToDoReader toDoReader;

    private final ToDoRepository repository;

    public ToDoRepositorySynchronizer(ToDoReader toDoReader, ToDoRepository repository){
        this.toDoReader = toDoReader;
        this.repository = repository;
    }

    @Override
    public void onFileModified(Path filePath) {
        try {
            List<ToDoItem> toDoItems = toDoReader.read();
            Set<ToDoItem> readToDos = new HashSet<>(toDoItems);

            Set<ToDoItem> todosInRepository = repository.getAll();

            // If the sets are identical — nothing to do
            if (todosInRepository.equals(readToDos)) {
                logger.debug("No changes detected for ToDos");
                return;
            }

            // items to remove = present in repo but not in file
            Set<ToDoItem> toRemove = new HashSet<>(todosInRepository);
            toRemove.removeAll(readToDos);

            // items to add = present in file but not in repo
            Set<ToDoItem> toAdd = new HashSet<>(readToDos);
            toAdd.removeAll(todosInRepository);

            for (ToDoItem item : toRemove) {
                boolean removed = repository.remove(item);
                if (removed) {
                    logger.info("Removed ToDo [item={}]", item);
                }
            }

            for (ToDoItem item : toAdd) {
                boolean added = repository.add(item);
                if (added) {
                    logger.info("Added ToDo [item={}]", item);
                }
            }

        } catch (IOException e) {
            logger.warn("Cannot read ToDos [ message={} ]", e.getMessage());
        }
    }
}
