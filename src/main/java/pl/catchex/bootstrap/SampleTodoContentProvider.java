package pl.catchex.bootstrap;

/**
 * Provides content for the sample todo.md file //NOSONAR
 */
public class SampleTodoContentProvider {

    public String getSampleContent() {
        return """
                # TOstDO - sample tasks

                - [ ] Kup mleko [+++]
                - [ ] Naprawić świat [++]
                - [ ] Przykładowe zadanie ukończone [+]
                """;
    }
}
