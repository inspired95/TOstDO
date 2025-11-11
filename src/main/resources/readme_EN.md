# TOstDO — Quick guide

TOstDO reminds you about tasks stored in the `todo.md` file.

Where to find the app files:
- The folder `\<user_folder>/.TOstDO/` contains: `config.yaml`, `todo.md`, and this `readme.md` file.

How to add tasks
- Add each task on a new line in the `todo.md` file.
- Use the Markdown checkbox format: `- [ ] Task description`.
- If you add a priority or a due date, the order must be exactly: task description -> `[PRIORITY]` -> `[DUE_DATE]`.

Required single-line task format:
- `- [ ] Task description [PRIORITY] [DUE_DATE]`

Examples
- Regular task:
    - `- [ ] Pay the bill`
- With priority (example of high priority):
    - `- [ ] Report a bug [+++]`
- With priority and due date (date format: `dd/MM/yyyy`):
    - `- [ ] Submit the report [++] [20/11/2025]`

Priorities
- Default priority symbols: `+`, `++`, `+++`.

About the configuration file
- In the folder `\<user_folder>/.TOstDO/` there is a file named `config.yaml`.
- This file contains basic application settings, such as priority symbols and the date format used for due dates (`dd/MM/yyyy`).
- You can edit `config.yaml` to adjust priority symbols or the date format; if you are not sure what to change, leave it as is.

A few tips
- Short, one-line task descriptions are the most readable.
- Keep the exact order of fields; otherwise a task may not be recognized correctly.

That's all — enjoy using TOstDO!