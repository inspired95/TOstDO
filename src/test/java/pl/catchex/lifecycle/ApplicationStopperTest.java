package pl.catchex.lifecycle;

import org.junit.jupiter.api.Test;
import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.reminder.TaskReminderService;
import pl.catchex.synchonizer.TaskRepositorySynchronizer;
import pl.catchex.tray.TrayService;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationStopperTest {

    @Test
    void stop_allComponentsStopped_noErrors() throws Exception {
        // given
        FileWatcher watcher = mock(FileWatcher.class);
        TaskRepositorySynchronizer sync = mock(TaskRepositorySynchronizer.class);
        TaskReminderService reminder = mock(TaskReminderService.class);
        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        TrayService tray = mock(TrayService.class);

        when(executor.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(true);

        ApplicationStopper stopper = new ApplicationStopper(watcher, sync, reminder, executor, tray);

        // when
        assertDoesNotThrow(() -> stopper.stop());

        // then
        verify(watcher, times(1)).removeListener(sync);
        verify(watcher, times(1)).stop();
        verify(reminder, times(1)).stop();
        verify(executor, times(1)).shutdown();
        verify(tray, times(1)).stop();
    }

    @Test
    void stop_watcherThrowsIOException_propagates() throws Exception {
        // given
        FileWatcher watcher = mock(FileWatcher.class);
        TaskRepositorySynchronizer sync = mock(TaskRepositorySynchronizer.class);
        TaskReminderService reminder = mock(TaskReminderService.class);
        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        TrayService tray = mock(TrayService.class);

        doThrow(new IOException("watcher fail")).when(watcher).stop();

        ApplicationStopper stopper = new ApplicationStopper(watcher, sync, reminder, executor, tray);

        // when / then
        IOException ex = assertThrows(IOException.class, () -> stopper.stop());
        assertEquals("watcher fail", ex.getMessage());

        // ensure we attempted to remove listener before stop
        verify(watcher, times(1)).removeListener(sync);
    }

    @Test
    void stop_withNullComponents_noNPE() {
        // given
        ApplicationStopper stopper = new ApplicationStopper(null, null, null, null, null);

        // when / then
        assertDoesNotThrow(() -> stopper.stop());
    }
}

