package pl.catchex.synchonizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.catchex.model.Task;
import pl.catchex.model.TaskRepository;
import pl.catchex.reader.TaskReader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskRepositorySynchronizerTest {
    private TaskReader reader;
    private TaskRepository repository;
    private TaskRepositorySynchronizer synchronizer;

    @BeforeEach
    void setUp() {
        reader = mock(TaskReader.class);
        repository = new TaskRepository();
        synchronizer = new TaskRepositorySynchronizer(reader, repository);
    }

    @Test
    void noChanges_doesNotModifyRepository() throws IOException {
        // given
        Task item = new Task.Builder().task("task1").priority(Task.Priority.MEDIUM).dueDate(LocalDate.now()).build();
        when(reader.read()).thenReturn(List.of(item));
        repository.add(item);

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertTrue(repository.contains(item));
    }

    @Test
    void addOnly_addsItemToRepository() throws IOException {
        // given
        Task item = new Task.Builder().task("task2").priority(Task.Priority.HIGH).dueDate(null).build();
        when(reader.read()).thenReturn(List.of(item));

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertTrue(repository.contains(item));
    }

    @Test
    void removeOnly_removesItemFromRepository() throws IOException {
        // given
        Task item = new Task.Builder().task("task3").priority(Task.Priority.LOW).dueDate(null).build();
        when(reader.read()).thenReturn(List.of());
        repository.add(item);

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertFalse(repository.contains(item));
    }

    @Test
    void addAndRemove_updatesRepositoryAccordingly() throws IOException {
        // given
        Task existing = new Task.Builder().task("existing").priority(Task.Priority.MEDIUM).dueDate(null).build();
        Task newItem = new Task.Builder().task("new").priority(Task.Priority.MEDIUM).dueDate(null).build();
        repository.add(existing);
        when(reader.read()).thenReturn(List.of(newItem));

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertFalse(repository.contains(existing));
        assertTrue(repository.contains(newItem));
    }

    @Test
    void readThrowsIOException_skipsSynchronization() throws IOException {
        // given
        Task existing = new Task.Builder().task("existing-io").priority(Task.Priority.MEDIUM).dueDate(null).build();
        repository.add(existing);
        when(reader.read()).thenThrow(new IOException("read error"));

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertTrue(repository.contains(existing));
    }
}
