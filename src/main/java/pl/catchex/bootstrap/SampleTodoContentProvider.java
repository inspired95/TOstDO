package pl.catchex.bootstrap;

/**
 * Provides content for the sample todo.md file //NOSONAR
 */
public class SampleTodoContentProvider {

    public String getSampleContent() {
        return "# TOstDO - sample tasks\n\n" +
                "- [ ] Kup mleko [+++]\n" +
                "- [ ] Naprawić świat [++]\n" +
                "- [ ] Przykładowe zadanie ukończone [+]\n";
    }
}

