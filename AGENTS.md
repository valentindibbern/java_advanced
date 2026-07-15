# Repository-Richtlinien

## Projektzweck und Struktur

Dieses Repository enthält eine Java-Kommandozeilenimplementierung des klassischen Mastermind-Spiels, die als Iperka-Lernauftrag erstellt wird. Java-Quellcode gehört in `src/main/java/`, Tests in `src/test/java/` und Ressourcen in `src/main/resources/`. Bearbeite `.idea/` nur bei einer beabsichtigten Änderung der IDE-Konfiguration.

Das Programm erzeugt einen geheimen Code aus vier Farben aus sechs verfügbaren Farben; Wiederholungen sind erlaubt. Ein Spieler erhält höchstens sieben Tipps. Verfolge die Zustände laufend, gewonnen und verloren und zeige nach jedem Tipp eine Rückmeldung.

## Kompilieren, Testen und Ausführen

Es ist noch kein Maven- oder Gradle-Build vorhanden. Bis einer hinzugefügt wird, kompiliere und starte mit IntelliJ IDEA oder dem JDK:

```powershell
javac -d out src/Main/java/<package>/*.java
java -cp out <package>.Main
```

Wird Maven oder Gradle eingeführt, committe den jeweiligen Wrapper und verwende ihn als verbindlichen Build- und Testbefehl. Committe niemals generierte `out/`, `target/`, `build/` oder `.class`-Dateien.

## Java-Stil und Design

Verwende vier Leerzeichen für Einrückungen, öffnende geschweifte Klammern auf derselben Zeile, `PascalCase` für Klassen, `camelCase` für Methoden und Variablen sowie `UPPER_SNAKE_CASE` für Konstanten. Halte Klassen fokussiert: Trenne Spielablauf, Ein- und Ausgabe, Codeerzeugung und Rückmeldeauswertung. Setze Arrays bewusst ein; die Aufgabe bewertet die Fähigkeit, Java-Arrays einschliesslich zweidimensionaler Arrays zu erklären. Kommentiere nicht offensichtliche Logik, insbesondere die Rückmeldung bei doppelten Farben.

## Sprache

Trenne Code und Dokumentation sprachlich. Java-Quellcode, Tests, Bezeichner und Code-Kommentare werden auf Englisch geschrieben. Dokumentation und jeder andere Fliesstext, einschliesslich der Konsolenausgabe, werden auf Deutsch geschrieben. Verwende die Umlaute `ä`, `ö` und `ü` anstelle von `ae`, `oe` und `ue`; nutze die Schweizer Schreibweise mit `ss` statt eines scharfen s.

## Tests

Lege Unit-Tests mit gespiegelten Packages unter `src/test/java/` ab. Benenne Tests nach ihrem Verhalten, zum Beispiel `returnsBlackMarkForCorrectColourAndPosition`. Decke exakte Treffer, Farben an falschen Positionen, doppelte Farben, ungültige Eingaben, sieben fehlgeschlagene Tipps und einen Sieg ab. Die Zufallscodeerzeugung muss injizierbar oder seedbar sein, damit Tests deterministisch bleiben.

## Arbeitsweise

- Hole bei Unklarheiten, Abwägungen und wichtigen Zwischenschritten frühzeitig Rückmeldung oder Entscheidungen ein.
- Formuliere Antworten und Pläne strukturiert, präzise und mit ausreichender Begründung.
- Prüfe Änderungen vor der Übergabe sorgfältig, einschliesslich betroffener Anforderungen, Dokumentation und Tests.
- Kontrolliere Ergebnisse bei Bedarf mit geeigneten Tests, manuellen Szenarien oder einem Diff.

## Commits und Pull Requests

Es gibt noch keine Commit-Historie. Verwende kurze Betreffzeilen im Imperativ, beispielsweise `Add feedback evaluation` oder `Handle seventh failed guess`. Halte Commits eng abgegrenzt. Pull Requests müssen das geänderte Verhalten zusammenfassen, die relevante Anforderung benennen und die getesteten Befehle oder Szenarien auflisten. Screenshots sind für die Kommandozeilenversion nicht nötig.
