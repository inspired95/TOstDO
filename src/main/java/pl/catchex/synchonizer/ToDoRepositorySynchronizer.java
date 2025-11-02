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

/**
 * Synchronizes the in-memory {@link ToDoRepository} with the content of the
 * ToDo file. It implements {@link FileChangeListener} so it may be //NOSONAR
 * registered with a {@link pl.catchex.filewatcher.FileWatcher}.
 */
public class ToDoRepositorySynchronizer implements FileChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ToDoRepositorySynchronizer.class);

    private final ToDoReader toDoReader;
    private final ToDoRepository repository;

    /**
     * Create a synchronizer using the provided reader and repository.
     *
     * @param toDoReader reader that reads {@link ToDoItem} instances from the configured file
     * @param repository repository to be synchronized
     */
    public ToDoRepositorySynchronizer(ToDoReader toDoReader, ToDoRepository repository) {
        this.toDoReader = toDoReader;
        this.repository = repository;
    }

    /**
     * Public API to trigger synchronization on demand. This method is
     * equivalent to receiving a file-modified event.
     */
    public void synchronizeRepository() {
        onFileModified(null);
    }

    @Override
    public void onFileModified(Path filePath) {
        Set<ToDoItem> readToDos = readToDos();
        if (readToDos.isEmpty()) {
            logger.info("No ToDo items read from file, skipping synchronization");
            return;
        }

        Set<ToDoItem> todosInRepository = repository.getAll();

        if (todosInRepository.equals(readToDos)) {
            logger.debug("No changes detected for ToDos");
            return;
        }

        Set<ToDoItem> toRemove = computeToRemove(todosInRepository, readToDos);
        Set<ToDoItem> toAdd = computeToAdd(todosInRepository, readToDos);

        applyRemovals(toRemove);
        applyAdditions(toAdd);
    }

    private Set<ToDoItem> readToDos() {
        try {
            List<ToDoItem> items = toDoReader.read();
            return new HashSet<>(items);
        } catch (IOException e) {
            logger.warn("Cannot read ToDos [ message={} ]", e.getMessage());
            return Set.of();
        }
    }

    private Set<ToDoItem> computeToRemove(Set<ToDoItem> currentRepo, Set<ToDoItem> fromFile) {
        Set<ToDoItem> toRemove = new HashSet<>(currentRepo);
        toRemove.removeAll(fromFile);
        return toRemove;
    }

    private Set<ToDoItem> computeToAdd(Set<ToDoItem> currentRepo, Set<ToDoItem> fromFile) {
        Set<ToDoItem> toAdd = new HashSet<>(fromFile);
        toAdd.removeAll(currentRepo);
        return toAdd;
    }

    private void applyRemovals(Set<ToDoItem> toRemove) {
        for (ToDoItem item : toRemove) {
            boolean removed = repository.remove(item);
            if (removed) {
                logger.info("Removed ToDo [item={}]", item);
            } else {
                logger.debug("Attempted to remove ToDo but it was not present [item={}]", item);
            }
        }
    }

    private void applyAdditions(Set<ToDoItem> toAdd) {
        for (ToDoItem item : toAdd) {
            boolean added = repository.add(item);
            if (added) {
                logger.info("Added ToDo [item={}]", item);
            } else {
                logger.debug("Attempted to add ToDo but it was already present [item={}]", item);
            }
        }
    }
}