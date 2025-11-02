package pl.catchex.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToDoRepositoryTest {
    private ToDoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ToDoRepository();
    }

    @Test
    void addAndContainsAndRemove() {
        ToDoItem item = new ToDoItem.Builder().task("t1").priority(ToDoItem.Priority.MEDIUM).dueDate(LocalDate.now()).build();
        assertFalse(repository.contains(item));
        assertTrue(repository.add(item));
        assertTrue(repository.contains(item));
        // adding again should return false
        assertFalse(repository.add(item));
        assertTrue(repository.remove(item));
        assertFalse(repository.contains(item));
        // removing again should return false
        assertFalse(repository.remove(item));
    }

    @Test
    void listenersAreNotifiedOnAddAndRemove() {
        ToDoItem item = new ToDoItem.Builder().task("t2").priority(ToDoItem.Priority.HIGH).dueDate(null).build();
        ToDoRepositoryListener listener = mock(ToDoRepositoryListener.class);
        repository.addListener(listener);

        assertTrue(repository.add(item));
        verify(listener, times(1)).onToDoAdded(item);

        assertTrue(repository.remove(item));
        verify(listener, times(1)).onToDoRemoved(item);
    }

    @Test
    void exceptionInListenerDoesNotBreakRepository() {
        ToDoItem item = new ToDoItem.Builder().task("t3").priority(ToDoItem.Priority.LOW).dueDate(null).build();
        ToDoRepositoryListener badListener = mock(ToDoRepositoryListener.class);
        doThrow(new RuntimeException("boom")).when(badListener).onToDoAdded(item);
        repository.addListener(badListener);

        // Even if listener throws, repository should still add the item
        assertTrue(repository.add(item));
        assertTrue(repository.contains(item));
    }

    @Test
    void multipleListenersReceiveNotifications() {
        ToDoItem item = new ToDoItem.Builder().task("t4").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
        ToDoRepositoryListener l1 = mock(ToDoRepositoryListener.class);
        ToDoRepositoryListener l2 = mock(ToDoRepositoryListener.class);
        repository.addListener(l1);
        repository.addListener(l2);

        repository.add(item);
        verify(l1, times(1)).onToDoAdded(item);
        verify(l2, times(1)).onToDoAdded(item);

        repository.remove(item);
        verify(l1, times(1)).onToDoRemoved(item);
        verify(l2, times(1)).onToDoRemoved(item);
    }
}

