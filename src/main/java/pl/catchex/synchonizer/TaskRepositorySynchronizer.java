package pl.catchex.synchonizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.filewatcher.FileChangeListener;
import pl.catchex.model.Task;
import pl.catchex.model.TaskRepository;
import pl.catchex.reader.TaskReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Synchronizes the in-memory {@link TaskRepository} with the content of the
 * tasks file. It implements {@link FileChangeListener} so it may be
 * registered with a {@link pl.catchex.filewatcher.FileWatcher}.
 */
public class TaskRepositorySynchronizer implements FileChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(TaskRepositorySynchronizer.class);

    private final TaskReader taskReader;
    private final TaskRepository repository;

    /**
     * Create a synchronizer using the provided reader and repository.
     *
     * @param taskReader reader that reads {@link Task} instances from the configured file
     * @param repository repository to be synchronized
     */
    public TaskRepositorySynchronizer(TaskReader taskReader, TaskRepository repository) {
        this.taskReader = taskReader;
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
        Optional<Set<Task>> readTasksOpt = readTasks();
        if (readTasksOpt.isEmpty()) {
            logger.info("Reading tasks failed, skipping synchronization");
            return;
        }

        Set<Task> tasksFromFile = readTasksOpt.get();

        Set<Task> tasksFromRepository = repository.getAll();


        if (tasksFromRepository.equals(tasksFromFile)) {
            logger.debug("No changes detected, skipping synchronization");
            return;
        }

        Set<Task> toRemove = computeToRemove(tasksFromRepository, tasksFromFile);
        Set<Task> toAdd = computeToAdd(tasksFromRepository, tasksFromFile);

        applyRemovals(toRemove);
        applyAdditions(toAdd);
    }

    private Optional<Set<Task>> readTasks() {
        try {
            List<Task> items = taskReader.read();
            return Optional.of(new HashSet<>(items));
        } catch (IOException e) {
            logger.warn("Cannot read [ message={} ]", e.getMessage());
            return Optional.empty();
        }
    }

    private Set<Task> computeToRemove(Set<Task> currentRepo, Set<Task> fromFile) {
        Set<Task> toRemove = new HashSet<>(currentRepo);
        toRemove.removeAll(fromFile);
        return toRemove;
    }

    private Set<Task> computeToAdd(Set<Task> currentRepo, Set<Task> fromFile) {
        Set<Task> toAdd = new HashSet<>(fromFile);
        toAdd.removeAll(currentRepo);
        return toAdd;
    }

    private void applyRemovals(Set<Task> toRemove) {
        for (Task item : toRemove) {
            boolean removed = repository.remove(item);
            if (removed) {
                logger.info("Removed  [item={}]", item);
            } else {
                logger.debug("Attempted to remove but it was not present [item={}]", item);
            }
        }
    }

    private void applyAdditions(Set<Task> toAdd) {
        for (Task item : toAdd) {
            boolean added = repository.add(item);
            if (added) {
                logger.info("Added task [item={}]", item);
            } else {
                logger.debug("Attempted to add task but it was already present [item={}]", item);
            }
        }
    }
}