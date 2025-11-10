package pl.catchex.bootstrap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppDirectoryInitializerTest {

    private FileSystemService fs;
    private SampleTodoContentProvider sampleTodoContentProvider;
    private AppDirectoryInitializer initializer;
    private Path home;
    private Path appDir;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fs = mock(FileSystemService.class);
        sampleTodoContentProvider = new SampleTodoContentProvider();
        initializer = new AppDirectoryInitializer(fs, sampleTodoContentProvider);
        // Use injected temporary directory as the 'home' to avoid touching real user.home
        home = tempDir;
        appDir = home.resolve(AppConstants.APP_DIR_NAME);
    }

    @Test
    void createsAppDirectoryAndFilesWhenMissing() throws IOException {
        Path config = appDir.resolve(AppConstants.CONFIG_FILENAME);
        Path todo = appDir.resolve(AppConstants.TODO_FILENAME);

        when(fs.getUserHome()).thenReturn(home);
        when(fs.exists(appDir)).thenReturn(false);
        when(fs.exists(config)).thenReturn(false);
        when(fs.exists(todo)).thenReturn(false);
        when(fs.getResourceAsStream(AppConstants.RESOURCE_CONFIGURATION))
                .thenReturn(new ByteArrayInputStream(("configuration:\n  todoFilePath: [path_to_todo.md_file]\n").getBytes(StandardCharsets.UTF_8)));

        initializer.perform();

        verify(fs).createDirectories(appDir);
        // resource is read, modified and written to config.yaml using writeString
        ArgumentCaptor<String> configCaptor = ArgumentCaptor.forClass(String.class);
        verify(fs).writeString(eq(config), configCaptor.capture(), eq(StandardCharsets.UTF_8));
        String configContent = configCaptor.getValue();
        assertTrue(configContent.contains("todoFilePath"));
        assertTrue(configContent.contains(appDir.resolve(AppConstants.TODO_FILENAME).toString()));

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(fs).writeString(eq(todo), contentCaptor.capture(), eq(StandardCharsets.UTF_8));
        String content = contentCaptor.getValue();
        assertTrue(content.contains("TOstDO"));
    }

    @Test
    void doesNotOverwriteExistingFiles() throws IOException {
        Path config = appDir.resolve(AppConstants.CONFIG_FILENAME);
        Path todo = appDir.resolve(AppConstants.TODO_FILENAME);

        when(fs.getUserHome()).thenReturn(home);
        when(fs.exists(appDir)).thenReturn(true);
        when(fs.exists(config)).thenReturn(true);
        when(fs.exists(todo)).thenReturn(true);

        initializer.perform();

        verify(fs, never()).createDirectories(any());
        verify(fs, never()).copy(any(), any());
        verify(fs, never()).writeString(any(), any(), any());
    }

    @Test
    void handlesMissingResourceGracefully() throws IOException {
        Path config = appDir.resolve(AppConstants.CONFIG_FILENAME);
        Path todo = appDir.resolve(AppConstants.TODO_FILENAME);

        when(fs.getUserHome()).thenReturn(home);
        when(fs.exists(appDir)).thenReturn(false);
        when(fs.exists(config)).thenReturn(false);
        when(fs.exists(todo)).thenReturn(false);
        when(fs.getResourceAsStream(AppConstants.RESOURCE_CONFIGURATION))
                .thenReturn(null);

        initializer.perform();

        verify(fs).createDirectories(appDir);
        verify(fs, never()).copy(any(), eq(config));
        verify(fs).writeString(eq(todo), anyString(), eq(StandardCharsets.UTF_8));
    }
}
