# ANSI-Farbausgabe in der Java-Konsolenanwendung

Diese Dokumentation beschreibt die Verwendung von ANSI-Escape-Sequenzen für die farbige Ausgabe des Mastermind-Spiels. ANSI-Farben sind **keine Java-API** und brauchen deshalb keine zusätzliche Bibliothek: Java gibt Zeichenketten über `System.out` aus; ein kompatibles Terminal interpretiert die darin enthaltenen Steuersequenzen und stellt die Farben dar.

Die Technik eignet sich für die kleinen, klar abgegrenzten Ausgaben dieses Projekts: Farbnamen und farbige Spielsteine, Erfolgsmeldungen, Fehlermeldungen sowie die Rückmeldung nach einem Tipp. Sie darf die Spielregeln nie allein vermitteln. Insbesondere sollen die Markierungen für exakte und nur farblich passende Treffer zusätzlich als Text oder mit unterschiedlichen Zeichen erkennbar bleiben.

## Inhaltsverzeichnis

- [Grundprinzip](#grundprinzip)
- [Aufbau einer SGR-Sequenz](#aufbau-einer-sgr-sequenz)
- [Farbcodes](#farbcodes)
- [Voraussetzungen und Konfiguration](#voraussetzungen-und-konfiguration)
- [Minimale Java-Verwendung](#minimale-java-verwendung)
- [Empfohlene Hilfsklasse](#empfohlene-hilfsklasse)
- [Umlaute und UTF-8](#umlaute-und-utf-8)
- [Mastermind-Beispiele](#mastermind-beispiele)
- [Kompatibilität](#kompatibilität)
- [Barrierefreiheit und robuste Ausgabe](#barrierefreiheit-und-robuste-ausgabe)
- [Testen](#testen)
- [Weiterführende Links](#weiterführende-links)

## Grundprinzip

Eine Escape-Sequenz beginnt mit dem Escape-Zeichen `ESC` (Unicode `U+001B`). In einer Java-Zeichenkette wird es am lesbarsten als `\u001B` geschrieben. Darauf folgt bei Farben `[` und eine **SGR**-Anweisung (*Select Graphic Rendition*).

```java
System.out.println("\u001B[31mRot\u001B[0m");
```

Das Terminal empfängt hierbei:

```text
ESC [ 31 m Rot ESC [ 0 m
```

`31` schaltet die Vordergrundfarbe auf Rot. `0` setzt danach alle Darstellungsattribute zurück. Ohne diesen Reset kann die gewählte Farbe in nachfolgende Ausgaben «auslaufen».

> Wichtig: ANSI-Sequenzen färben nur die Darstellung im Terminal. Eine in eine Datei umgeleitete Ausgabe enthält die Steuerzeichen weiterhin. Für gespeicherte Spielstände, Logdateien und Tests sollte daher eine farbfreie Ausgabe verfügbar sein.

## Aufbau einer SGR-Sequenz

Die allgemeine Form lautet `ESC[Parameter;Parameter...m`, in Java also `"\u001B[...m"`. Mehrere Attribute lassen sich mit Semikolon kombinieren.

```java
String greenBold = "\u001B[1;32m";
String reset = "\u001B[0m";
System.out.println(greenBold + "Gewonnen!" + reset);
```

| Sequenz | Bedeutung |
| --- | --- |
| `\u001B[0m` | Alle SGR-Attribute zurücksetzen |
| `\u001B[1m` | Erhöhte Intensität (häufig fett; terminalabhängig) |
| `\u001B[2m` | Verminderte Intensität (terminalabhängig) |
| `\u001B[3m` | Kursiv, falls unterstützt |
| `\u001B[4m` | Unterstrichen |
| `\u001B[7m` | Vorder- und Hintergrund tauschen |
| `\u001B[22m` | Intensität zurücksetzen |
| `\u001B[24m` | Unterstreichung zurücksetzen |
| `\u001B[27m` | Umkehrung zurücksetzen |
| `\u001B[39m` | Standard-Vordergrundfarbe wiederherstellen |
| `\u001B[49m` | Standard-Hintergrundfarbe wiederherstellen |

Für das Spiel sind `0`, die Vordergrundfarben und bei Bedarf `1` ausreichend. Stilattribute wie Kursiv sollten nicht Teil der Spiellogik sein, weil nicht jedes Terminal sie gleich darstellt.

## Farbcodes

### Standard-Vordergrundfarben

| Farbe | Code | Java-Konstante als Sequenz |
| --- | ---: | --- |
| Schwarz | 30 | `"\u001B[30m"` |
| Rot | 31 | `"\u001B[31m"` |
| Grün | 32 | `"\u001B[32m"` |
| Gelb | 33 | `"\u001B[33m"` |
| Blau | 34 | `"\u001B[34m"` |
| Magenta | 35 | `"\u001B[35m"` |
| Cyan | 36 | `"\u001B[36m"` |
| Weiss | 37 | `"\u001B[37m"` |

Die jeweiligen Hintergrundfarben verwenden `40` bis `47` statt `30` bis `37`, beispielsweise `"\u001B[44m"` für einen blauen Hintergrund. Helle Varianten verwenden für den Vordergrund `90` bis `97` und für den Hintergrund `100` bis `107`.

| Farbe | Heller Vordergrund | Heller Hintergrund |
| --- | ---: | ---: |
| Schwarz/Grau | 90 | 100 |
| Rot | 91 | 101 |
| Grün | 92 | 102 |
| Gelb | 93 | 103 |
| Blau | 94 | 104 |
| Magenta | 95 | 105 |
| Cyan | 96 | 106 |
| Weiss | 97 | 107 |

Schwarz und Weiss sind als feste Spielfarben ungeeignet: Je nach hellem oder dunklem Terminalthema sind sie schlecht lesbar. Für die sechs Mastermind-Farben sind Rot, Grün, Gelb, Blau, Magenta und Cyan eine gute Zuordnung. Gelb auf weissem Hintergrund kann allerdings zu wenig Kontrast haben; ein Symbol und der ausgeschriebene Farbname bleiben deshalb wichtig.

### 256 Farben und RGB

Viele Terminals verstehen zusätzlich 256-Farben- und RGB-Sequenzen:

```java
String orange256 = "\u001B[38;5;208m";
String customRgb = "\u001B[38;2;255;128;0m";
String customBackground = "\u001B[48;2;20;20;20m";
```

`38` setzt eine Vordergrund- und `48` eine Hintergrundfarbe; `5;n` wählt eine 256-Farben-Palette, `2;r;g;b` eine RGB-Farbe. Diese Varianten sind nicht nötig und für dieses Lernprojekt nicht empfohlen, weil die klassische 16-Farben-Palette breiter unterstützt wird.

## Voraussetzungen und Konfiguration

Für ANSI-Farben ist keine Java-Bibliothek und keine besondere Java-Option erforderlich. Vorausgesetzt wird jedoch ein Terminal, das virtuelle Terminalsequenzen interpretiert. Für dieses Windows-Projekt ist **Windows Terminal** mit PowerShell die empfohlene Zielumgebung. Unter Windows 10 und neuer unterstützen sowohl Windows Terminal als auch der aktuelle Windows Console Host ANSI-Sequenzen. In PowerShell lässt sich die Unterstützung des aktuellen Hosts prüfen:

```powershell
$Host.UI.SupportsVirtualTerminal
```

Das Ergebnis soll `True` sein. Ist es `False`, werden die Escape-Sequenzen nicht zuverlässig als Farben dargestellt. Dann wird das Programm trotzdem funktional ausgeführt, soll die Farben aber zentral deaktivieren und nur die deutschen Texte ausgeben. Die Programmausgabe darf nie von Farbe allein abhängen.

Für die Entwicklung gelten diese Einstellungen:

1. Die Quelldateien im Editor und in IntelliJ IDEA als **UTF-8** speichern. In IntelliJ IDEA wird dies unter *Settings > Editor > File Encodings* eingestellt.
2. Die Anwendung im Windows Terminal manuell testen; das IntelliJ-Run-Fenster ist kein Ersatz für diesen Test, weil dessen Unterstützung von Escape-Sequenzen von Version und Einstellung abhängt.
3. Bei einer älteren klassischen Windows-Konsole vor dem Start einmal `chcp 65001` ausführen. Damit wird die aktive Konsolencodepage auf UTF-8 gesetzt. Windows Terminal benötigt diesen Schritt normalerweise nicht.
4. Bei direktem Kompilieren die Quellcodierung immer explizit angeben. So hängt das Ergebnis nicht von der Windows-Standardcodepage ab:

```powershell
javac -encoding UTF-8 -d out src/main/java/<package>/*.java
java -Dfile.encoding=UTF-8 -cp out <package>.Main
```

`javac -encoding UTF-8` ist besonders bei JDK 17 und älter wichtig, weil `javac` ohne diese Option die plattformabhängige Standardcodierung verwenden kann. Die Laufzeitoption macht die Java-Standardzeichencodierung explizit. Bei modernen JDK-Versionen, deren Standard bereits UTF-8 ist, bleibt sie eine nachvollziehbare Absicherung.

## Grundlage im Code

ANSI gehört ausschliesslich in die Ausgabeschicht. Die Spielklassen arbeiten mit `Color`-Werten, Rückmeldungen und deutschen Texten, aber weder mit Escape-Sequenzen noch mit Terminaleinstellungen. Eine zentrale Klasse `Ansi` kapselt die Sequenzen; `ConsoleUI` ruft sie auf. Dadurch lassen sich Farben später ohne Änderung der Spielregeln abschalten.

Die kleinste sinnvolle Grundlage besteht aus einer Reset-Konstante, den benötigten Vordergrundfarben und einer Methode zum sicheren Umschliessen von Text:

```java
public final class Ansi {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";

    private Ansi() {
    }

    public static String colour(String colour, String text) {
        return colour + text + RESET;
    }
}
```

`\u001B` ist gegenüber einem direkt eingefügten Steuerzeichen vorzuziehen: Es ist im Quellcode sichtbar, rein ASCII und unabhängig von der Dateicodierung. Jede gefärbte Ausgabe muss durch `RESET` abgeschlossen werden. Die `colour`-Methode soll später eine farbfreie Zeichenkette liefern können, wenn ANSI deaktiviert ist oder die Ausgabe umgeleitet wird.

## Minimale Java-Verwendung

Für einen einzelnen, kurzen Hinweis genügt eine lokale Konstante:

```java
String red = "\u001B[31m";
String reset = "\u001B[0m";

System.out.println(red + "Ungültige Eingabe." + reset);
```

Vermeide hartcodierte Zahlen im Spielablauf. Die Bedeutung einer Farbe soll an einer zentralen Stelle definiert sein. Es ist auch besser, nicht das gesamte Terminal mit einer Farbe zu konfigurieren, sondern jede gefärbte Textstelle direkt mit `RESET` abzuschliessen.

## Empfohlene Hilfsklasse

Die folgende kleine Klasse ist für `src/main/java/<package>/Ansi.java` vorgesehen. `<package>` muss durch den tatsächlichen Paketnamen ersetzt werden. Sie enthält keine Spiellogik und benötigt keine externe Abhängigkeit.

```java
public final class Ansi {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";

    private Ansi() {
    }

    public static String colour(String colour, String text) {
        return colour + text + RESET;
    }
}
```

Anwendung:

```java
System.out.println(Ansi.colour(Ansi.GREEN + Ansi.BOLD, "Gewonnen!"));
System.out.println(Ansi.colour(Ansi.RED, "Ungültige Eingabe."));
```

Die Methode heisst bewusst `colour`, weil sie eine Darstellung umschliesst, nicht eine Farbe validiert oder den Mastermind-Code auswertet. Die Klasse soll bei einer späteren Option für farbfreie Ausgabe erweitert werden können, ohne die Spiellogik anzupassen.

## Umlaute und UTF-8

Die deutschen Konsolentexte werden mit echten Umlauten geschrieben, also beispielsweise `Ungültige Eingabe`, `Grün`, `weisse Marken` und `verfügbar` – nie mit `ae`, `oe` oder `ue`. Die Zeichen `ä`, `ö` und `ü` sind normale Unicode-Zeichen; ANSI-Farbsequenzen verändern ihre Codierung nicht. Probleme wie `GrÃ¼n` entstehen nur, wenn Quelldatei, Java-Prozess und Terminal unterschiedliche Zeichencodierungen verwenden.

Die verbindliche Kette für dieses Projekt lautet daher: Java-Quellcode und Markdown-Dateien in UTF-8 speichern, mit `javac -encoding UTF-8` kompilieren und in einem UTF-8-fähigen Terminal ausführen. Für die Ausgabe genügt dann `System.out.println("Ungültige Eingabe.")`; für Eingaben verarbeitet `Scanner` die vom Terminal gelieferten Zeichen. Falls das Programm später Farbnamen statt Nummern akzeptiert, müssen Eingaben wie `Grün` ebenfalls als UTF-8 getestet werden.

Zur Diagnose einer fehlerhaften Umgebung kann die tatsächlich verwendete Java-Standardcodierung angezeigt werden:

```java
System.out.println(java.nio.charset.Charset.defaultCharset());
```

Das erwartete Ergebnis ist `UTF-8`. Diese Diagnose gehört nicht in den normalen Spielablauf, sondern ist nur beim Einrichten oder bei fehlerhaft dargestellten Umlauten nützlich.

## Mastermind-Beispiele

### Farbige Spielsteine, aber verständlich ohne Farbe

Ein einzelnes farbiges Zeichen ist für Menschen mit eingeschränktem Farbsehen, für Ausdrucke und für manche Terminals nicht ausreichend. Ein Zeichen zusammen mit dem Farbnamen ist robust:

```java
System.out.println(Ansi.colour(Ansi.RED, "● Rot"));
System.out.println(Ansi.colour(Ansi.GREEN, "● Grün"));
System.out.println(Ansi.colour(Ansi.YELLOW, "● Gelb"));
```

Für die kompakte Darstellung eines Tipps kann die Farbe den Buchstaben ergänzen:

```java
String guess = Ansi.colour(Ansi.RED, "R")
        + Ansi.colour(Ansi.BLUE, "B")
        + Ansi.colour(Ansi.GREEN, "G")
        + Ansi.colour(Ansi.YELLOW, "Y");
System.out.println("Tipp: " + guess + "  (R=Rot, B=Blau, G=Grün, Y=Gelb)");
```

### Rückmeldung

Die Rückmeldung sollte ihre Bedeutung ausschreiben. Beispielsweise kann ein exakter Treffer grün und eine korrekte Farbe an falscher Position gelb dargestellt werden:

```java
System.out.println(Ansi.colour(Ansi.GREEN, "● 2 exakt richtig"));
System.out.println(Ansi.colour(Ansi.YELLOW, "○ 1 Farbe richtig, Position falsch"));
```

Die Zeichen `●` und `○` unterscheiden die beiden Fälle auch ohne Farbe. Die Farben sind ausschliesslich eine zusätzliche visuelle Hilfe und dürfen die Auswertung nicht beeinflussen.

### Spielzustände

```java
System.out.println(Ansi.colour(Ansi.GREEN + Ansi.BOLD, "Gewonnen!"));
System.out.println(Ansi.colour(Ansi.RED + Ansi.BOLD, "Verloren. Der Code war: R-B-G-Y."));
System.out.println(Ansi.colour(Ansi.YELLOW, "Noch 3 von 7 Tipps verfügbar."));
```

## Kompatibilität

ANSI-Sequenzen werden von einem Terminal interpretiert, nicht von Java. `System.out` schreibt die Zeichen lediglich in den Standardausgabestrom; [`PrintStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/PrintStream.html) bietet dafür unter anderem `print`, `println` und `printf`.

- **Windows Terminal und aktuelle PowerShell:** Unterstützen virtuelle Terminalsequenzen normalerweise direkt.
- **Klassische Windows-Konsole:** Die Unterstützung hängt von Windows-Version und Konsolenkonfiguration ab. Microsoft dokumentiert virtuelle Terminalsequenzen und den Modus `ENABLE_VIRTUAL_TERMINAL_PROCESSING` in der [Windows-Konsolenreferenz](https://learn.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences). Java aktiviert diesen Windows-Konsolenmodus nicht als eigene Standard-Java-API.
- **IntelliJ IDEA:** Die Ausgabe wird im Run-Fenster angezeigt. Ob Escape-Sequenzen als Farben statt als Text sichtbar werden, hängt von dessen Terminal-/Konsolenunterstützung und Einstellungen ab. Daher immer auch im vorgesehenen Zielterminal testen.
- **Umleitung und CI:** Bei `java ... > ausgabe.txt` sowie in manchen Test- oder CI-Umgebungen sind die Steuerzeichen unerwünscht oder werden sichtbar. Hier sollte Farbe abschaltbar sein.

Wenn die Zielumgebung ANSI nicht interpretiert, ist die korrekte Rückfallebene: die gleichen deutschen Texte ohne Escape-Sequenzen ausgeben. Eine externe Bibliothek wie Jansi kann Kompatibilitätsarbeit übernehmen, ist für dieses kleine Projekt aber nur sinnvoll, wenn die getestete Zielumgebung sie tatsächlich benötigt.

## Barrierefreiheit und robuste Ausgabe

- Verwende Farbe nie als einzigen Informationsträger. Ergänze sie durch Wörter, Buchstaben oder unterschiedliche Symbole.
- Setze nach **jeder** gefärbten Stelle `RESET`. Das verhindert, dass Eingabeaufforderungen oder Fehlertexte unbeabsichtigt weiter gefärbt bleiben.
- Gib in der Eingabeaufforderung die erlaubten Farben als Text an, zum Beispiel `Rot, Grün, Gelb, Blau, Magenta, Cyan`.
- Nutze keine Kombinationen mit schwachem Kontrast. Besonders Gelb auf hellem Hintergrund und Blau auf dunklem Hintergrund können schwer lesbar sein.
- Verwende keine blinkenden Attribute (`5` oder `6`); sie stören und werden häufig ignoriert.
- Trenne Darstellung und Spielmodell: Das `enum` oder Array mit den Spielfarben gehört zur Spiellogik, die ANSI-Zeichenketten zur Ausgabeschicht.

Eine einfache Schaltmöglichkeit lässt sich an der Ausgabeschicht abbilden:

```java
public final class Ansi {
    private static final boolean ENABLED = true;
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";

    private Ansi() {
    }

    public static String colour(String colour, String text) {
        return ENABLED ? colour + text + RESET : text;
    }
}
```

Die Entscheidung, ob `ENABLED` über eine Programmeinstellung, ein Kommandozeilenargument oder eine Umgebungsvariable gesetzt wird, kann später getroffen werden. Wichtig ist, dass sich die Ausgabe zentral und ohne Änderungen an der Auswertung deaktivieren lässt.

## Testen

Die farbliche Darstellung selbst ist eine Eigenschaft des Terminals; Unit-Tests sollten deshalb nicht prüfen, ob auf einem Bildschirm tatsächlich Rot erscheint. Prüfe stattdessen die erzeugten Zeichenketten oder schalte Farben in Tests aus.

```java
@Test
void wrapsTextWithColourAndReset() {
    String result = Ansi.colour(Ansi.RED, "Fehler");

    assertEquals("\u001B[31mFehler\u001B[0m", result);
}
```

Zusätzlich manuell prüfen:

1. Anwendung im vorgesehenen Terminal starten.
2. Einen ungültigen Tipp eingeben und kontrollieren, dass die Fehlermeldung farbig, die nächste Eingabeaufforderung aber wieder normal dargestellt wird.
3. Gewinn, Verlust und beide Arten der Rückmeldung kontrollieren.
4. Die Ausgabe in eine Datei umleiten oder Farben deaktivieren und kontrollieren, dass der Text weiterhin verständlich bleibt.

## Weiterführende Links

- [ECMA-48: Control Functions for Coded Character Sets](https://ecma-international.org/publications-and-standards/standards/ecma-48/) – der Standard, aus dem die heute verwendeten Steuerfunktionen stammen.
- [ANSI escape code (Überblick und SGR-Tabellen)](https://en.wikipedia.org/wiki/ANSI_escape_code) – ausführlicher Überblick mit Farbcodetabellen.
- [Oracle Java API: `PrintStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/PrintStream.html) – Standardausgabe mit `System.out`.
- [Oracle: `javac`-Option `-encoding`](https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html) – explizite Zeichenkodierung beim Kompilieren.
- [Oracle: unterstützte Zeichencodierungen](https://docs.oracle.com/en/java/javase/25/intl/supported-encodings.html) – Standardzeichencodierung und Unterschiede zu JDK 17 und älter.
- [Microsoft: Console Virtual Terminal Sequences](https://learn.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences) – Verhalten und Aktivierung virtueller Terminalsequenzen unter Windows.
- [Microsoft: ANSI-Terminals in PowerShell](https://learn.microsoft.com/en-us/powershell/module/microsoft.powershell.core/about/about_ansi_terminals) – Unterstützung und Prüfung virtueller Terminalsequenzen.
- [Jansi auf GitHub](https://github.com/fusesource/jansi) – optionale Java-Bibliothek für konsolenübergreifende ANSI-Unterstützung.
