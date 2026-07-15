# Mastermind

Eine Java-Anwendung für klassisches Mastermind mit Konsolenmodus und optionaler Swing-GUI. Der Spieler errät einen geheimen Code aus vier Farben innerhalb von höchstens sieben gültigen Versuchen.

## Voraussetzungen

- JDK 21 oder neuer.
- Windows Terminal oder eine andere UTF-8- und ANSI-fähige Konsole für die farbige Darstellung.
- Kein global installiertes Maven nötig; der Maven Wrapper ist Teil des Projekts.
- Für die GUI sind keine zusätzlichen Bibliotheken nötig; Swing ist Teil des JDK.

## Kompilieren, testen und starten

In PowerShell im Projektordner:

~~~powershell
.\mvnw.cmd test
.\mvnw.cmd package
java -jar target\mastermind.jar
~~~

Die grafische Oberfläche starten:

~~~powershell
java -jar target\mastermind.jar --gui
~~~

Farbige ANSI-Ausgabe ist standardmässig aktiv. Für farblose Ausgabe:

~~~powershell
java -jar target\mastermind.jar --no-color
~~~

Alternativ kann die Umgebungsvariable `NO_COLOR` gesetzt werden:

~~~powershell
$env:NO_COLOR = '1'
java -jar target\mastermind.jar
~~~

`--no-color` und `NO_COLOR` betreffen nur die Konsolenausgabe. Mit `--gui` wird immer die grafische Oberfläche gestartet.

`target/mastermind.jar` ist das ausführbare JAR. `target/` enthält generierte Build-Dateien und wird nicht versioniert.

## Projektstruktur

Der Produktionscode liegt im Package `ch.valentindibbern.mastermind` unter `src/main/java/ch/valentindibbern/mastermind/`. Die Tests spiegeln dieses Package unter `src/test/java/ch/valentindibbern/mastermind/`.

## Bedienung

Die Farblegende verwendet diese Nummern:

| Nummer | Farbe |
| ---: | --- |
| 0 | Rot |
| 1 | Grün |
| 2 | Blau |
| 3 | Gelb |
| 4 | Orange |
| 5 | Violett |

Ein Tipp besteht aus vier durch Leerzeichen getrennten Nummern, beispielsweise `0 3 3 5`. Ungültige Eingaben zählen nicht als Versuch.

Nach jedem gültigen Tipp bedeutet `Schwarz` eine richtige Farbe an der richtigen Position. `Weiss` bedeutet eine zusätzliche richtige Farbe an einer falschen Position. Bei mehrfachen Farben wird jede Stelle höchstens einmal bewertet.

Bei einem Volltreffer gewinnt der Spieler sofort. Nach dem siebten nicht gewinnenden Tipp verliert er. Anschliessend zeigt das Programm den Geheimcode und fragt nach einer neuen Runde (`j` oder `n`).

## GUI-Bedienung

Der GUI-Modus zeigt ein klassisches Brett mit sieben Versuchzeilen. Wähle die vier Farben eines Tipps über die beschriftete Farbpalette; Wiederholungen sind erlaubt. `Letzte Farbe löschen` entfernt die letzte Wahl. `Tipp prüfen` wird erst nach vier gewählten Farben aktiv. Die Rückmeldung zeigt pro Zug zuerst schwarze und danach weisse Marken. Die Rückmeldungsfläche ist hellgrau hinterlegt; weisse Marken haben eine kräftige dunkle Umrandung, damit sie gut vom Hintergrund unterscheidbar sind. Nach Sieg oder Niederlage wird der Geheimcode aufgedeckt und ein Dialog bietet eine neue Runde an.

## Tests

Die Tests verwenden JUnit 5 und werden über den Maven Wrapper ausgeführt:

~~~powershell
.\mvnw.cmd test
~~~

Der Testlauf prüft Fachlogik, Duplikatbewertung, Zustandswechsel, defensive Kopien, Konsoleneingaben, GUI-Farbwahl, Rückmeldedarstellung, Neustartverhalten sowie farbige und farblose Ausgabe.
