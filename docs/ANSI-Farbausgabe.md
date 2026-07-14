# ANSI-Farbausgabe in der Java-Konsolenanwendung

Diese Dokumentation beschreibt die Verwendung von ANSI-Escape-Sequenzen für die farbige Ausgabe des Mastermind-Spiels. ANSI-Farben sind keine Java-API und brauchen deshalb keine zusätzliche Bibliothek: Java gibt Zeichenketten über System.out aus; ein kompatibles Terminal interpretiert die darin enthaltenen Steuersequenzen.

Die Technik eignet sich für Farbnamen und farbige Spielsteine. Sie darf die Spielregeln nie allein vermitteln. Insbesondere bleiben Rückmeldungen zusätzlich durch deutsche Texte, Zahlen und Symbole verständlich.

## Inhaltsverzeichnis

- Grundprinzip
- Aufbau einer SGR-Sequenz
- Farbcodes
- Voraussetzungen und Konfiguration
- Grundlage im Code
- Umlaute und UTF-8
- Mastermind-Beispiele
- Kompatibilität
- Barrierefreiheit und robuste Ausgabe
- Testen

## Grundprinzip

Eine Escape-Sequenz beginnt mit dem Escape-Zeichen ESC (Unicode U+001B). In einer Java-Zeichenkette wird es als \u001B geschrieben. Darauf folgt für Farben eine SGR-Anweisung (Select Graphic Rendition).

~~~java
System.out.println("\u001B[31mRot\u001B[0m");
~~~

Das Terminal empfängt ESC, [, 31, m, den Text und anschliessend ESC, [, 0, m. 31 schaltet die Vordergrundfarbe auf Rot; 0 setzt danach alle Darstellungsattribute zurück. Ohne Reset kann die gewählte Farbe in nachfolgende Ausgaben auslaufen.

ANSI-Sequenzen verändern nur die Darstellung im Terminal. Bei einer Umleitung in eine Datei bleiben die Steuerzeichen enthalten. Da dieses Projekt keinen farbfreien Modus implementiert, müssen Ausgaben immer auch ohne Farbe verständlich sein.

## Aufbau einer SGR-Sequenz

Die allgemeine Form lautet ESC[Parameter;Parameter...m. Mehrere Attribute lassen sich mit Semikolon kombinieren:

~~~java
String greenBold = "\u001B[1;32m";
String reset = "\u001B[0m";
System.out.println(greenBold + "Gewonnen!" + reset);
~~~

| Sequenz | Bedeutung |
| --- | --- |
| \u001B[0m | Alle SGR-Attribute zurücksetzen |
| \u001B[1m | Erhöhte Intensität, häufig fett |
| \u001B[31m | Rote Vordergrundfarbe |
| \u001B[32m | Grüne Vordergrundfarbe |
| \u001B[33m | Gelbe Vordergrundfarbe |
| \u001B[34m | Blaue Vordergrundfarbe |
| \u001B[35m | Magenta als Darstellung für Violett |

Für das Spiel reichen Reset und Vordergrundfarben. Stilattribute sind keine Spiellogik und werden im aktuellen Code nicht als Konstanten angeboten.

## Farbcodes

Die klassische ANSI-Palette verwendet für Vordergrundfarben die Werte 30 bis 37. Hintergrundfarben liegen bei 40 bis 47, helle Varianten bei 90 bis 97 beziehungsweise 100 bis 107.

| Farbe | Vordergrund | Heller Vordergrund |
| --- | ---: | ---: |
| Schwarz | 30 | 90 |
| Rot | 31 | 91 |
| Grün | 32 | 92 |
| Gelb | 33 | 93 |
| Blau | 34 | 94 |
| Magenta | 35 | 95 |
| Cyan | 36 | 96 |
| Weiss | 37 | 97 |

Viele Terminals verstehen zusätzlich 256-Farben- und RGB-Sequenzen:

~~~java
String orange256 = "\u001B[38;5;208m";
String customRgb = "\u001B[38;2;255;128;0m";
~~~

Diese Varianten werden nicht verwendet, weil die klassische Palette für das Lernprojekt besser portabel ist.

## Voraussetzungen und Konfiguration

Für ANSI-Farben ist keine Java-Bibliothek und keine besondere Java-Option erforderlich. Das Zielterminal für dieses Windows-Projekt ist Windows Terminal mit PowerShell. Die Unterstützung des aktuellen Hosts lässt sich prüfen:

~~~powershell
$Host.UI.SupportsVirtualTerminal
~~~

True bedeutet, dass ANSI-Sequenzen üblicherweise interpretiert werden. Bei fehlender Unterstützung bleiben die Texte und Zahlen des Spiels verständlich, Steuerzeichen können jedoch sichtbar sein.

Für die Entwicklung gelten diese Einstellungen:

1. Java-Quellcode und Markdown-Dateien werden als UTF-8 gespeichert.
2. Die Anwendung wird zusätzlich zum IntelliJ-Run-Fenster im Windows Terminal getestet.
3. Bei einer älteren klassischen Windows-Konsole kann vor dem Start chcp 65001 nötig sein.
4. Beim direkten Kompilieren wird die Quellcodierung explizit angegeben.

~~~powershell
javac -encoding UTF-8 -d out src/main/java/Ansi.java src/main/java/enums/Color.java
~~~

## Grundlage im Code

Color modelliert die sechs erlaubten Spielfarben. Jeder Enum-Wert vereint drei Eigenschaften: seine Eingabenummer von 0 bis 5, den deutschen Anzeigenamen und seine vollständige ANSI-Farbsequenz. Die Zuordnung liegt damit zentral beim jeweiligen Farbwert.

| Farbe | Eingabenummer | ANSI-Sequenz |
| --- | ---: | --- |
| Rot | 0 | \u001B[31m |
| Grün | 1 | \u001B[32m |
| Blau | 2 | \u001B[34m |
| Gelb | 3 | \u001B[33m |
| Orange | 4 | \u001B[33m |
| Violett | 5 | \u001B[35m |

Orange verwendet die portable ANSI-Farbe Gelb; der Anzeigename bleibt Orange.

Ansi ist eine finale Hilfsklasse mit privatem Konstruktor. Sie besitzt nur die Reset-Konstante und die statische Methode colour. Die Methode prüft beide Werte auf null und hängt immer genau einen Reset an:

~~~java
public final class Ansi {
    public static final String RESET = "\u001B[0m";

    private Ansi() {
    }

    public static String colour(String colour, String text) {
        return Objects.requireNonNull(colour) + Objects.requireNonNull(text) + RESET;
    }
}
~~~

Die Konsolenausgabe formatiert eine Spielfarbe so:

~~~java
String formattedColor = Ansi.colour(color.getAnsiCode(), color.getDisplayName());
~~~

Ansi enthält absichtlich keine Konstanten wie RED oder GREEN. Diese Sequenzen gehören direkt zu den entsprechenden Color-Werten. Ein Schalter für farbfreie Ausgabe, Terminalerkennung oder weitere ANSI-Stile ist nicht Teil des aktuellen Projektumfangs.

## Umlaute und UTF-8

Die deutschen Konsolentexte werden mit echten Umlauten geschrieben, beispielsweise Ungültige Eingabe, Grün und verfügbar. ANSI-Farbsequenzen verändern ihre Codierung nicht. Probleme wie falsch dargestelltes Grün entstehen nur, wenn Quelldatei, Java-Prozess und Terminal unterschiedliche Zeichenkodierungen verwenden.

Die verbindliche Kette lautet daher: Dateien in UTF-8 speichern, mit javac -encoding UTF-8 kompilieren und in einem UTF-8-fähigen Terminal ausführen.

## Mastermind-Beispiele

### Farbige Spielsteine, aber verständlich ohne Farbe

Ein farbiges Zeichen allein ist nicht ausreichend. Ein Name ergänzt die Farbe:

~~~java
System.out.println(Ansi.colour(Color.RED.getAnsiCode(), "● Rot"));
System.out.println(Ansi.colour(Color.GREEN.getAnsiCode(), "● Grün"));
System.out.println(Ansi.colour(Color.YELLOW.getAnsiCode(), "● Gelb"));
~~~

Für einen Tipp kann jeder ausgeschriebene Farbname mit seiner eigenen Sequenz formatiert werden:

~~~java
String guess = Ansi.colour(Color.RED.getAnsiCode(), "Rot")
        + " "
        + Ansi.colour(Color.BLUE.getAnsiCode(), "Blau");
System.out.println("Tipp: " + guess);
~~~

### Rückmeldung

Die Rückmeldung schreibt ihre Bedeutung aus. Symbole unterscheiden exakte und teilweise Treffer auch ohne Farbe:

~~~java
System.out.println("● 2 exakt richtig");
System.out.println("○ 1 Farbe richtig, Position falsch");
~~~

## Kompatibilität

ANSI-Sequenzen werden vom Terminal interpretiert, nicht von Java. Windows Terminal und aktuelle PowerShell unterstützen virtuelle Terminalsequenzen normalerweise direkt. In der klassischen Windows-Konsole hängt die Unterstützung von Windows-Version und Konfiguration ab. Im IntelliJ-Run-Fenster kann die Darstellung je nach Einstellung abweichen; deshalb wird immer auch im Zielterminal getestet.

Bei einer Ausgabeumleitung und in manchen Test- oder CI-Umgebungen können die Steuerzeichen sichtbar sein. Die aktuelle Version deaktiviert Farben nicht automatisch.

## Barrierefreiheit und robuste Ausgabe

- Farbe ist nie der einzige Informationsträger.
- Jede farbige Textstelle wird mit RESET abgeschlossen.
- Namen, Zahlen und Symbole ergänzen die Darstellung.
- Gelb auf hellem Hintergrund kann schlecht lesbar sein; der ausgeschriebene Name bleibt deshalb wichtig.
- Blinkende Attribute werden nicht verwendet.

## Testen

Die farbliche Darstellung selbst ist eine Eigenschaft des Terminals. Unit-Tests prüfen deshalb die erzeugte Zeichenkette statt der sichtbaren Bildschirmfarbe:

~~~java
String result = Ansi.colour(Color.RED.getAnsiCode(), "Fehler");

assertEquals("\u001B[31mFehler\u001B[0m", result);
~~~

Zusätzlich wird manuell geprüft:

1. Anwendung im vorgesehenen Terminal starten.
2. Farblegende und formatierte Farbnamen kontrollieren.
3. Prüfen, dass eine nachfolgende Ausgabe wieder normal dargestellt wird.
4. Kontrollieren, dass Texte und Symbole auch ohne Farberkennung verständlich bleiben.

## Weiterführende Links

- ECMA-48: Control Functions for Coded Character Sets
- Oracle Java API: PrintStream
- Oracle javac-Option -encoding
- Microsoft: Console Virtual Terminal Sequences
