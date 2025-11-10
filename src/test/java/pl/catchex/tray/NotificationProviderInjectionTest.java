package pl.catchex.tray;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import pl.catchex.config.AppConfiguration;
import pl.catchex.config.reader.ReaderConfiguration;
import pl.catchex.config.reader.reminder.ReminderConfiguration;
import pl.catchex.di.AppModule;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationProviderInjectionTest {

    @Test
    void providerIsInjectableAndNotificationSenderIsMockable() {
        // create a mock NotificationSender
        NotificationSender mockSender = mock(NotificationSender.class);
        NotificationSenderFactory.Provider provider = new NotificationSenderFactory.Provider(mockSender, null);

        // build minimal AppConfiguration required by AppModule
        AppConfiguration cfg = new AppConfiguration();
        ReaderConfiguration rc = new ReaderConfiguration();
        rc.setReminder(new ReminderConfiguration());
        cfg.setConfiguration(rc);

        // create injector with custom provider
        Injector injector = Guice.createInjector(new AppModule(cfg, provider));

        // provider binding should return our provider instance
        NotificationSenderFactory.Provider injectedProvider = injector.getInstance(NotificationSenderFactory.Provider.class);
        assertSame(provider, injectedProvider);

        // NotificationSender should be the mock provided by our provider
        NotificationSender injectedSender = injector.getInstance(NotificationSender.class);
        assertNotNull(injectedSender);

        // call via injected sender and verify the mock is invoked
        injectedSender.send("title", "message", java.awt.TrayIcon.MessageType.INFO);
        verify(mockSender).send("title", "message", java.awt.TrayIcon.MessageType.INFO);
    }
}

