package pl.catchex.synchonizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.catchex.model.ToDoItem;
import pl.catchex.model.ToDoRepository;
import pl.catchex.reader.ToDoReader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToDoRepositorySynchronizerTest {
    private ToDoReader reader;
    private ToDoRepository repository;
    private ToDoRepositorySynchronizer synchronizer;

    @BeforeEach
    void setUp() {
        reader = mock(ToDoReader.class);
        repository = new ToDoRepository();
        synchronizer = new ToDoRepositorySynchronizer(reader, repository);
    }

    @Test
    void noChanges_doesNotModifyRepository() throws IOException {
        // given
        ToDoItem item = new ToDoItem.Builder().task("task1").priority(ToDoItem.Priority.MEDIUM).dueDate(LocalDate.now()).build();
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
        ToDoItem item = new ToDoItem.Builder().task("task2").priority(ToDoItem.Priority.HIGH).dueDate(null).build();
        when(reader.read()).thenReturn(List.of(item));

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertTrue(repository.contains(item));
    }

    @Test
    void removeOnly_removesItemFromRepository() throws IOException {
        // given
        ToDoItem item = new ToDoItem.Builder().task("task3").priority(ToDoItem.Priority.LOW).dueDate(null).build();
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
        ToDoItem existing = new ToDoItem.Builder().task("existing").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
        ToDoItem newItem = new ToDoItem.Builder().task("new").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
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
        ToDoItem existing = new ToDoItem.Builder().task("existing-io").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
        repository.add(existing);
        when(reader.read()).thenThrow(new IOException("read error"));

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertTrue(repository.contains(existing));
    }
}
