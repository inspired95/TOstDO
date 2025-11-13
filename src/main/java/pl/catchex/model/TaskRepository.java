package pl.catchex.model;

import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRepository {
    private static final Logger logger = LoggerFactory.getLogger(TaskRepository.class);

    private final Set<Task> concurrentSet = ConcurrentHashMap.newKeySet();

    private final CopyOnWriteArraySet<TaskRepositoryListener> listeners = new CopyOnWriteArraySet<>();

    /**
     * Return a snapshot of all {@link Task} instances currently stored in the repository.
     *
     * @return a new Set containing all stored Task instances
     */
    public Set<Task> getAll() {
        return new HashSet<>(concurrentSet);
    }

    /**
     * Check whether the repository contains a given {@link Task}.
     *
     * @param item {@link Task} to check
     * @return true if the item is present, false otherwise
     */
    public boolean contains(Task item){
        return concurrentSet.contains(item);
    }

    /**
     * Add a {@link Task} to the repository. If the item was added successfully
     * registered listeners will be notified.
     *
     * @param item {@link Task} to add
     * @return true if the item was added (it was not present previously)
     */
    public boolean add(Task item){
        boolean added = concurrentSet.add(item);
        if (added) {
            // notify listeners only when the item was actually added
            for (TaskRepositoryListener l : listeners) {
                try {
                    l.onTaskAdded(item);
                } catch (Exception ex) {
                    // protect repository from listener exception - log it
                    logger.warn("Listener threw exception for item {}: {}", item, ex.getMessage(), ex);
                }
            }
        }
        return added;
    }

    /**
     * Remove a {@link Task} from the repository. If the item was removed
     * registered listeners will be notified.
     *
     * @param item {@link Task} to remove
     * @return true if the item was removed (it was present before)
     */
    public boolean remove(Task item){
        boolean removed = concurrentSet.remove(item);
        if (removed) {
            for (TaskRepositoryListener l : listeners) {
                try {
                    l.onTaskRemoved(item);
                } catch (Exception ex) {
                    // protect repository from listener exception - log it
                    logger.warn("Listener threw exception for item {}: {}", item, ex.getMessage(), ex);
                }
            }
        }
        return removed;
    }

    /**
     * Register a listener to be notified of repository changes.
     *
     * @param listener listener to register (ignored if null)
     */
    public void addListener(TaskRepositoryListener listener) {
        if (listener != null) {
            listeners.add(listener);
            logger.debug("Listener added: {}", listener);
        }
    }

    /**
     * Unregister a previously registered repository listener.
     *
     * @param listener listener to remove (ignored if null)
     */
    public void removeListener(TaskRepositoryListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            logger.debug("Listener removed: {}", listener);
        }
    }
}
