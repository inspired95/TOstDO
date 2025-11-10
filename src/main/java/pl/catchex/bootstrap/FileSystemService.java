package pl.catchex.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

public interface FileSystemService {
    Path getUserHome();
    boolean exists(Path path) throws IOException;
    void createDirectories(Path path) throws IOException;
    InputStream getResourceAsStream(String resourceName);
    void copy(InputStream in, Path target) throws IOException;
    void writeString(Path path, String content, Charset charset) throws IOException;
}

