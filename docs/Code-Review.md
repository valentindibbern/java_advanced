# Kritische Code-Review – Mastermind

**Stand:** 15. Juli 2026  
**Umfang:** Vollständiger Produktionscode unter `src/main/java/` (13 Java-Dateien, rund 700 Zeilen). Testquellcode wurde bewusst nicht fachlich bewertet. `README.md` und `pom.xml` wurden nur als Build- und Betriebs-Kontext gelesen.

## Kurzurteil

Der Code hat eine für die Projektgrösse gute, verständliche Grundstruktur. Die zentrale Fachlogik ist sauber von der Konsolen- und Swing-Bedienung getrennt, und die Bewertung doppelter Farben folgt dem korrekten zweiphasigen Mastermind-Verfahren. Auch die defensiven Kopien an mehreren Array-Grenzen sind positiv.

Es bestehen keine erkennbaren Fehler im normalen Spielablauf mit dem eingebauten Zufallsgenerator. Die wichtigsten Schwachstellen liegen in der unvollständig abgesicherten Grenze zwischen `Game` und dem injizierbaren `CodeGenerator`, der Vermischung von Fachmodell und Präsentation sowie einigen unpräzisen beziehungsweise wenig robusten Schnittstellen. Vor einer Erweiterung oder einer externen Nutzung des Modells sollten die Befunde mit hoher Priorität bereinigt werden.

## Befunde nach Priorität

### P1 – Geheimcode wird beim Spielstart nicht validiert oder defensiv kopiert

**Fundstelle:** `Game.java:31-39` (`startNewRound`)

`Game` übernimmt die von `codeGenerator.generateCode()` gelieferte Array-Referenz direkt. Anders als ein Spielertipp wird sie weder mit `CodeValidator.validateCode(...)` geprüft noch kopiert.

Das ist eine echte Verletzung der sonst gut umgesetzten Kapselung:

* Ein alternativer oder testbarer Generator kann `null`, ein zu kurzes Array oder `null`-Farben liefern. Der Fehler erscheint erst beim nächsten Tipp tief im `FeedbackEvaluator` und nicht an der Ursache beim Rundenstart.
* Gibt ein Generator dieselbe Array-Referenz nach aussen weiter oder verändert sie später, kann sich der Geheimcode während einer laufenden Runde ändern. Der Spieler würde dann gegen einen anderen Code spielen als beim Start erzeugt wurde.

**Empfehlung:** Ergebnis lokal entgegennehmen, sofort mit dem Kontext `"Geheimcode"` validieren und danach mit `Arrays.copyOf` in `secretCode` übernehmen. Alternativ sollte ein kleines `CodeSource`-Interface statt einer vererbbaren konkreten Klasse verwendet werden. Damit wird die bereits gewünschte deterministische Injektion klar und sicher modelliert.

### P1 – Das Fachmodell ist an beide Oberflächentechnologien gekoppelt

**Fundstelle:** `Color.java:4-20, 41-46`

Das enum `Color` enthält gleichzeitig fachliche Identität, deutsche Anzeigezeichenkette, ANSI-Escape-Sequenz und `java.awt.Color`. Damit hängt der gesamte Kern (`Game`, `FeedbackEvaluator`, `CodeGenerator`) transitiv von AWT ab, obwohl die Fachlogik weder Swing noch ANSI braucht.

Folgen:

* Eine künftige Web-, JavaFX- oder reine Server-Anwendung muss Präsentationsdetails im Domänenenum mittragen oder ändern.
* Farbgebung und Texte lassen sich nicht getrennt lokalisieren, thematisieren oder für Barrierefreiheit anpassen.
* In einer headless Umgebung wird unnötig `java.desktop` als Abhängigkeit mitgeführt.

**Empfehlung:** `Color` auf eine stabile Fachidentität (und allenfalls die Eingabenummer) beschränken. ANSI-Formatierung nach `ConsoleUI` und AWT-Farben nach Swing-Komponenten beziehungsweise einen `SwingColorPalette`-Adapter verschieben. Die deutschen Namen gehören ebenfalls in die jeweilige Präsentationsschicht oder einen bewusst benannten Lokalisierungsadapter.

### P2 – Öffentliche, vererbbare Klassen ohne dokumentierten Erweiterungsvertrag

**Fundstellen:** `CodeGenerator.java:6`, `FeedbackEvaluator.java:3`, `ConsoleUI.java:7`, `Game.java:6`

Diese Klassen sind öffentlich und nicht `final`, ihre Methoden sind überschreibbar. Der Code behandelt sie jedoch als feste Implementierungen und definiert keine Invarianten für Unterklassen. Der P1-Befund wird gerade dadurch wahrscheinlicher: Ein überschriebenes `generateCode()` kann die erwarteten Garantien brechen.

**Empfehlung:** Klassen, die nicht ausdrücklich Erweiterungspunkte sind, als `final` deklarieren. Für die wenigen echten Erweiterungspunkte kleine Interfaces mit klarer Vor- und Nachbedingung verwenden, zum Beispiel `SecretCodeProvider`. Das macht die Abhängigkeitsinjektion für Zufall und Tests explizit und verhindert fragile Vererbung.

### P2 – `getGuessHistory()` hat eine irreführende und inkonsistente Leerstellen-Semantik

**Fundstelle:** `Game.java:75-88`

`guessHistory` wird als zweidimensionales, vollständig angelegtes Array erzeugt. Deshalb ist jede Zeile stets nicht `null`, auch wenn noch kein Tipp abgegeben wurde; `startNewRound()` füllt lediglich deren Elemente mit `null`. Im Getter ist der Test `if (guessHistory[index] != null)` somit immer wahr. Der Aufrufer erhält für nicht verwendete Runden vierstellige Arrays mit `null`-Einträgen statt klarer leerer Zeilen (`null`) oder nur tatsächlich gespielter Tipps.

Das ist nicht unmittelbar falsch, aber der API-Vertrag ist schwer lesbar und die Implementierung suggeriert eine andere Struktur als tatsächlich verwendet wird. Konsumenten müssen spezielle `null`-Einträge interpretieren; eine versehentliche Weitergabe an `CodeValidator` wirft dann eine Ausnahme.

**Empfehlung:** Einen eindeutigen Vertrag wählen und dokumentieren:

* Entweder nur die ersten `attemptsUsed` Tipps zurückgeben (am klarsten für eine öffentliche API),
* oder noch nicht verwendete Zeilen explizit `null` lassen und genauso zurückgeben,
* oder eine unveränderliche Liste von `TurnResult` statt paralleler Arrays bereitstellen.

Die parallelen Arrays `guessHistory` und `feedbackHistory` sollten langfristig durch eine Sammlung von `TurnResult` ersetzt werden, damit ein Tipp und seine Rückmeldung nicht auseinanderlaufen können.

### P2 – Öffentliche Wertobjekte haben keine wertbasierte Gleichheit

**Fundstellen:** `Feedback.java:3-30`, `TurnResult.java:6-46`

`Feedback` und `TurnResult` sind unveränderliche Datenobjekte, verhalten sich bei `equals` jedoch wie Objektidentitäten. Zwei fachlich gleiche Rückmeldungen oder Zugergebnisse sind also nicht gleich. Das erschwert Vergleiche in UI, Protokollierung und künftigen Clients unnötig.

**Empfehlung:** Als Java-21-`record` modellieren, sofern API-Änderungen akzeptabel sind, oder konsistente Implementierungen von `equals`, `hashCode` und `toString` ergänzen. Bei `TurnResult` muss der Arraybestandteil dabei defensiv kopiert bleiben; ein Record allein löst das nicht.

### P3 – Spielzustand wird in der Swing-Oberfläche aus mehreren Quellen rekonstruiert

**Fundstellen:** `MastermindPanel.java:131-181`, `185-222`

Das Panel führt mit `selectedGuess` und `selectedCount` einen eigenen, temporären Zustand neben dem `Game`-Zustand. Das ist für die Bearbeitung eines noch nicht abgegebenen Tipps legitim, aber das Panel ist dadurch stark an die interne Zählweise des Spiels gekoppelt (`guessPegs[game.getAttemptsUsed()]`, `result.getAttemptNumber() - 1`). Die Logik für Rücksetzen, Status, Steuerelemente und Ende einer Runde verteilt sich über mehrere Methoden.

Heute bleibt dies im normalen Ablauf konsistent. Bei Undo, gespeicherten Spielen, alternativen Eingaben oder asynchronen Ereignissen wird es jedoch fehleranfällig.

**Empfehlung:** Einen kleinen UI-spezifischen Entwurf als eigenes Objekt kapseln (z. B. `CurrentGuess`) und die Anzeige nach jedem Spielereignis zentral aus einem Modell-Snapshot ableiten. Zusätzlich sollten die direkten Indexberechnungen in benannte Hilfsmethoden ausgelagert werden.

### P3 – Fehlende Validierung bei UI-Komponenten und fragiles Zeichnen bei sehr kleiner Grösse

**Fundstellen:** `PegView.java:13-23, 49-73`, `FeedbackView.java:24-35`

`PegView.showColor(null)` führt zu einer `NullPointerException` beim Tooltip statt zu einer klaren Eingabevalidierung. In `paintComponent` kann `diameter` negativ werden, wenn die Komponente kleiner als sechs Pixel breit oder hoch gerendert wird; Swing respektiert Mindestgrössen nicht in jeder Einbettung garantiert. `FeedbackView.showFeedback(null)` hat dieselbe unklare NPE-Eigenschaft.

**Empfehlung:** Öffentliche beziehungsweise paketweite Übergänge mit `Objects.requireNonNull` absichern und den Durchmesser auf mindestens `0` begrenzen. Für das aktuelle feste Fenster ist die Auswirkung gering, für wiederverwendbare Komponenten aber sinnvoll.

### P3 – Kommandozeilenargumente werden stillschweigend ignoriert

**Fundstelle:** `Main.java:23-43`

Nur `--gui` und `--no-color` werden gesucht. Unbekannte Optionen (etwa ein Tippfehler wie `--gUi` oder `--no-colour`) starten kommentarlos den Konsolenmodus. Bei einer kleinen Anwendung ist dies kein Sicherheitsproblem, führt aber zu unnötig schwer nachvollziehbarem Verhalten.

**Empfehlung:** Argumente einmal zentral parsen, unbekannte Optionen mit deutscher Fehlermeldung und Nutzungshinweis zurückweisen und bei Bedarf `--help` anbieten. Dabei auch die doppelte Iteration über `args` vermeiden.

### P3 – Eingabeende und Rundenabbruch sind für die Konsole nicht sichtbar

**Fundstellen:** `ConsoleUI.java:33-44, 47-59, 62-109`

Bei EOF während eines Tipps oder beim Neustart kehrt das Programm ohne Meldung zurück. Technisch ist das korrekt und verhindert eine Endlosschleife, aber aus Sicht eines interaktiven Benutzers wirkt es wie ein abruptes Ende. Das gilt besonders beim versehentlichen Senden von EOF.

**Empfehlung:** Vor dem geordneten Ende eine knappe Nachricht wie `"Eingabe beendet. Auf Wiedersehen."` ausgeben. Optional `Scanner` nicht schliessen, solange er `System.in` umschliesst, was der aktuelle Code bereits korrekt vermeidet.

### P3 – Nummern- und Grössenwissen ist mehrfach indirekt im Code verteilt

**Fundstellen:** `ConsoleUI.java:83, 86`, `Color.java:4-9`, `Feedback.java:13`, `FeedbackView.java:29-31`, `MastermindPanel.java:31-33`

Die Werte `0 bis 5`, vier Positionen und sieben Versuche sind teilweise über `Color.values()`, teilweise über `Game`-Konstanten und teilweise als Text oder Layoutannahme kodiert. Besonders die Fehlermeldungen der Konsole sind an exakt sechs Farben gebunden. Bei einer Regeländerung entstehen leicht widersprüchliche Texte oder Darstellungen.

**Empfehlung:** Farbenbereich aus `Color.values()` herleiten (für Texte z. B. erste und letzte Nummer), die Spielregeln in einer klaren Konfiguration oder mindestens in einer zentralen Regelklasse bündeln und UI-Layoutkonstanten als benannte Konstanten ausweisen.

### P4 – Stil- und Wartbarkeitsdetails

* **`MastermindPanel.java:245`:** `getSecretPeg` steht entgegen dem vereinbarten Vier-Leerzeichen-Stil in einer Zeile. Klein, aber sichtbar inkonsistent.
* **Mehrere Dateien, z. B. `Game.java:13`, `FeedbackEvaluator.java:12`, `ConsoleUI.java:70`:** Code-Kommentare sind Deutsch. Die Repository-Vorgabe verlangt ausdrücklich englische Java-Kommentare. Die Konsolentexte sind dagegen korrekt Deutsch.
* **`FeedbackView.java:28-44` und `PegView.java:58-71`:** Hart kodierte Farben, Abstände, Durchmesser und Schriftpositionen erschweren konsistentes Theming und Skalierung. Für die kleine GUI vertretbar, aber bei Änderungen sollten sie mindestens benannte Konstanten sein.
* **`CodeValidator.java:7`:** Der frei übergebene Kontextstring kann selbst `null` sein und erzeugt dann eine unprofessionelle Fehlermeldung. Ein Enum oder feste Methoden (`validateSecretCode`, `validateGuess`) wäre typsicherer.
* **`FeedbackEvaluator.java:9-34`:** Die Laufzeit ist bei vier Stellen trivial. Für Verständlichkeit ist der Algorithmus gut. Eine frequenzbasierte zweite Phase wäre bei variabler Codelänge kompakter, aber kein notwendiger Umbau.

## Positiv bewertete Aspekte

* **Korrekte Doppelwertungslogik:** `FeedbackEvaluator.java:12-34` reserviert zuerst exakte Treffer und ordnet danach nur noch unbenutzte Farben zu. Damit werden bei Wiederholungen weder schwarze noch weisse Marken doppelt vergeben.
* **Saubere Zustandsübergänge:** `Game.submitGuess` erlaubt nur laufende Runden, zählt erst nach einer gültigen Bewertung hoch und setzt den Gewinn vor der Sieben-Versuche-Niederlage. Das entspricht den Spielregeln.
* **Sinnvolle defensive Kopien:** `Game.submitGuess`, `Game.revealSecretCode`, `Game.getGuessHistory`, `Game.getFeedbackHistory` und `TurnResult` schützen wesentliche Arrays vor externer Mutation. Der fehlende Schutz des Generatorergebnisses ist deshalb besonders klar begrenzt und leicht zu beheben.
* **Gut geführte Konsoleneingabe:** Leere Eingaben, falsche Anzahl Werte, Nichtzahlen und ungültige Farbnummern werden unterschieden und zählen nicht als Versuch.
* **Swing-Thread-Einstieg korrekt:** `Main` erzeugt und zeigt das Fenster über `SwingUtilities.invokeLater`.
* **GUI-Interaktion ist begrenzt:** Farbwahl, Löschen und Abgeben prüfen sowohl die lokale Auswahl als auch den Spielstatus. Die Rückmeldungsmarken sind aufgrund der dunklen Umrandung der weissen Marke optisch unterscheidbar.

## Empfohlene Reihenfolge für die Weiterverarbeitung

1. **Zuerst P1 beheben:** Generatorvertrag absichern und Präsentationsmetadaten aus `Color` trennen. Danach ist der Kern stabil und unabhängig von der UI.
2. **Dann P2 bereinigen:** Öffentliche Vererbungsflächen schliessen oder ausdrücklich als Interfaces modellieren; Verlauf als zusammenhängende Zugdaten modellieren; Wertsemantik ergänzen.
3. **Danach P3 als gezielte Wartbarkeitsarbeit:** Argumentparser, UI-Entwurfsobjekt, Robustheit der Swing-Komponenten und abgeleitete Regeltexte verbessern.
4. **Zum Schluss P4 vereinheitlichen:** Stil, Kommentarsprachen und benannte Layoutkonstanten bereinigen.

## Verifikation und Grenzen der Review

* Der Produktionscode wurde vollständig gelesen; Testquellcode wurde nicht bewertet.
* `./mvnw.cmd -q compile` war erfolgreich. Damit ist der aktuell vorhandene Produktionscode kompilierbar.
* `./mvnw.cmd -q -DskipTests package` schlug beim **Kompilieren der vorhandenen Tests** mit zahlreichen `cannot find symbol`-Fehlern fehl. `-DskipTests` überspringt nur die Ausführung, nicht die Testkompilierung. Dieser Buildzustand ist ein wichtiger Projektbefund, aber keine Aussage über die Qualität des bewusst ausgeschlossenen Testcodes.
* Der Arbeitsbaum enthielt vor dieser Review bereits Änderungen und Löschungen. Ausser dieser Review-Datei wurde kein bestehender Projektcode verändert.
