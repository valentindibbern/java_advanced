# Mastermind

Ein Java-Kommandozeilenprogramm für klassisches Mastermind. Der Spieler errät einen geheimen Code aus vier Farben innerhalb von höchstens sieben gültigen Versuchen.

## Voraussetzungen

- JDK 25, getestet mit Oracle JDK 25.0.3.
- Windows Terminal oder eine andere UTF-8- und ANSI-fähige Konsole für die farbige Darstellung.
- IntelliJ IDEA zum Ausführen der JUnit-Tests.

## Kompilieren und starten

In PowerShell im Projektordner:

~~~powershell
$jdk = 'C:\Program Files\Java\jdk-25.0.3'
$sources = Get-ChildItem -Recurse -Filter *.java src/main/java | ForEach-Object FullName
& "$jdk\bin\javac.exe" -encoding UTF-8 -d out $sources
& "$jdk\bin\java.exe" -cp out Main
~~~

`out/` enthält nur generierte Klassendateien und wird nicht versioniert.

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

## Tests

Die Tests liegen unter `src/test/java/` und verwenden JUnit 5. In IntelliJ die Testklasse oder den Ordner `src/test/java` ausführen. Die Datei `.idea/java_adanced.iml` markiert diesen Ordner als Test Source Root und verwendet `junit-platform-console-standalone` 1.12.2 als Testbibliothek.

Falls die JUnit-Bibliothek lokal noch nicht vorhanden ist, kann sie vor dem Testlauf mit diesem PowerShell-Befehl in das lokale Maven-Repository geladen werden:

~~~powershell
$version = '1.12.2'
$directory = "$HOME\.m2\repository\org\junit\platform\junit-platform-console-standalone\$version"
New-Item -ItemType Directory -Force -Path $directory | Out-Null
Invoke-WebRequest "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$version/junit-platform-console-standalone-$version.jar" -OutFile "$directory\junit-platform-console-standalone-$version.jar"
~~~

Die Produktionsanwendung benötigt keine externe Bibliothek und keinen Build-Manager.
