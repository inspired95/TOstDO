package pl.catchex.synchonizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.catchex.model.Task;
import pl.catchex.model.TaskRepository;
import pl.catchex.reader.TaskReader;

import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskRepositorySynchronizerReaderExceptionTest {
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
    void readerIOException_doesNotModifyRepository() throws IOException {
        // given
        Task existing = new Task.Builder().task("existing").priority(Task.Priority.MEDIUM).dueDate(null).build();
        repository.add(existing);

        when(reader.read()).thenThrow(new IOException("io error"));

        // when
        synchronizer.onFileModified(Path.of("dummy"));

        // then
        assertTrue(repository.contains(existing));
    }
}
