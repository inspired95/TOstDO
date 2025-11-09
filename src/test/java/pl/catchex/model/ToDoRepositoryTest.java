package pl.catchex.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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
        // given
        ToDoItem item = new ToDoItem.Builder().task("t1").priority(ToDoItem.Priority.MEDIUM).dueDate(LocalDate.now()).build();

        // when / then
        assertFalse(repository.contains(item));
        assertTrue(repository.add(item));
        assertTrue(repository.contains(item));

        // when adding again
        assertFalse(repository.add(item));

        // when removing
        assertTrue(repository.remove(item));
        assertFalse(repository.contains(item));

        // when removing again
        assertFalse(repository.remove(item));
    }

    @Test
    void listenersAreNotifiedOnAddAndRemove() {
        // given
        ToDoItem item = new ToDoItem.Builder().task("t2").priority(ToDoItem.Priority.HIGH).dueDate(null).build();
        ToDoRepositoryListener listener = mock(ToDoRepositoryListener.class);
        repository.addListener(listener);

        // when
        assertTrue(repository.add(item));

        // then
        verify(listener, times(1)).onToDoAdded(item);

        // when
        assertTrue(repository.remove(item));

        // then
        verify(listener, times(1)).onToDoRemoved(item);
    }

    @Test
    void exceptionInListenerDoesNotBreakRepository() {
        // given
        ToDoItem item = new ToDoItem.Builder().task("t3").priority(ToDoItem.Priority.LOW).dueDate(null).build();
        ToDoRepositoryListener badListener = mock(ToDoRepositoryListener.class);
        doThrow(new RuntimeException("boom")).when(badListener).onToDoAdded(item);
        repository.addListener(badListener);

        // when
        boolean added = repository.add(item);

        // then
        assertTrue(added);
        assertTrue(repository.contains(item));
    }

    @Test
    void multipleListenersReceiveNotifications() {
        // given
        ToDoItem item = new ToDoItem.Builder().task("t4").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
        ToDoRepositoryListener l1 = mock(ToDoRepositoryListener.class);
        ToDoRepositoryListener l2 = mock(ToDoRepositoryListener.class);
        repository.addListener(l1);
        repository.addListener(l2);

        // when
        repository.add(item);

        // then
        verify(l1, times(1)).onToDoAdded(item);
        verify(l2, times(1)).onToDoAdded(item);

        // when
        repository.remove(item);

        // then
        verify(l1, times(1)).onToDoRemoved(item);
        verify(l2, times(1)).onToDoRemoved(item);
    }

    @Test
    void removeListenerStopsNotifications() {
        // given
        ToDoItem item = new ToDoItem.Builder().task("t5").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
        ToDoRepositoryListener listener = mock(ToDoRepositoryListener.class);
        repository.addListener(listener);

        // when
        repository.removeListener(listener);

        // when
        repository.add(item);

        // then
        verify(listener, times(0)).onToDoAdded(item);

        // when
        repository.remove(item);

        // then
        verify(listener, times(0)).onToDoRemoved(item);
    }
}
