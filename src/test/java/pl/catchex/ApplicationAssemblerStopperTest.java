package pl.catchex;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.junit.jupiter.api.Test;
import pl.catchex.config.AppConfiguration;
import pl.catchex.lifecycle.ApplicationStopper;
import pl.catchex.lifecycle.ApplicationStopperFactory;
import pl.catchex.di.AppModule;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationAssemblerStopperTest {

    @Test
    void stop_delegatesToInjectedStopper() throws Exception {
        // given
        AppConfiguration cfg = new AppConfiguration();

        ApplicationStopperFactory mockFactory = mock(ApplicationStopperFactory.class);
        ApplicationStopper mockStopper = mock(ApplicationStopper.class);
        when(mockFactory.create(any(), any(), any(), any(), any())).thenReturn(mockStopper);

        Injector injector = Guice.createInjector(
                Modules.override(new AppModule(cfg)).with(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ApplicationStopperFactory.class).toInstance(mockFactory);
                    }
                })
        );

        ApplicationAssembler assembler = injector.getInstance(ApplicationAssembler.class);

        // when
        assembler.stop();

        // then
        ApplicationStopperFactory factory = injector.getInstance(ApplicationStopperFactory.class);
        verify(factory, times(1)).create(any(), any(), any(), any(), any());
        verify(mockStopper, times(1)).stop();
    }

    @Test
    void stop_propagatesIOExceptionFromInjectedStopper() throws Exception {
        // given
        AppConfiguration cfg = new AppConfiguration();

        ApplicationStopperFactory mockFactory = mock(ApplicationStopperFactory.class);
        ApplicationStopper mockStopper = mock(ApplicationStopper.class);
        doThrow(new IOException("boom")).when(mockStopper).stop();
        when(mockFactory.create(any(), any(), any(), any(), any())).thenReturn(mockStopper);

        Injector injector = Guice.createInjector(
                Modules.override(new AppModule(cfg)).with(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ApplicationStopperFactory.class).toInstance(mockFactory);
                    }
                })
        );

        ApplicationAssembler assembler = injector.getInstance(ApplicationAssembler.class);

        // when / then
        IOException ex = assertThrows(IOException.class, assembler::stop);
        assertEquals("boom", ex.getMessage());

        ApplicationStopperFactory factory = injector.getInstance(ApplicationStopperFactory.class);
        verify(factory, times(1)).create(any(), any(), any(), any(), any());
        verify(mockStopper, times(1)).stop();
    }
}
