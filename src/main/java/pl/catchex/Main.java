package pl.catchex;

import pl.catchex.config.Configuration;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.ToDoReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
        String path = "C:\\Age\\TODO.md";
        List<ToDoItem> toDoItems = new ToDoReader(configuration).read(Paths.get(path));
        toDoItems.forEach(System.out::println);

    }
}
