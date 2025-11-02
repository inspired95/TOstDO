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

    public Set<ToDoItem> getAll() {
        return new HashSet<>(concurrentSet);
    }

    public boolean contains(ToDoItem item){
        return concurrentSet.contains(item);
    }

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

    public void addListener(ToDoRepositoryListener listener) {
        if (listener != null) {
            listeners.add(listener);
            logger.debug("ToDoRepository listener added: {}", listener);
        }
    }

    public void removeListener(ToDoRepositoryListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            logger.debug("ToDoRepository listener removed: {}", listener);
        }
    }
}
