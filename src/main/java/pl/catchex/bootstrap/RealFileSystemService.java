package pl.catchex.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RealFileSystemService implements FileSystemService {

    @Override
    public Path getUserHome() {
        return Path.of(System.getProperty("user.home"));
    }

    @Override
    public boolean exists(Path path) throws IOException {
        return Files.exists(path);
    }

    @Override
    public void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public InputStream getResourceAsStream(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

    @Override
    public void copy(InputStream in, Path target) throws IOException {
        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void writeString(Path path, String content, Charset charset) throws IOException {
        Files.writeString(path, content, charset);
    }
}
