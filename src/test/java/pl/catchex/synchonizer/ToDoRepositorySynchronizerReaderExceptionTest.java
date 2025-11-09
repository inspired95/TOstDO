package pl.catchex.synchonizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.catchex.model.ToDoItem;
import pl.catchex.model.ToDoRepository;
import pl.catchex.reader.ToDoReader;

import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToDoRepositorySynchronizerReaderExceptionTest {
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
    void readerIOException_doesNotModifyRepository() throws IOException {
        // given
        ToDoItem existing = new ToDoItem.Builder().task("existing").priority(ToDoItem.Priority.MEDIUM).dueDate(null).build();
        repository.add(existing);

        when(reader.read()).thenThrow(new IOException("io error"));

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertTrue(repository.contains(existing));
    }
}
