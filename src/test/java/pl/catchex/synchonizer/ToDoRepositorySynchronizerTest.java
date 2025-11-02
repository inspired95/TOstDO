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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToDoRepositorySynchronizerTest {
    private ToDoReader reader;
    private ToDoRepository repository;
    private ToDoRepositorySynchronizer synchronizer;

    @BeforeEach
    void setUp() {
        reader = mock(ToDoReader.class);
        // use real repository implementation for integration-style testing
        repository = new ToDoRepository();
        synchronizer = new ToDoRepositorySynchronizer(reader, repository);
    }

    @Test
    void noChanges_doesNotModifyRepository() throws IOException {
        ToDoItem item = new ToDoItem.Builder().task("task1").priority(ToDoItem.Priority.MEDIUM).dueDate(LocalDate.now()).build();
        when(reader.read()).thenReturn(List.of(item));
        // repository already contains the item
        repository.add(item);

        synchronizer.onFileModified(Path.of("dummy"));

        // repository should still contain the item
        assertTrue(repository.contains(item));
    }

    @Test
    void addOnly_addsItemToRepository() throws IOException {
        ToDoItem item = new ToDoItem.Builder().task("task2").priority(ToDoItem.Priority.HIGH).dueDate(null).build();
        when(reader.read()).thenReturn(List.of(item));

        synchronizer.onFileModified(Path.of("dummy"));

        assertTrue(repository.contains(item));
    }

    @Test
    void removeOnly_removesItemFromRepository() throws IOException {
        ToDoItem item = new ToDoItem.Builder().task("task3").priority(ToDoItem.Priority.LOW).dueDate(null).build();
        when(reader.read()).thenReturn(List.of());
        // repository initially contains the item
        repository.add(item);

        synchronizer.onFileModified(Path.of("dummy"));

        assertFalse(repository.contains(item));
    }

    @Test
    void addAndRemove_updatesRepositoryAccordingly() throws IOException {
        ToDoItem existing = new ToDoItem.Builder().task("existing").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
        ToDoItem newItem = new ToDoItem.Builder().task("new").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();

        // initial state: repository has existing
        repository.add(existing);
        when(reader.read()).thenReturn(List.of(newItem));

        synchronizer.onFileModified(Path.of("dummy"));

        assertFalse(repository.contains(existing));
        assertTrue(repository.contains(newItem));
    }
}
