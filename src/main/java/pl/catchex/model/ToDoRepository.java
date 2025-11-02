package pl.catchex.model;

import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToDoRepository {
    private static final Logger logger = LoggerFactory.getLogger(ToDoRepository.class);

    private final Set<ToDoItem> concurrentSet = ConcurrentHashMap.newKeySet();

    private final CopyOnWriteArraySet<ToDoRepositoryListener> listeners = new CopyOnWriteArraySet<>();

    /**
     * Return a snapshot of all {@link ToDoItem} instances currently stored in the repository.
     *
     * @return a new Set containing all stored ToDoItem instances
     */
    public Set<ToDoItem> getAll() {
        return new HashSet<>(concurrentSet);
    }

    /**
     * Check whether the repository contains a given {@link ToDoItem}.
     *
     * @param item {@link ToDoItem} to check
     * @return true if the item is present, false otherwise
     */
    public boolean contains(ToDoItem item){
        return concurrentSet.contains(item);
    }

    /**
     * Add a {@link ToDoItem} to the repository. If the item was added successfully
     * registered listeners will be notified.
     *
     * @param item {@link ToDoItem} to add
     * @return true if the item was added (it was not present previously)
     */
    public boolean add(ToDoItem item){
        boolean added = concurrentSet.add(item);
        if (added) {
            // notify listeners only when the item was actually added
            for (ToDoRepositoryListener l : listeners) {
                try {
                    l.onToDoAdded(item);
                } catch (Exception ex) {
                    // protect repository from listener exception - log it
                    logger.warn("Listener threw exception during onToDoAdded for item {}: {}", item, ex.getMessage(), ex);
                }
            }
        }
        return added;
    }

    /**
     * Remove a {@link ToDoItem} from the repository. If the item was removed
     * registered listeners will be notified.
     *
     * @param item {@link ToDoItem} to remove
     * @return true if the item was removed (it was present before)
     */
    public boolean remove(ToDoItem item){
        boolean removed = concurrentSet.remove(item);
        if (removed) {
            for (ToDoRepositoryListener l : listeners) {
                try {
                    l.onToDoRemoved(item);
                } catch (Exception ex) {
                    // protect repository from listener exception - log it
                    logger.warn("Listener threw exception during onToDoRemoved for item {}: {}", item, ex.getMessage(), ex);
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
    public void addListener(ToDoRepositoryListener listener) {
        if (listener != null) {
            listeners.add(listener);
            logger.debug("ToDoRepository listener added: {}", listener);
        }
    }

    /**
     * Unregister a previously registered repository listener.
     *
     * @param listener listener to remove (ignored if null)
     */
    public void removeListener(ToDoRepositoryListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            logger.debug("ToDoRepository listener removed: {}", listener);
        }
    }
}
