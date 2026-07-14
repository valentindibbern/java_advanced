# Implementierungsplan – Mastermind

> **Planungshinweis für das gesamte Dokument:** Dieser Plan beschreibt die vorgesehene Implementierung vor dem Programmieren. Klassennamen, Verantwortlichkeiten, Datenflüsse und Spielregeln sind verbindliche Leitplanken. Einzelne Sichtbarkeiten, Hilfsmethoden, Parameter oder Rückgabetypen können sich während der Umsetzung ändern, falls Tests oder eine einfachere Lösung dies fachlich begründen. Der tatsächlich implementierte und getestete Code ist am Ende massgebend.

## 1. Ziel, Umfang und technische Leitlinien

Dieses Dokument plant ein Mastermind-Spiel als Java-Kommandozeilenprogramm. Das Programm erzeugt pro Runde einen geheimen, geordneten Code aus genau vier Farben. Die sechs zugelassenen Farben sind Rot, Grün, Blau, Gelb, Orange und Violett. Jede Farbe darf im Geheimcode und im Tipp mehrfach vorkommen. Der Spieler verfügt über höchstens sieben gültige Tipps. Nach jedem Tipp meldet das Programm die Zahl schwarzer Marken für richtige Farbe an richtiger Position sowie weisser Marken für richtige Farbe an falscher Position.

Die Anwendung verwendet ausschliesslich das JDK. Sie benötigt keine externe Bibliothek, keinen Build-Manager und keine Konfigurationsdatei. ANSI-Escape-Sequenzen sorgen in einem geeigneten Terminal für Farbe, sind aber ausschliesslich Darstellung: Alle Informationen bleiben zusätzlich durch deutsche Texte, Zahlen und Symbole verständlich.

Die Architektur bleibt bewusst klein. Konkrete Klassen statt abstrakter Frameworks trennen Konsolenein- und -ausgabe von Spielzustand, Zufallscode und Rückmeldealgorithmus. So ist die Fachlogik isoliert testbar, ohne eine nicht benötigte Erweiterungsinfrastruktur einzuführen.

## 2. Bedienung und Ablauf einer Runde

Beim Start zeigt die Konsole Titel, Farblegende und Eingabeform. Ein Tipp besteht aus vier durch Leerzeichen getrennten Zahlen von 1 bis 6. Der Spieler sieht vor jedem gültigen Tipp die Nummer des nächsten Versuchs. Ungültige Eingaben erklären den Fehler und zählen nicht.

~~~text
Mastermind
1 = Rot, 2 = Grün, 3 = Blau, 4 = Gelb, 5 = Orange, 6 = Violett
Gib vier Farbnummern ein, zum Beispiel: 1 4 4 6
~~~

Der Ablauf einer Runde ist genau festgelegt:

1. Die Konsole erstellt eine neue Game-Instanz. Diese erzeugt einen geheimen Code und startet im Zustand ONGOING.
2. Die Konsole zeigt Versuch x von 7 und fordert eine Eingabe an.
3. Eine Eingabe ist nur gültig, wenn sie genau vier ganze Zahlen von 1 bis 6 enthält. Jede Nummer wird in einen Color-Wert übersetzt.
4. Game verarbeitet den gültigen Tipp, speichert ihn in der Historie und erzeugt ein TurnResult.
5. Die Konsole zeigt den Tipp farbig sowie Schwarz: x und Weiss: y. Sie zeigt keine Zuordnung von Marken zu Positionen.
6. Bei vier schwarzen Marken erhält die Runde den Zustand WON. Nach dem siebten nicht gewinnenden gültigen Tipp erhält sie LOST.
7. Im Endzustand zeigt die Konsole den Geheimcode und fragt Neue Runde? (j/n).
8. j oder J startet mit einer neuen Game-Instanz; n oder N beendet die Anwendung. Jede andere Antwort wird erneut abgefragt.

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

Color ist ein Java-Enum, keine Klasse und kein Interface. Es modelliert die einzige erlaubte Menge von Spielfarben. Ein Enum-Wert kombiniert die Eingabenummer, den deutschen Namen und die bereits in Ansi definierte Farbsequenz. Dadurch können Fachklassen nur gültige Farben verarbeiten, während ConsoleUI keine eigene Umrechnungstabelle führen muss.

| Wert | Eingabenummer | Anzeigename | ANSI-Konstante | Zweck |
| --- | ---: | --- | --- | --- |
| RED | 1 | Rot | Ansi.RED | Rote Spielfarbe. |
| GREEN | 2 | Grün | Ansi.GREEN | Grüne Spielfarbe. |
| BLUE | 3 | Blau | Ansi.BLUE | Blaue Spielfarbe. |
| YELLOW | 4 | Gelb | Ansi.YELLOW | Gelbe Spielfarbe. |
| ORANGE | 5 | Orange | Ansi.YELLOW | Orange wird mit der portablen ANSI-Farbe Gelb dargestellt; der Text bleibt Orange. |
| PURPLE | 6 | Violett | Ansi.MAGENTA | Violette Spielfarbe. |

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| number | private | final | int | – | – | – | – | Eindeutige Nummer für die Konsoleneingabe. |
| displayName | private | final | String | – | – | – | – | Deutscher, farbunabhängig lesbarer Anzeigename. |
| ansiCode | private | final | String | – | – | – | – | Referenz auf eine vollständige ANSI-Farbsequenz aus Ansi. |
| Color(int, String, String) | private | Enum-Konstruktor | – | number, displayName, ansiCode | int, String, String | – | – | Initialisiert jeden festen Enum-Wert. |
| fromNumber(int) | public static | – | Optional<Color> | number | int | passende Farbe oder leer | Optional<Color> | Ordnet 1 bis 6 zu; andere Werte sind keine Spielfarbe. |
| getNumber() | public | – | int | – | – | Eingabenummer | int | Liefert die Nummer ohne sie zu verändern. |
| getDisplayName() | public | – | String | – | – | Anzeigename | String | Liefert den lesbaren deutschen Namen. |
| getAnsiCode() | public | – | String | – | – | ANSI-Sequenz | String | Liefert nur die Darstellungssequenz, keine Spiellogik. |

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

Main ist eine konkrete, finale Java-Klasse mit privatem Konstruktor. Sie ist kein Interface und besitzt keinen Spielzustand. Ihre einzige Aufgabe besteht darin, die Abhängigkeiten der Anwendung an einem sichtbaren Ort zu erstellen und den Konsolenablauf zu starten. Main enthält ausdrücklich keine Spielregel, Eingabeprüfung oder ANSI-Formatierung.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Main() | private | – | Konstruktor | – | – | – | – | Verhindert die Instanziierung der reinen Startklasse. |
| main(String[] args) | public | static | void | args | String[] | – | – | Erstellt Random, CodeGenerator, FeedbackEvaluator, Scanner, PrintStream und ConsoleUI. Ruft anschliessend genau consoleUI.run() auf. args wird nicht ausgewertet. |

### 5.2 ConsoleUI

ConsoleUI ist eine konkrete Java-Klasse für die gesamte Benutzerschnittstelle. Sie ist weder eine Fachlogikklasse noch ein Interface. Sie liest Textzeilen, prüft das Eingabeformat, erstellt Runden, ruft Game auf und schreibt verständliche Ausgaben. Sie darf nie selbst schwarze oder weisse Marken berechnen und verändert nie Felder von Game direkt.

Die Klasse erhält Ein- und Ausgabe im Konstruktor. Dadurch kann die produktive Anwendung System.in und System.out verwenden, während Tests ByteArrayInputStream und ByteArrayOutputStream einsetzen. Sie schliesst den Scanner nicht, weil ein an System.in gebundener Eingabestrom bis zum Programmende offen bleiben soll.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| scanner | private | final | Scanner | – | – | – | – | Liest vollständige Eingabezeilen. |
| out | private | final | PrintStream | – | – | – | – | Schreibt alle sichtbaren Meldungen und erlaubt testbare Ausgabe. |
| codeGenerator | private | final | CodeGenerator | – | – | – | – | Wird bei jeder neuen Runde an Game übergeben. |
| feedbackEvaluator | private | final | FeedbackEvaluator | – | – | – | – | Wird bei jeder neuen Runde an Game übergeben, nicht direkt zur Berechnung genutzt. |
| ConsoleUI(Scanner, PrintStream, CodeGenerator, FeedbackEvaluator) | public | – | Konstruktor | scanner, out, codeGenerator, feedbackEvaluator | Scanner, PrintStream, CodeGenerator, FeedbackEvaluator | – | – | Übernimmt alle Abhängigkeiten und weist null-Werte sofort zurück. |
| run() | public | – | void | – | – | – | – | Zeigt Begrüssung und Legende, startet Runden und endet bei n/N oder Eingabeende. |
| createGame() | private | – | Game | – | – | neue Runde | Game | Erstellt eine neue Game-Instanz aus Generator und Evaluator. |
| playRound(Game) | private | – | boolean | game | Game | Eingabe fortsetzen | boolean | Verarbeitet Tipps bis WON oder LOST; gibt false zurück, wenn die Eingabequelle endet. |
| readGuess() | private | – | Color[] | – | – | gültiger Tipp oder leer | Color[] oder null | Prüft Zeile, Anzahl, Zahlen und Bereich. null ist ausschliesslich das kontrollierte Abbruchsignal bei Eingabeende. |
| readRestartChoice() | private | – | boolean | – | – | neue Runde ja/nein | boolean | Akzeptiert nur j/J oder n/N und fragt andere Eingaben erneut ab. |
| showLegend() | private | – | void | – | – | – | – | Gibt Nummer, Namen und Farbdarstellung aller Color-Werte aus. |
| showTurnResult(TurnResult) | private | – | void | result | TurnResult | – | – | Zeigt Versuch, formatierten Tipp und beide Markenzahlen. |
| showEndMessage(Game) | private | – | void | game | Game | – | – | Zeigt abhängig von WON oder LOST die Endmeldung und den Geheimcode. |
| formatColor(Color) | private | – | String | color | Color | formatierter Farbname | String | Ruft Ansi.colour(color.getAnsiCode(), color.getDisplayName()) auf. |
| formatColors(Color[]) | private | – | String | code | Color[] | formatierter Code | String | Liest die vier Positionen in Reihenfolge und verbindet deren formatierte Namen. |

### 5.3 Ansi

Ansi ist eine konkrete, finale Java-Hilfsklasse mit privatem Konstruktor. Sie ist kein Interface und enthält keinerlei Spielzustand oder Spielregel. Sie definiert die technischen ANSI-SGR-Sequenzen nur einmal und stellt eine sichere Methode bereit, die nach jedem farbigen Text RESET anhängt. So kann keine Ausgabe versehentlich weitergefärbt bleiben.

Ansi steuert keine Terminalfähigkeit und führt keinen Schalter für farbfreie Ausgabe ein. Die Pflichtversion verwendet die klassische ANSI-Palette direkt. Falls ein Terminal ANSI nicht interpretiert, bleiben die deutschen Texte dennoch verständlich, obwohl Steuerzeichen sichtbar werden können.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| RESET | public | static final | String | – | – | – | – | ANSI-Sequenz zum Zurücksetzen aller Darstellungsattribute. |
| RED | public | static final | String | – | – | – | – | ANSI-Vordergrundfarbe Rot. |
| GREEN | public | static final | String | – | – | – | – | ANSI-Vordergrundfarbe Grün. |
| YELLOW | public | static final | String | – | – | – | – | ANSI-Vordergrundfarbe Gelb. |
| BLUE | public | static final | String | – | – | – | – | ANSI-Vordergrundfarbe Blau. |
| MAGENTA | public | static final | String | – | – | – | – | ANSI-Vordergrundfarbe Magenta für Violett. |
| CYAN | public | static final | String | – | – | – | – | Vollständige Standardpalette; in dieser Spielversion keiner Spielfarbe zugeordnet. |
| BOLD | public | static final | String | – | – | – | – | Optionale Hervorhebung für Text, nie alleiniger Informationsträger. |
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

### 5.6 FeedbackEvaluator

FeedbackEvaluator ist eine konkrete, zustandslose Java-Klasse für genau eine fachliche Aufgabe: die Berechnung schwarzer und weisser Marken. Sie ist kein Interface und kennt weder ConsoleUI noch GameStatus noch Historie. Dadurch lässt sie sich mit bekannten Arrays unabhängig und deterministisch testen.

evaluate prüft zuerst beide Codes. Im ersten Durchlauf zählt sie schwarze Marken und markiert verwendete Positionen in zwei lokalen boolean-Arrays. Im zweiten Durchlauf sucht sie für jede noch unmarkierte Tippfarbe genau eine noch unmarkierte Geheimcodefarbe. Bei einem Treffer zählt sie eine weisse Marke und markiert die Geheimcodeposition. Diese Reihenfolge verhindert Doppelzählungen bei wiederholten Farben.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Keine Instanzattribute | – | zustandslos | – | – | – | – | – | Jede Auswertung verwendet nur Parameter und lokale Variablen. |
| evaluate(Color[], Color[]) | public | – | Feedback | secret, guess | Color[], Color[] | Markenergebnis | Feedback | Berechnet zuerst schwarze und danach weisse Marken ohne Positionszuordnung. |
| validateCode(Color[], String) | private | static | void | code, context | Color[], String | – | – | Prüft Codelänge und nicht null Elemente vor jeder Auswertung. |

### 5.7 Feedback

Feedback ist eine konkrete, unveränderliche Java-Wertklasse. Sie ist kein Interface und enthält nur das Ergebnis einer Rückmeldeberechnung. Ein Feedback-Objekt wird nach der Erstellung nicht verändert und kann deshalb sicher in Historien gespeichert sowie von TurnResult weitergegeben werden.

| Name | Sichtbarkeit | Andere Attribute | Typ | Input | Input-Typ | Output | Output-Typ | Präzise Aufgabe |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| blackMarks | private | final | int | – | – | – | – | Anzahl richtiger Farben an richtiger Position; 0 bis CODE_LENGTH. |
| whiteMarks | private | final | int | – | – | – | – | Zusätzliche richtige Farben an falscher Position; zusammen mit blackMarks höchstens CODE_LENGTH. |
| Feedback(int, int) | public | – | Konstruktor | blackMarks, whiteMarks | int, int | – | – | Prüft nicht negative Werte und die maximale Gesamtzahl. |
| getBlackMarks() | public | – | int | – | – | schwarze Marken | int | Liefert die unveränderliche schwarze Anzahl. |
| getWhiteMarks() | public | – | int | – | – | weisse Marken | int | Liefert die unveränderliche weisse Anzahl. |

### 5.8 TurnResult

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

Die Abhängigkeiten verlaufen von der Darstellung zur Fachlogik; keine Fachklasse hängt von Scanner, PrintStream oder ANSI-Ausgabe ab.

~~~text
Main → ConsoleUI → Game → CodeGenerator
                    └──→ FeedbackEvaluator → Feedback
ConsoleUI ← TurnResult ← Game
ConsoleUI → Color → Ansi
ConsoleUI → Ansi
~~~

Main verdrahtet die konkreten Klassen. ConsoleUI erstellt und bedient Game, verwendet Color zur Eingabeumrechnung und Ansi nur für Texte. Game verwendet CodeGenerator und FeedbackEvaluator. FeedbackEvaluator liefert Feedback, während TurnResult eine bereits abgeschlossene einzelne Tippverarbeitung transportiert. Diese Richtung verhindert, dass Regeln an die Konsole gekoppelt werden.

## 7. Fehlerbehandlung und Zustandsgrenzen

Eingabefehler des Spielers sind erwartete Bedienfälle. ConsoleUI behandelt leere Eingaben, Text statt Zahl, Dezimalzahlen, falsche Anzahl Werte und Zahlen ausserhalb von 1 bis 6 mit einer konkreten Meldung. Diese Fälle rufen submitGuess nicht auf und verändern weder attemptsUsed noch Historie noch GameStatus.

Fehlerhafte Aufrufe innerhalb des Programms sind davon getrennt. Game und FeedbackEvaluator weisen Arrays mit falscher Länge oder null-Elementen mit IllegalArgumentException ab. Game weist einen Tipp nach WON oder LOST mit IllegalStateException ab. Diese Fehler zeigen Programmierfehler früh an, statt einen ungültigen Spielzustand zu speichern.

Der Geheimcode wird während ONGOING nie ausgegeben. Nach WON oder LOST wird kein weiterer Tipp angenommen. Eine neue Runde bedeutet stets eine neue Game-Instanz und damit einen vollständig neuen Geheimcode, Zähler, Status und Historie.

## 8. Geplante Code- und Testdateien

Alle Produktionsdateien liegen im gleichen Paket unter src/main/java/<package>/; die zugehörigen JUnit-Dateien liegen mit gleichem package-Statement unter src/test/java/<package>/. Es sind keine Ressourcen- oder Konfigurationsdateien vorgesehen.

| Produktionsdatei | Enthält | Verantwortlichkeit |
| --- | --- | --- |
| Main.java | finale Klasse Main | Startet die Anwendung und verdrahtet Abhängigkeiten. |
| Color.java | Enum Color | Definiert die sechs Spielfarben und ihre Darstellungseigenschaften. |
| GameStatus.java | Enum GameStatus | Definiert ONGOING, WON und LOST. |
| Ansi.java | finale Klasse Ansi | Zentralisiert ANSI-Sequenzen und colour. |
| CodeGenerator.java | Klasse CodeGenerator | Erzeugt zufällige Geheimcodes. |
| FeedbackEvaluator.java | Klasse FeedbackEvaluator | Berechnet schwarze und weisse Marken. |
| Feedback.java | unveränderliche Klasse Feedback | Speichert die beiden Markenzahlen. |
| TurnResult.java | unveränderliche Klasse TurnResult | Transportiert das Ergebnis eines Tipps. |
| Game.java | Klasse Game | Besitzt und verarbeitet genau eine Spielrunde. |
| ConsoleUI.java | Klasse ConsoleUI | Liest, validiert und zeigt den Konsolenablauf. |

| Testdatei | Prüft | Wichtige Fälle |
| --- | --- | --- |
| ColorTest.java | Color | Nummern 1 bis 6, ungültige Nummern, Namen und Sequenzzuordnung. |
| AnsiTest.java | Ansi | Zusammensetzen von Farbsequenz, Text und genau einem RESET; null-Argumente. |
| CodeGeneratorTest.java | CodeGenerator | Länge vier, bekannte Farben, reproduzierbarer Zufall. |
| FeedbackEvaluatorTest.java | FeedbackEvaluator | Volltreffer, Vertauschung, keine Treffer, schwarze vor weissen Marken, Duplikate. |
| GameTest.java | Game | Anfangszustand, Sieg, siebte Niederlage, Historie, Kopien und abgelehnte Tipps. |
| ConsoleUITest.java | ConsoleUI | Ungültige Eingaben zählen nicht, Neustart, kontrolliertes Eingabeende und erzeugte ANSI-Zeichenketten. |

## 9. Umsetzungsreihenfolge

1. Paketstruktur und die einfachen Werttypen Ansi, Color, GameStatus, Feedback und TurnResult anlegen.
2. FeedbackEvaluator mit dem zweiphasigen Algorithmus implementieren und dessen Tests zuerst ausführen.
3. CodeGenerator mit injizierbarem Random implementieren und auf Form des Codes testen.
4. Game mit defensiven Kopien, Historie, Statuswechseln und beiden Konstruktoren implementieren.
5. ConsoleUI mit injizierbarer Ein- und Ausgabe, Validierung, Rundenablauf und Neustartfrage implementieren.
6. ANSI-Darstellung ausschliesslich über Ansi.colour in ConsoleUI hinzufügen und im Zielterminal prüfen.
7. Main verdrahten und vollständige Spielrunden manuell ausführen.
8. Alle JUnit-Tests und die manuellen Konsolenszenarien erneut ausführen; Fehler zuerst in Fachlogik, danach in Darstellung korrigieren.

## 10. Testkonzept

Unit-Tests verwenden bekannte Color-Arrays und kontrollierte Zufallsquellen; sie verlassen sich nie auf echten Zufall oder auf die tatsächliche Bildschirmfarbe eines Terminals. ANSI-Tests vergleichen erzeugte Zeichenketten, nicht die visuelle Darstellung. Testnamen beschreiben beobachtbares Verhalten, beispielsweise returnsBlackMarkForCorrectColourAndPosition.

| Bereich | Testfall | Erwartung |
| --- | --- | --- |
| Color | Gültige und ungültige Nummer | 1 bis 6 liefern den passenden Enum-Wert; andere Werte liefern ein leeres Optional. |
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

Manuell wird die Anwendung im vorgesehenen Terminal getestet: leere Zeile, Text, drei oder fünf Zahlen, 0, 7 und Dezimalzahlen; Sieg im ersten und siebten Versuch; sieben falsche Tipps; j, J, n, N und ungültige Neustartantwort; neue Runde mit zurückgesetzter Historie; farbige Legende, Tipps, Gewinn, Verlust und Rückmeldung. Dabei wird kontrolliert, dass jede Aussage auch ohne Farbe durch Text und Zahlen verständlich bleibt.

## 11. README

Die README dokumentiert die tatsächlich umgesetzte Anwendung: benötigtes JDK, Kompilier- und Startbefehl, Farblegende, Eingabeformat, Bedeutung schwarzer und weisser Marken sowie die Ausführung der Tests in IntelliJ. Sie weist darauf hin, dass ANSI-Farben in einer ANSI-fähigen Konsole am besten dargestellt werden. Dokumentiert werden nur vorhandene Klassen, Befehle und Funktionen.
