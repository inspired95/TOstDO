package pl.catchex.di;

import org.junit.jupiter.api.Test;
import pl.catchex.ApplicationAssembler;
import pl.catchex.config.AppConfiguration;
import pl.catchex.lifecycle.ApplicationStopper;
import pl.catchex.lifecycle.ApplicationStopperFactory;

import static org.mockito.Mockito.*;

class AppModuleDITest {

    @Test
    void di_injectsApplicationAssembler_withMockedStopperFactory() throws Exception {
        // given
        AppConfiguration cfg = new AppConfiguration();

        ApplicationStopperFactory mockFactory = mock(ApplicationStopperFactory.class);
        ApplicationStopper mockStopper = mock(ApplicationStopper.class);
        when(mockFactory.create(any(), any(), any(), any(), any())).thenReturn(mockStopper);

        ApplicationAssembler assembler = new ApplicationAssembler(cfg, null, null, mockFactory);

        // when
        assembler.stop();

        // then
        verify(mockFactory, times(1)).create(any(), any(), any(), any(), any());
        verify(mockStopper, times(1)).stop();
    }
}
