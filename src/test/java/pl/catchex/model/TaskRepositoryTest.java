package pl.catchex.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskRepositoryTest {
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TaskRepository();
    }

    @Test
    void addAndContainsAndRemove() {
        // given
        Task item = new Task.Builder().task("t1").priority(Task.Priority.MEDIUM).dueDate(LocalDate.now()).build();

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
        Task item = new Task.Builder().task("t2").priority(Task.Priority.HIGH).dueDate(null).build();
        TaskRepositoryListener listener = mock(TaskRepositoryListener.class);
        repository.addListener(listener);

        // when
        assertTrue(repository.add(item));

        // then
        verify(listener, times(1)).onTaskAdded(item);

        // when
        assertTrue(repository.remove(item));

        // then
        verify(listener, times(1)).onTaskRemoved(item);
    }

    @Test
    void exceptionInListenerDoesNotBreakRepository() {
        // given
        Task item = new Task.Builder().task("t3").priority(Task.Priority.LOW).dueDate(null).build();
        TaskRepositoryListener badListener = mock(TaskRepositoryListener.class);
        doThrow(new RuntimeException("boom")).when(badListener).onTaskAdded(item);
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
        Task item = new Task.Builder().task("t4").priority(Task.Priority.MEDIUM).dueDate(null).build();
        TaskRepositoryListener l1 = mock(TaskRepositoryListener.class);
        TaskRepositoryListener l2 = mock(TaskRepositoryListener.class);
        repository.addListener(l1);
        repository.addListener(l2);

        // when
        repository.add(item);

        // then
        verify(l1, times(1)).onTaskAdded(item);
        verify(l2, times(1)).onTaskAdded(item);

        // when
        repository.remove(item);

        // then
        verify(l1, times(1)).onTaskRemoved(item);
        verify(l2, times(1)).onTaskRemoved(item);
    }

    @Test
    void removeListenerStopsNotifications() {
        // given
        Task item = new Task.Builder().task("t5").priority(Task.Priority.MEDIUM).dueDate(null).build();
        TaskRepositoryListener listener = mock(TaskRepositoryListener.class);
        repository.addListener(listener);

        // when
        repository.removeListener(listener);

        // when
        repository.add(item);

        // then
        verify(listener, times(0)).onTaskAdded(item);

        // when
        repository.remove(item);

        // then
        verify(listener, times(0)).onTaskRemoved(item);
    }
}
