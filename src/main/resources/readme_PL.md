# TOstDO — szybki przewodnik

TOstDO przypomina o zadaniach zapisanych w pliku `todo.md`.

Gdzie znaleźć pliki aplikacji:
- Folder `\<katalog_użytkownika>/.TOstDO/` zawiera: `config.yaml`, `todo.md` oraz ten plik `readme.md`.

Jak dodawać zadania
- Każde zadanie wpisz w nowej linii w pliku `todo.md`.
- Używaj formatu Markdown checkbox: `- [ ] Opis zadania`.
- Jeśli dodajesz priorytet lub termin wykonania, kolejność musi być dokładnie taka: opis zadania -> `[PRIORYTET]` -> `[DUE_DATE]`.

Wymagany format pojedynczej linii zadania:
- `- [ ] Opis zadania [PRIORYTET] [DUE_DATE]`

Przykłady
- Zwykłe zadanie:
    - `- [ ] Opłać rachunek`
- Z priorytetem (przykład priorytetu wysokiego):
    - `- [ ] Zgłoś błąd [+++]`
- Z priorytetem i terminem (format daty: `dd/MM/yyyy`):
    - `- [ ] Prześlij raport [++] [20/11/2025]`

Priorytety
- Domyślne symbole priorytetów: `+`, `++`, `+++`.

O pliku konfiguracyjnym
- W folderze `\<katalog_użytkownika>/.TOstDO/` znajduje się plik `config.yaml`.
- Plik ten zawiera podstawowe ustawienia aplikacji, takie jak symbole priorytetów oraz format daty używany przy terminach (`dd/MM/yyyy`).
- Możesz edytować `config.yaml`, aby zmienić symbole priorytetów lub format daty; jeśli nie jesteś pewien/pewna, co zmienić, pozostaw plik bez zmian.

Kilka wskazówek
- Krótkie, jednozdaniowe opisy zadań są najbardziej czytelne.
- Zachowaj dokładną kolejność pól; w przeciwnym razie zadanie może nie zostać poprawnie rozpoznane.

To wszystko — miłego korzystania z TOstDO!

