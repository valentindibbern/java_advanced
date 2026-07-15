# Implementierungsplan – Mastermind

> **Dokumentationshinweis:** Dieses Dokument beschreibt die implementierte Architektur. Klassennamen, Verantwortlichkeiten, Datenflüsse und Spielregeln entsprechen dem aktuellen Quellcode. Bei späteren Änderungen werden Code, Tests und der betroffene Abschnitt dieses Dokuments gemeinsam nachgeführt.

## 1. Ziel, Umfang und technische Leitlinien

Dieses Dokument beschreibt ein Mastermind-Spiel mit Konsolenmodus und optionaler Swing-GUI. Das Programm erzeugt pro Runde einen geheimen, geordneten Code aus genau vier Farben. Die sechs zugelassenen Farben sind Rot, Grün, Blau, Gelb, Orange und Violett. Jede Farbe darf im Geheimcode und im Tipp mehrfach vorkommen. Der Spieler verfügt über höchstens sieben gültige Tipps. Nach jedem Tipp meldet das Programm die Zahl schwarzer Marken für richtige Farbe an richtiger Position sowie weisser Marken für richtige Farbe an falscher Position.

Die Produktionsanwendung verwendet Java 21 als Zielversion und wird mit dem versionierten Maven Wrapper gebaut. JUnit 5 ist ausschliesslich eine Testabhängigkeit. ANSI-Escape-Sequenzen sorgen in einem geeigneten Terminal für Farbe, sind aber ausschliesslich Darstellung: Alle Informationen bleiben zusätzlich durch deutsche Texte, Zahlen und Symbole verständlich. Die farbige Ausgabe ist standardmässig aktiv und kann mit `--no-color` oder einer nicht leeren Umgebungsvariable `NO_COLOR` deaktiviert werden.

Die Architektur bleibt bewusst klein. Konkrete Klassen trennen Konsolen- und Swing-Darstellung von Spielzustand, Zufallscode und Rückmeldealgorithmus. `GameSession` verwaltet den Rundenwechsel für beide Oberflächen. Swing gehört zu JDK 21, daher ist keine zusätzliche Abhängigkeit nötig.

## 2. Bedienung und Ablauf einer Runde

Beim Start zeigt die Konsole Titel, Farblegende und Eingabeform. Ein Tipp besteht aus vier durch Leerzeichen getrennten Zahlen von 0 bis 5. Der Spieler sieht vor jedem gültigen Tipp die Nummer des nächsten Versuchs. Ungültige Eingaben erklären den Fehler und zählen nicht.

~~~text
Mastermind
0 = Rot, 1 = Grün, 2 = Blau, 3 = Gelb, 4 = Orange, 5 = Violett
Gib vier Farbnummern ein, zum Beispiel: 0 3 3 5
~~~

Der Ablauf einer Runde ist genau festgelegt:

1. GameSession erzeugt beim Start und bei jedem Neustart eine neue Game-Instanz. Diese erzeugt einen geheimen Code und startet im Zustand ONGOING.
2. Die Konsole zeigt Versuch x von 7 und fordert eine Eingabe an.
3. Eine Eingabe ist nur gültig, wenn sie genau vier ganze Zahlen von 0 bis 5 enthält. Jede Nummer wird in einen Color-Wert übersetzt.
4. Game verarbeitet den gültigen Tipp, speichert ihn in der Historie und erzeugt ein TurnResult.
5. Die Konsole zeigt den Tipp farbig sowie Schwarz: x und Weiss: y. Sie zeigt keine Zuordnung von Marken zu Positionen.
6. Bei vier schwarzen Marken erhält die Runde den Zustand WON. Nach dem siebten nicht gewinnenden gültigen Tipp erhält sie LOST.
7. Im Endzustand zeigt die Konsole den Geheimcode und fragt Neue Runde? (j/n).
8. j oder J ruft GameSession.startNewRound() auf; n oder N beendet die Anwendung. Jede andere Antwort wird erneut abgefragt.

Endet die Eingabequelle, etwa bei einem automatisierten Konsolentest, beendet die Anwendung kontrolliert. Sie darf weder eine Endlosschleife noch eine unbehandelte Eingabeausnahme erzeugen.

## 3. Fachregeln, Konstanten und Datenhaltung

Game definiert die für diese Version festen, paketweit sichtbaren Konstanten CODE_LENGTH mit Wert 4 und MAX_ATTEMPTS mit Wert 7. CodeGenerator und FeedbackEvaluator verwenden dieselbe Codelänge, damit keine Klasse abweichende Zahlen kennt.

~~~java
static final int CODE_LENGTH = 4;
static final int MAX_ATTEMPTS = 7;
~~~

Eine Game-Instanz besitzt genau einen Geheimcode. Sie speichert jeden gültigen Tipp in einem zweidimensionalen Color-Array und das zugehörige Feedback im gleichen Versuchindex. Das Array guessHistory besitzt sieben Zeilen und vier Spalten. Der erste Index ist der Versuch von 0 bis 6; der zweite Index ist die Codeposition von 0 bis 3. Diese Darstellung bildet die Spielhistorie direkt ab und macht den bewussten Einsatz eines zweidimensionalen Arrays nachvollziehbar.

~~~java
private final Color[] secretCode;
private final Color[][] guessHistory;
private final Feedback[] feedbackHistory;
private int attemptsUsed;
private GameStatus status;
~~~

Alle übergebenen oder zurückgegebenen Color-Arrays werden defensiv kopiert. Kein Aufrufer darf den Geheimcode oder einen bereits gespeicherten Tipp nachträglich verändern. Nicht benutzte Historieneinträge bleiben intern leer; die Konsole zeigt nur Einträge vor attemptsUsed an.

## 4. Enums

### 4.1 Color

Color ist ein Java-Enum, keine Klasse und kein Interface. Es modelliert die einzige erlaubte Menge von Spielfarben. Ein Enum-Wert kombiniert die Eingabenummer, den deutschen Namen und seine ANSI-Farbsequenz. Dadurch können Fachklassen nur gültige Farben verarbeiten, während ConsoleUI keine eigene Umrechnungstabelle führen muss.

| Wert | Eingabenummer | Anzeigename | ANSI-Konstante | Zweck |
| --- | ---: | --- | --- | --- |
| RED | 0 | Rot | `\u001B[31m` | Rote Spielfarbe. |
| GREEN | 1 | Grün | `\u001B[32m` | Grüne Spielfarbe. |
| BLUE | 2 | Blau | `\u001B[34m` | Blaue Spielfarbe. |
| YELLOW | 3 | Gelb | `\u001B[33m` | Gelbe Spielfarbe. |
| ORANGE | 4 | Orange | `\u001B[33m` | Orange wird mit der portablen ANSI-Farbe Gelb dargestellt; der Text bleibt Orange. |
| PURPLE | 5 | Violett | `\u001B[35m` | Violette Spielfarbe. |

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| number | private | final | int | – | – | – | – | Eindeutige Nummer für die Konsoleneingabe. |
| displayName | private | final | String | – | – | – | – | Deutscher, farbunabhängig lesbarer Anzeigename. |
| ansiCode | private | final | String | – | – | – | – | Vollständige ANSI-Farbsequenz dieses Enum-Werts. |
| Color(int, String, String) | private | Enum-Konstruktor | – | number, displayName, ansiCode | int, String, String | – | – | Initialisiert jeden festen Enum-Wert. |
| fromNumber(int) | public static | – | Color | number | int | passende Farbe | Color | Ordnet 0 bis 5 zu; andere Werte führen zu `IllegalArgumentException`. |
| number() | public | – | int | – | – | Eingabenummer | int | Liefert die Nummer ohne sie zu verändern. |
| displayName() | public | – | String | – | – | Anzeigename | String | Liefert den lesbaren deutschen Namen. |
| ansiCode() | public | – | String | – | – | ANSI-Sequenz | String | Liefert nur die Darstellungssequenz, keine Spiellogik. |

### 4.2 GameStatus

GameStatus ist ein Java-Enum, keine Klasse und kein Interface. Es beschreibt ausschliesslich den Lebenszyklus einer Runde. Der Zustand wird durch Game gesetzt; ConsoleUI liest ihn nur, um Eingabe, Endmeldung und Neustartfrage zu steuern.

| Wert | Bedeutung | Zulässige nächste Aktion |
| --- | --- | --- |
| ONGOING | Die Runde akzeptiert einen weiteren gültigen Tipp. | submitGuess aufrufen. |
| WON | Ein Tipp hat vier schwarze Marken geliefert. | Geheimcode anzeigen; neue Runde oder Programmende. |
| LOST | Sieben gültige, nicht gewinnende Tipps wurden verarbeitet. | Geheimcode anzeigen; neue Runde oder Programmende. |

GameStatus besitzt für die Pflichtversion keine eigenen Attribute oder Methoden. Die drei Werte selbst bilden die vollständige Zustandsmenge; ein Endzustand wechselt in derselben Game-Instanz nicht zurück zu ONGOING.

## 5. Klassen und ihre Verantwortlichkeiten

### 5.1 Main

Main ist eine konkrete, finale Java-Klasse mit privatem Konstruktor. Sie ist kein Interface und besitzt keinen Spielzustand. Sie erstellt die gemeinsamen Abhängigkeiten, bestimmt mit `--gui` den Modus und startet entweder die Konsole oder Swing auf dem Event-Dispatch-Thread. Main enthält keine Spielregel, Eingabeprüfung oder ANSI-Formatierung.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Main() | private | – | Konstruktor | – | – | – | – | Verhindert die Instanziierung der reinen Startklasse. |
| main(String[] args) | public | static | void | args | String[] | – | – | Erstellt Random, CodeGenerator, FeedbackEvaluator und GameSession. Startet bei `--gui` MastermindFrame auf dem Event-Dispatch-Thread, sonst ConsoleUI. |
| shouldUseGui(String[]) | package-private | static | boolean | args | String[] | GUI-Modus | boolean | Liefert true, wenn die Argumente exakt `--gui` enthalten. |
| shouldUseAnsiColours(String[], String) | package-private | static | boolean | args, noColor | String[], String | Farbmodus | boolean | Liefert false, wenn `--no-color` übergeben wurde oder `NO_COLOR` nicht leer ist; sonst true. |

### 5.2 ConsoleUI

ConsoleUI ist eine konkrete Java-Klasse für den Konsolenmodus. Sie ist weder eine Fachlogikklasse noch ein Interface. Sie liest Textzeilen, prüft das Eingabeformat, bedient GameSession und schreibt verständliche Ausgaben. Sie darf nie selbst schwarze oder weisse Marken berechnen.

Die Klasse erhält Ein- und Ausgabe im Konstruktor. Dadurch kann die produktive Anwendung System.in und System.out verwenden, während Tests ByteArrayInputStream und ByteArrayOutputStream einsetzen. Sie schliesst den Scanner nicht, weil ein an System.in gebundener Eingabestrom bis zum Programmende offen bleiben soll.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| scanner | private | final | Scanner | – | – | – | – | Liest vollständige Eingabezeilen. |
| out | private | final | PrintStream | – | – | – | – | Schreibt alle sichtbaren Meldungen und erlaubt testbare Ausgabe. |
| gameSession | private | final | GameSession | – | – | – | – | Verwaltet die aktuelle Runde und erzeugt beim Neustart die nächste. |
| useAnsiColours | private | final | boolean | – | – | – | – | Steuert, ob Farbnamen mit ANSI-Sequenzen oder als reiner Text ausgegeben werden. |
| ConsoleUI(Scanner, PrintStream, GameSession, boolean) | public | – | Konstruktor | scanner, out, gameSession, useAnsiColours | Scanner, PrintStream, GameSession, boolean | – | – | Übernimmt alle Abhängigkeiten, weist null-Werte sofort zurück und speichert den Farbmodus. |
| run() | public | – | void | – | – | – | – | Zeigt Begrüssung und Legende, startet Runden und endet bei n/N oder Eingabeende. |
| playRound() | private | – | boolean | – | – | Eingabe fortsetzen | boolean | Verarbeitet Tipps über GameSession bis WON oder LOST; gibt false zurück, wenn die Eingabequelle endet. |
| readGuess() | private | – | Color[] | – | – | gültiger Tipp oder leer | Color[] oder null | Prüft Zeile, Anzahl, Zahlen und Bereich. null ist ausschliesslich das kontrollierte Abbruchsignal bei Eingabeende. |
| readRestartChoice() | private | – | boolean | – | – | neue Runde ja/nein | boolean | Akzeptiert nur j/J oder n/N und fragt andere Eingaben erneut ab. |
| showLegend() | private | – | void | – | – | – | – | Gibt Nummer, Namen und Farbdarstellung aller Color-Werte aus. |
| showTurnResult(TurnResult) | private | – | void | result | TurnResult | – | – | Zeigt Versuch, formatierten Tipp und beide Markenzahlen. |
| showEndMessage() | private | – | void | – | – | – | Zeigt abhängig von WON oder LOST die Endmeldung und den durch GameSession freigegebenen Geheimcode. |
| formatColor(Color) | private | – | String | color | Color | formatierter Farbname | String | Liefert bei aktivem Farbmodus Ansi.colour(color.ansiCode(), color.displayName()), sonst nur color.displayName(). |
| formatColors(Color[]) | private | – | String | code | Color[] | formatierter Code | String | Liest die vier Positionen in Reihenfolge und verbindet deren formatierte Namen. |

### 5.3 Ansi

Ansi ist eine konkrete, finale Java-Hilfsklasse mit privatem Konstruktor. Sie ist kein Interface und enthält keinerlei Spielzustand oder Spielregel. Sie definiert die technischen ANSI-SGR-Sequenzen nur einmal und stellt eine sichere Methode bereit, die nach jedem farbigen Text RESET anhängt. So kann keine Ausgabe versehentlich weitergefärbt bleiben.

Ansi steuert keine Terminalfähigkeit. Der Schalter für farbfreie Ausgabe liegt in Main und ConsoleUI. Die Pflichtversion verwendet die klassische ANSI-Palette direkt. Falls ein Terminal ANSI nicht interpretiert, bleiben die deutschen Texte dennoch verständlich, und der Benutzer kann die Steuerzeichen mit `--no-color` oder `NO_COLOR` vermeiden.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| RESET | public | static final | String | – | – | – | – | ANSI-Sequenz zum Zurücksetzen aller Darstellungsattribute. |
| Ansi() | private | – | Konstruktor | – | – | – | – | Verhindert Instanzen einer reinen Hilfsklasse. |
| colour(String, String) | public | static | String | colour, text | String, String | umschlossener Text | String | Prüft beide Eingaben auf nicht null und liefert colour + text + RESET. |

### 5.4 CodeGenerator

CodeGenerator ist eine konkrete Java-Klasse für die Erzeugung eines Geheimcodes. Sie ist kein Interface und speichert keinen Spielverlauf. Ihr einziger veränderungsrelevanter Einfluss ist die injizierte Zufallsquelle. Der Konstruktor nimmt Random entgegen, damit die Anwendung echten Zufall nutzt und Tests einen reproduzierbaren Random mit festem Seed einsetzen können.

generateCode erstellt für jeden Aufruf ein neues Color-Array der Länge Game.CODE_LENGTH. Für jede Position wird unabhängig ein Element von Color.values() gewählt. Es gibt ausdrücklich keine Prüfung auf bereits verwendete Farben, weil Duplikate nach den Spielregeln erlaubt sind.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| random | private | final | Random | – | – | – | – | Einzige Quelle für die zufällige Farbauswahl. |
| CodeGenerator(Random) | public | – | Konstruktor | random | Random | – | – | Speichert eine nicht null Zufallsquelle. |
| generateCode() | public | – | Color[] | – | – | Geheimcode | Color[] | Erzeugt ein neues vierstelliges Array mit ausschliesslich bekannten Color-Werten. |

### 5.5 Game

Game ist die zentrale konkrete Fachklasse einer einzelnen Mastermind-Runde. Sie ist kein Interface, keine UI-Klasse und kein globaler Spielmanager. Jede Instanz besitzt genau einen Geheimcode, eine eigene Historie, einen Versuchszähler und einen Status. Nur diese Klasse darf einen gültigen Tipp speichern oder den Status zu WON beziehungsweise LOST ändern.

Der produktive Konstruktor erhält CodeGenerator und FeedbackEvaluator. Der Testkonstruktor erhält einen bekannten Geheimcode sowie den Evaluator. Beide Wege führen zur gleichen internen Initialisierung: Der Geheimcode wird geprüft und kopiert, die Historie wird leer angelegt, attemptsUsed beginnt bei 0 und status bei ONGOING. Nach einem Endzustand lehnt submitGuess weitere Tipps ab.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| CODE_LENGTH | package-private | static final | int | – | – | – | – | Feste Codelänge 4 für Generator, Evaluator und Validierung. |
| MAX_ATTEMPTS | package-private | static final | int | – | – | – | – | Feste maximale Anzahl gültiger Tipps 7. |
| secretCode | private | final | Color[] | – | – | – | – | Defensive Kopie des geheimen vierstelligen Codes. |
| guessHistory | private | final | Color[][] | – | – | – | – | Historie mit MAX_ATTEMPTS Zeilen und CODE_LENGTH Spalten. |
| feedbackHistory | private | final | Feedback[] | – | – | – | – | Feedback am gleichen Index wie der zugehörige Tipp. |
| feedbackEvaluator | private | final | FeedbackEvaluator | – | – | – | – | Zustandsloser Dienst für die Markenauswertung. |
| attemptsUsed | private | – | int | – | – | – | – | Zahl gespeicherter gültiger Tipps von 0 bis 7. |
| status | private | – | GameStatus | – | – | – | – | Aktueller Rundenstatus; beginnt bei ONGOING. |
| Game(CodeGenerator, FeedbackEvaluator) | public | – | Konstruktor | generator, evaluator | CodeGenerator, FeedbackEvaluator | – | – | Lässt den Generator den Geheimcode erzeugen und initialisiert die Runde. |
| Game(Color[], FeedbackEvaluator) | public | – | Konstruktor | secretCode, evaluator | Color[], FeedbackEvaluator | – | – | Testkonstruktor mit bekanntem, defensiv kopiertem Geheimcode. |
| submitGuess(Color[]) | public | – | TurnResult | guess | Color[] | Ergebnis des Tipps | TurnResult | Prüft Status und Tipp, wertet aus, speichert Kopien, erhöht Versuchszahl und aktualisiert Status. |
| getStatus() | public | – | GameStatus | – | – | Rundenstatus | GameStatus | Liefert den aktuellen Enum-Wert. |
| getAttemptsUsed() | public | – | int | – | – | gültige Versuchszahl | int | Liefert die Zahl verarbeiteter Tipps. |
| getSecretCode() | public | – | Color[] | – | – | Codekopie | Color[] | Liefert nur eine defensive Kopie. |
| getGuessHistory() | public | – | Color[][] | – | – | tiefe Historienkopie | Color[][] | Liefert eine tiefe Kopie, damit keine Zeile verändert werden kann. |
| getFeedbackHistory() | public | – | Feedback[] | – | – | Historienkopie | Feedback[] | Liefert eine Kopie der Referenzen auf unveränderliche Feedback-Werte. |
| validateCode(Color[], String) | private | static | void | code, context | Color[], String | – | – | Prüft Länge und null-Werte und erzeugt bei Fehlern eine klare IllegalArgumentException. |

### 5.6 CodeValidator

CodeValidator ist eine package-private, finale Hilfsklasse. Sie bündelt die Prüfung von Farbcode-Arrays, damit Game und FeedbackEvaluator dieselbe Fehlergrenze verwenden. Die Klasse erzeugt keine Spielobjekte und besitzt keinen Zustand.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| CodeValidator() | private | – | Konstruktor | – | – | – | – | Verhindert Instanzen einer reinen Hilfsklasse. |
| validateCode(Color[], String) | package-private | static | void | code, context | Color[], String | – | – | Prüft Länge und null-Werte und erzeugt bei Fehlern eine klare IllegalArgumentException. |

### 5.7 FeedbackEvaluator

FeedbackEvaluator ist eine konkrete, zustandslose Java-Klasse für genau eine fachliche Aufgabe: die Berechnung schwarzer und weisser Marken. Sie ist kein Interface und kennt weder ConsoleUI noch GameStatus noch Historie. Dadurch lässt sie sich mit bekannten Arrays unabhängig und deterministisch testen.

evaluate prüft zuerst beide Codes. Im ersten Durchlauf zählt sie schwarze Marken und markiert verwendete Positionen in zwei lokalen boolean-Arrays. Im zweiten Durchlauf sucht sie für jede noch unmarkierte Tippfarbe genau eine noch unmarkierte Geheimcodefarbe. Bei einem Treffer zählt sie eine weisse Marke und markiert die Geheimcodeposition. Diese Reihenfolge verhindert Doppelzählungen bei wiederholten Farben.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Keine Instanzattribute | – | zustandslos | – | – | – | – | – | Jede Auswertung verwendet nur Parameter und lokale Variablen. |
| evaluate(Color[], Color[]) | public | – | Feedback | secret, guess | Color[], Color[] | Markenergebnis | Feedback | Berechnet zuerst schwarze und danach weisse Marken ohne Positionszuordnung. |
| validateCode(Color[], String) | private | static | void | code, context | Color[], String | – | – | Prüft Codelänge und nicht null Elemente vor jeder Auswertung. |

### 5.8 Feedback

Feedback ist eine konkrete, unveränderliche Java-Wertklasse. Sie ist kein Interface und enthält nur das Ergebnis einer Rückmeldeberechnung. Ein Feedback-Objekt wird nach der Erstellung nicht verändert und kann deshalb sicher in Historien gespeichert sowie von TurnResult weitergegeben werden.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| blackMarks | private | final | int | – | – | – | – | Anzahl richtiger Farben an richtiger Position; 0 bis CODE_LENGTH. |
| whiteMarks | private | final | int | – | – | – | – | Zusätzliche richtige Farben an falscher Position; zusammen mit blackMarks höchstens CODE_LENGTH. |
| Feedback(int, int) | public | – | Konstruktor | blackMarks, whiteMarks | int, int | – | – | Prüft nicht negative Werte und die maximale Gesamtzahl. |
| getBlackMarks() | public | – | int | – | – | schwarze Marken | int | Liefert die unveränderliche schwarze Anzahl. |
| getWhiteMarks() | public | – | int | – | – | weisse Marken | int | Liefert die unveränderliche weisse Anzahl. |

### 5.9 TurnResult

TurnResult ist eine konkrete, unveränderliche Java-Wertklasse für das Ergebnis eines erfolgreich verarbeiteten Tipps. Sie ist kein Interface und enthält keinen eigenen Spielzustand. Game erstellt ein TurnResult erst, nachdem Tipp, Feedback, Versuchszahl und neuer Status feststehen. ConsoleUI erhält damit alle Informationen eines Zuges zusammenhängend, ohne mehrfach den Zustand von Game abfragen zu müssen.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| guess | private | final | Color[] | – | – | – | – | Defensive Kopie des gerade gespeicherten vierstelligen Tipps. |
| feedback | private | final | Feedback | – | – | – | – | Unveränderliches Markenergebnis dieses Tipps. |
| status | private | final | GameStatus | – | – | – | – | Status der Runde unmittelbar nach diesem Tipp. |
| attemptNumber | private | final | int | – | – | – | – | Einsbasierte Versuchszahl von 1 bis 7 für die Ausgabe. |
| TurnResult(Color[], Feedback, GameStatus, int) | public | – | Konstruktor | guess, feedback, status, attemptNumber | Color[], Feedback, GameStatus, int | – | – | Prüft Werte und kopiert guess defensiv. |
| getGuess() | public | – | Color[] | – | – | Tippkopie | Color[] | Liefert eine defensive Kopie des Tipps. |
| getFeedback() | public | – | Feedback | – | – | Markenergebnis | Feedback | Liefert das unveränderliche Feedback. |
| getStatus() | public | – | GameStatus | – | – | Status | GameStatus | Liefert den Status nach dem Zug. |
| getAttemptNumber() | public | – | int | – | – | Versuchszahl | int | Liefert die einsbasierte Nummer des Zuges. |

## 6. Abhängigkeiten und Objektfluss

Die Abhängigkeiten verlaufen von der Darstellung zur Fachlogik; keine Fachklasse hängt von Scanner, PrintStream, ANSI oder Swing ab.

~~~text
Main → GameSession → Game → CodeGenerator
  ├──→ ConsoleUI → Color → Ansi
  └──→ MastermindFrame → MastermindPanel → PegView / FeedbackView
GameSession → FeedbackEvaluator → Feedback
ConsoleUI / MastermindPanel ← TurnResult ← Game
~~~

Main verdrahtet die konkreten Klassen. GameSession erstellt und bedient Game. ConsoleUI verwendet Color zur Eingabeumrechnung und Ansi nur für Texte; MastermindPanel erzeugt gültige Color-Arrays nur über Farbbuttons. FeedbackEvaluator liefert Feedback, während TurnResult eine abgeschlossene einzelne Tippverarbeitung transportiert. Diese Richtung verhindert, dass Regeln an eine Oberfläche gekoppelt werden.

## 7. Fehlerbehandlung und Zustandsgrenzen

Eingabefehler des Spielers sind erwartete Bedienfälle. ConsoleUI behandelt leere Eingaben, Text statt Zahl, Dezimalzahlen, falsche Anzahl Werte und Zahlen ausserhalb von 0 bis 5 mit einer konkreten Meldung. Diese Fälle rufen submitGuess nicht auf und verändern weder attemptsUsed noch Historie noch GameStatus.

Die Anwendung verwendet für die Farbzuordnung kein `Optional`. `Color.fromNumber(int)` liefert für `0` bis `5` einen gültigen `Color`-Wert und wirft für jeden anderen Wert `IllegalArgumentException`. ConsoleUI fängt diese erwartete Ausnahme bei der Texteingabe ab, schreibt eine deutsche Bereichsmeldung und fordert erneut eine Eingabe an. Die Ausnahme ist damit die technische Fehlergrenze; sie wird nicht als Stacktrace an den Spieler weitergegeben.

Fehlerhafte Aufrufe innerhalb des Programms sind davon getrennt. Game und FeedbackEvaluator weisen Arrays mit falscher Länge oder null-Elementen mit IllegalArgumentException ab. Game weist einen Tipp nach WON oder LOST mit IllegalStateException ab. Diese Fehler zeigen Programmierfehler früh an, statt einen ungültigen Spielzustand zu speichern.

Der Geheimcode wird während ONGOING nie ausgegeben. Nach WON oder LOST wird kein weiterer Tipp angenommen. Eine neue Runde bedeutet stets eine neue Game-Instanz und damit einen vollständig neuen Geheimcode, Zähler, Status und Historie.

## 8. Code- und Testdateien

Die Fachklassen liegen im Package `ch.valentindibbern.mastermind` unter `src/main/java/ch/valentindibbern/mastermind/`. Die JUnit-Tests spiegeln dieses Package unter `src/test/java/ch/valentindibbern/mastermind/`. Der Maven Wrapper ist der verbindliche Build- und Testweg. `.\mvnw.cmd package` erzeugt das ausführbare, versionslose JAR `target/mastermind.jar`; sein Manifest verweist auf `ch.valentindibbern.mastermind.Main`.

| Produktionsdatei | Enthält | Verantwortlichkeit |
| --- | --- | --- |
| Main.java | finale Klasse Main | Startet die Anwendung und verdrahtet Abhängigkeiten. |
| GameSession.java | finale Klasse GameSession | Verwaltet die aktuelle Runde und erzeugt neue Runden für beide Oberflächen. |
| Color.java | Enum Color | Definiert die sechs Spielfarben und ihre Darstellungseigenschaften. |
| GameStatus.java | Enum GameStatus | Definiert ONGOING, WON und LOST. |
| Ansi.java | finale Klasse Ansi | Zentralisiert ANSI-Sequenzen und colour. |
| CodeGenerator.java | Klasse CodeGenerator | Erzeugt zufällige Geheimcodes. |
| CodeValidator.java | finale Hilfsklasse CodeValidator | Prüft Farbcode-Arrays zentral für Game und FeedbackEvaluator. |
| FeedbackEvaluator.java | Klasse FeedbackEvaluator | Berechnet schwarze und weisse Marken. |
| Feedback.java | unveränderliche Klasse Feedback | Speichert die beiden Markenzahlen. |
| TurnResult.java | unveränderliche Klasse TurnResult | Transportiert das Ergebnis eines Tipps. |
| Game.java | Klasse Game | Besitzt und verarbeitet genau eine Spielrunde. |
| ConsoleUI.java | Klasse ConsoleUI | Liest, validiert und zeigt den Konsolenablauf. |
| MastermindFrame.java | finale Klasse MastermindFrame | Swing-Fenster und Enddialog. |
| MastermindPanel.java | Klasse MastermindPanel | Klassisches Spielbrett, Farbwahl und Zugdarstellung. |
| PegView.java / FeedbackView.java | Swing-Komponenten | Zeichnen Farb- und Rückmeldesteine ohne Bilddateien. |

| Testdatei | Prüft | Wichtige Fälle |
| --- | --- | --- |
| ColorTest.java | Color | Nummern 0 bis 5, ungültige Nummern, Namen und Sequenzzuordnung. |
| AnsiTest.java | Ansi | Zusammensetzen von Farbsequenz, Text und genau einem RESET; null-Argumente. |
| FeedbackTest.java | Feedback | Gültige und ungültige Markenkombinationen. |
| TurnResultTest.java | TurnResult | Defensives Kopieren und ungültige Zugdaten. |
| CodeGeneratorTest.java | CodeGenerator | Länge vier, bekannte Farben, reproduzierbarer Zufall. |
| FeedbackEvaluatorTest.java | FeedbackEvaluator | Volltreffer, Vertauschung, keine Treffer, schwarze vor weissen Marken, Duplikate. |
| GameTest.java | Game | Anfangszustand, Sieg, siebte Niederlage, Historie, Kopien und abgelehnte Tipps. |
| ConsoleUITest.java | ConsoleUI | Ungültige Eingaben zählen nicht, Neustart, kontrolliertes Eingabeende sowie farbige und farblose Ausgabe. |
| GameSessionTest.java | GameSession | Rundenstart, Ende, verdeckter Geheimcode und defensives Kopieren. |
| MastermindPanelTest.java | GUI-Bedienung | Auswahl, Löschen, Rückmeldung, Rundenende und Neustart auf dem Event-Dispatch-Thread. |
| MainTest.java | Main | GUI-Flag sowie Farbmodus aus `--no-color` und `NO_COLOR`. |

## 9. Umsetzungsreihenfolge

1. Einfache Werttypen und Enums korrigieren sowie die Java-21- und Maven-Wrapper-Konfiguration sicherstellen.
2. FeedbackEvaluator mit dem zweiphasigen Algorithmus und zugehörigen Duplikattests umsetzen.
3. CodeGenerator mit injizierbarem Random und deterministischen Tests umsetzen.
4. Game mit defensiven Kopien, siebenzeiliger Historie, Statuswechseln und beiden Konstruktoren umsetzen.
5. ConsoleUI mit injizierbarer Ein- und Ausgabe, Validierung, Rundenablauf und Neustartfrage umsetzen.
6. ANSI-Darstellung ausschliesslich über Ansi.colour in ConsoleUI verwenden.
7. Main mit klassischer `public static void main(String[] args)` verdrahten.
8. Alle JUnit-Tests mit `.\mvnw.cmd test` sowie die manuellen Konsolenszenarien mit Java 21 oder neuer ausführen.

## 10. Testkonzept

Unit-Tests verwenden bekannte Color-Arrays und kontrollierte Zufallsquellen; sie verlassen sich nie auf echten Zufall oder auf die tatsächliche Bildschirmfarbe eines Terminals. ANSI-Tests vergleichen erzeugte Zeichenketten, nicht die visuelle Darstellung. Testnamen beschreiben beobachtbares Verhalten, beispielsweise returnsBlackMarkForCorrectColourAndPosition.

| Bereich | Testfall | Erwartung |
| --- | --- | --- |
| Color | Gültige und ungültige Nummer | 0 bis 5 liefern den passenden Enum-Wert; andere Werte lösen `IllegalArgumentException` aus. |
| Ansi | Farbigen Text umschliessen | colour liefert Farbsequenz, Text und abschliessendes RESET. |
| CodeGenerator | Gültiger Code | Jeder neue Code hat vier nicht null Farben aus Color.values(). |
| FeedbackEvaluator | Exakter Code | 4 schwarz und 0 weiss. |
| FeedbackEvaluator | Alle Positionen vertauscht | 0 schwarz und 4 weiss. |
| FeedbackEvaluator | Duplikate | Keine Farbe wird häufiger markiert, als sie im Geheimcode vorkommt. |
| Game | Sieg | Ein Volltreffer setzt WON und speichert Versuch 1. |
| Game | Niederlage | Erst der siebte erfolglose gültige Tipp setzt LOST. |
| Game | Abgeschlossenes Spiel | Ein weiterer Tipp löst IllegalStateException aus. |
| Game | Defensive Kopien | Änderungen an Eingabe- oder Rückgabe-Arrays verändern keine gespeicherten Daten. |
| ConsoleUI | Ungültige Eingabe | Meldung erscheint; der nächste gültige Tipp bleibt Versuch 1. |

Manuell wird die Anwendung im vorgesehenen Terminal getestet: leere Zeile, Text, drei oder fünf Zahlen, -1, 6 und Dezimalzahlen; Sieg im ersten und siebten Versuch; sieben falsche Tipps; j, J, n, N und ungültige Neustartantwort; neue Runde mit zurückgesetzter Historie; farbige Legende, Tipps, Gewinn, Verlust und Rückmeldung. Dabei wird kontrolliert, dass jede Aussage auch ohne Farbe durch Text und Zahlen verständlich bleibt.

## 11. README

Die README dokumentiert die umgesetzte Anwendung: benötigtes JDK 21 oder neuer, den Maven-Wrapper-Befehl `.\mvnw.cmd package` sowie die JAR-Aufrufe `java -jar target\mastermind.jar`, `java -jar target\mastermind.jar --gui` und `java -jar target\mastermind.jar --no-color`, Farblegende, Eingabeformat, Bedeutung schwarzer und weisser Marken, farbige beziehungsweise farblose Konsolenausgabe, GUI-Bedienung sowie die Ausführung der Tests mit `.\mvnw.cmd test`.

## 12. Optionaler Swing-GUI-Modus

Das Flag `--gui` startet `MastermindFrame` auf dem Swing-Event-Dispatch-Thread. Ohne Flag bleibt die Konsole der Standardmodus. `--no-color` und `NO_COLOR` haben nur im Konsolenmodus eine Wirkung; Swing verwendet eigene RGB-Farben aus `SwingPalette`.

`MastermindPanel` zeigt den verdeckten Geheimcode, sieben feste Versuchzeilen und pro Zeile vier Tippsteine sowie ein 2×2-Feld für Rückmeldesteine. Die sechs textlich beschrifteten Farbbuttons füllen das lokale `Color[4]` von links nach rechts; Wiederholungen sind erlaubt. `Letzte Farbe löschen` entfernt den letzten Eintrag. `Tipp prüfen` ist nur bei vier gewählten Farben aktiv und übergibt den Tipp an GameSession.

Nach einem Zug zeichnet die GUI schwarze Marken vor weissen Marken, ohne Positionen zuzuordnen. Bei Sieg oder Niederlage deckt sie den Geheimcode auf, sperrt die Eingabe und verwendet einen deutschen Ja/Nein-Dialog. Ja erstellt durch `GameSession.startNewRound()` eine vollständig leere neue Runde, Nein schliesst das Fenster. Die `RoundEndPrompt`-Abstraktion erlaubt es, diese Dialogentscheidung in Tests ohne sichtbares Fenster zu simulieren.
