package pl.catchex.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import pl.catchex.bootstrap.AppDirectoryInitializer;
import pl.catchex.bootstrap.ConfigCreator;
import pl.catchex.bootstrap.DefaultConfigCreatorImpl;
import pl.catchex.bootstrap.FileSystemService;
import pl.catchex.bootstrap.RealFileSystemService;
import pl.catchex.bootstrap.SampleTodoContentProvider;
import pl.catchex.bootstrap.DefaultPathProvider;

public class BootstrapModule extends AbstractModule {

    @Override
    protected void configure() {
        // no-op
    }

    @Provides
    @Singleton
    public FileSystemService provideFileSystemService() {
        return new RealFileSystemService();
    }

    @Provides
    @Singleton
    public pl.catchex.bootstrap.PathProvider providePathProvider(FileSystemService fs) {
        return new DefaultPathProvider(fs);
    }

    @Provides
    @Singleton
    public ConfigCreator provideConfigCreator(FileSystemService fs) {
        return new DefaultConfigCreatorImpl(fs);
    }

    @Provides
    @Singleton
    public AppDirectoryInitializer provideAppDirectoryInitializer(FileSystemService fs, ConfigCreator configCreator) {
        return new AppDirectoryInitializer(fs, new SampleTodoContentProvider(), configCreator);
    }
}
