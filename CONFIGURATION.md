# Application configuration — TOstDO

This document describes the format and example content of the application configuration used by TOstDO.

By default the configuration file is named `config.yaml` and is located in the application directory (for example `~/.TOstDO/config.yaml`). The application may create a default config during the first run if none exists.

## YAML structure
The configuration maps to Java POJOs as follows:
- `AppConfiguration` -> property `configuration` of type `ReaderConfiguration`.
- `ReaderConfiguration` contains:
  - `toDoFilePath` (string) — path to the todo file.
  - `todoItem` — a `TodoItemConfiguration` object describing date format and priority symbols.
  - `reminder` — a `ReminderConfiguration` object describing reminder behavior.

Minimal example `configuration.yaml`:

```yaml
configuration:
  todoFilePath: 'C:\\Users\\alice\\.TOstDO\\todo.md'
  todoItem:
    dateFormat: 'yyyy-MM-dd'
    priority:
      symbol:
        low: '[LOW]'
        medium: '[MEDIUM]'
        high: '[HIGH]'
  reminder:
    periodFactor:
      minutes: 15
    periodThreshold:
      minutes: 60
```

> Note: The default config generator quotes the `todoFilePath` value using single quotes when writing YAML.

## Field descriptions
- `configuration.todoFilePath` (string)
  - Path to the `todo.md` file that stores tasks. Accepts absolute or relative paths. If absent, the app will default to `~/.TOstDO/todo.md`.

- `configuration.todoItem.dateFormat` (string)
  - The date format used in the todo file. Use Java `DateTimeFormatter` patterns (e.g. `yyyy-MM-dd`).

- `configuration.todoItem.priority.symbol.low|medium|high` (string)
  - Symbols used to mark priority levels in the todo file; the parser maps these symbols to priority enum values.

- `configuration.reminder.periodFactor` and `configuration.reminder.periodThreshold`
  - Numeric units defining reminder scheduling behavior (for example `minutes: 15`). See code in `ReminderConfiguration` for exact mapping.

## Default creation and location
- On first run, `AppDirectoryInitializer` ensures the application directory exists (usually `~/.TOstDO`) and calls `ConfigCreator` to copy and adapt the default configuration from classpath resources (if a bundled `configuration.yaml` exists).
- If you prefer a custom config location, place a `config.yaml` file into the application directory or implement a `ConfigSource` that reads from your custom location.

## Validation and errors
- The app uses SnakeYAML to map YAML to POJOs. If the file is malformed or types mismatch, the loader logs a warning and returns an empty configuration (the application will behave in degraded mode where necessary).

## Common scenarios
1. Missing config file: app writes default `config.yaml` (if resource available) and creates sample `todo.md`.
2. Missing `todoFilePath`: app uses default `~/.TOstDO/todo.md`.

---

Update `CONFIGURATION.md` if you change config POJOs. Consider adding schema/JSON Schema if you want automated validation tooling.
