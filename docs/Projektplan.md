# Projektplan - Mastermind

## 1. Auftrag, Ziel und Abgrenzung

Im Schnupperpraktikum wird innerhalb von zwei Arbeitstagen ein einfaches Mastermind-Spiel als Java-Kommandozeilenprogramm entwickelt. Der Spieler versucht einen verdeckten Code aus vier Farben zu erraten. Der Code wird aus sechs Farben zufällig erstellt; Farben dürfen mehrfach vorkommen. Nach jedem gültigen Tipp zeigt das Programm die Anzahl schwarzer Marken für Farbe und Position richtig sowie weisser Marken für Farbe richtig, Position falsch. Nach spätestens sieben gültigen Tipps gewinnt oder verliert der Spieler. Danach kann eine neue Runde gestartet werden.

Das Hauptziel ist ein zuverlässiges und gut erklärbares Pflichtprogramm. Es bleibt bewusst klein, wird aber in klar getrennte Teile aufgebaut. Dadurch sollen spätere Erweiterungen - etwa andere Schwierigkeitsstufen, eine grafische Oberfläche oder mehr Farben - möglich sein, ohne die Spielregeln umzubauen.

Nicht Teil dieser Version sind eine grafische Oberfläche, eine Bestenliste, Benutzerkonten, Dateispeicherung, verschiedene Schwierigkeitsstufen oder Netzwerkfunktionen. Solche Ideen sind nur als mögliche Weiterentwicklungen dokumentiert.

## 2. Arbeitsmethode: IPERKA

IPERKA gliedert die Arbeit in Informieren, Planen, Entscheiden, Realisieren, Kontrollieren und Auswerten. Der Ablauf hilft dabei, die Entwicklung nicht nur umzusetzen, sondern begründet und nachvollziehbar zu dokumentieren.

### 2.1 Informieren

Zu Beginn werden die Aufgabenstellung, die Spielregeln und die Repository-Vorgaben gelesen. Daraus werden die Pflichtanforderungen in eine Abnahmecheckliste übertragen. Wichtig sind insbesondere die vier Codepositionen, sechs Farben, erlaubte Wiederholungen, sieben Versuche, schwarze und weisse Marken sowie die Spielzustände.

Weiter wird die vorhandene Arbeitsumgebung geprüft: Windows-PC, IntelliJ IDEA, installiertes JDK und die vorhandene Projektstruktur. Unklarheiten zur Abgabe werden früh bei der Betreuungsperson angesprochen. Falls keine Vorlage verlangt wird, gehört eine kurze README zur Abgabe.

**Ergebnis:** Der Auftrag, die Rahmenbedingungen und die Erfolgskriterien sind verstanden.

### 2.2 Planen

Die Arbeit wird in zwei Tage mit klaren Zwischenzielen aufgeteilt. Die beiden 20-Minuten-Pausen bleiben flexibel; die Mittagspause ist von 12:00 bis 13:00 Uhr. Pro Tag stehen dadurch 6 Stunden und 20 Minuten Nettoarbeitszeit zur Verfügung, insgesamt 12 Stunden und 40 Minuten.

Die Fachlogik wird von der Konsoleneingabe getrennt. Der Aufbau verwendet kleine Klassen mit je einer klaren Aufgabe und zentrale Konstanten für Farben, Codelänge und maximale Versuchszahl. Damit bleibt die Anwendung für diese Aufgabe einfach, ist aber erweiterbar.

**Ergebnis:** Ablauf, Ressourcen, Prioritäten, Risiken und Meilensteine sind festgelegt.

### 2.3 Entscheiden

Folgende Entscheidungen gelten für die Pflichtversion:

- Die Anwendung ist ein Konsolenprogramm; eine GUI ist kein Ziel dieser zwei Tage.
- Farben werden als vier durch Leerzeichen getrennte Nummern `1` bis `6` eingegeben. Die Konsole zeigt vorher eine eindeutige Legende.
- Die Farben heissen Rot, Grün, Blau, Gelb, Orange und Violett.
- Nach einem Spielende wird eine neue Runde mit `j` oder `n` angeboten.
- Es wird kein Maven oder Gradle eingeführt. Das Projekt wird in IntelliJ kompiliert; JUnit-Tests werden dort ausgeführt.
- Die Farben werden als `enum` modelliert. Die Spielhistorie verwendet bewusst ein zweidimensionales Array.
- Der Algorithmus für doppelte Farben wird unabhängig von der Konsolenausgabe implementiert und getestet.

**Ergebnis:** Der Umfang und der technische Weg sind verbindlich entschieden.

### 2.4 Realisieren

Die Umsetzung folgt dem technischen Implementierungsplan. Zuerst werden die Datenmodelle, Codeerzeugung und Spielzustand erstellt. Danach folgen Eingabe, Rückmeldelogik und die Darstellung in der Konsole. So bleibt die Kernlogik auch später ohne grosse Änderungen für eine GUI nutzbar.

Nach jedem wesentlichen Arbeitspaket wird das Programm kompiliert und mit mindestens einem passenden Beispiel ausprobiert. Kommentare werden nur für nicht offensichtliche Logik geschrieben, insbesondere für die Berechnung bei doppelten Farben.

**Ergebnis:** Eine spielbare, strukturierte und kommentierte Anwendung entsteht schrittweise.

### 2.5 Kontrollieren

Die Umsetzung wird gegen die Abnahmecheckliste kontrolliert. Die Rückmeldelogik wird mit JUnit getestet; der komplette Ablauf wird zusätzlich manuell in der Konsole geprüft. Die Betreuungsperson prüft den Stand heute und morgen jeweils um 15:00 Uhr.

Vor der Abgabe werden Projekt, Tests, README und ZIP-Inhalt kontrolliert. Insbesondere dürfen keine generierten Dateien wie `.class`-Dateien oder `out/` enthalten sein.

**Ergebnis:** Die Pflichtanforderungen sind durch Tests und einen manuellen Durchlauf belegt.

### 2.6 Auswerten

Am Ende wird kurz reflektiert, welche Ziele erreicht wurden, welche Schwierigkeiten auftraten und wie sie gelöst wurden. Für das Fachgespräch wird vorbereitet, warum Arrays verwendet werden, wie das zweidimensionale Array funktioniert und weshalb schwarze Marken vor weissen Marken berechnet werden müssen.

Mögliche Erweiterungen werden festgehalten, aber nicht mehr umgesetzt: andere Codelängen, mehr Farben, Schwierigkeitsstufen, grafische Oberfläche, Statistik oder Speicherung.

**Ergebnis:** Das Vorgehen, die technischen Entscheide und die Lernpunkte können fachlich erklärt werden.

## 3. Zeit- und Meilensteinplan

| Tag und Arbeitsblock | Schwerpunkt | Ergebnis / Meilenstein |
| --- | --- | --- |
| Tag 1, Vormittag | Auftrag und Umfeld prüfen, Anforderungen erfassen, Struktur festlegen, Projekt starten | Startfähiger Grundbau und klare Abnahmecheckliste |
| Tag 1, Nachmittag | Konsoleneingabe, Spielablauf und erster technischer Stand; Betreuungscheck um 15:00 Uhr | Spielbarer Rohbau mit dokumentierten offenen Punkten |
| Tag 2, Vormittag | Rückmeldelogik, Spielende und automatisierte Tests fertigstellen | Vollständige Pflichtfunktion |
| Tag 2, Nachmittag | Manuelle Tests, Fehlerbehebung, README, Betreuungscheck um 15:00 Uhr und Abgabe | Getestete, dokumentierte ZIP-Datei |

Die genaue Reihenfolge einzelner Methoden steht im Implementierungsplan. Dieser Projektplan beschreibt absichtlich nur die Arbeitsblöcke, ihre Ziele und ihre Ergebnisse.

## 4. Ressourcen und Verantwortlichkeiten

| Ressource | Verwendung | Verantwortlich |
| --- | --- | --- |
| Windows-PC mit JDK | Entwickeln, Kompilieren und Starten | Praktikant |
| IntelliJ IDEA | Projektverwaltung, Debugging und Ausführen der JUnit-Tests | Praktikant |
| Aufgabenstellung und Spielregeln | Verbindliche Anforderungen und Testfälle | Praktikant |
| Betreuungsperson | Rückfragen und zwei kurze Zwischenchecks | Betreuungsperson / Praktikant |
| README und ZIP-Datei | Bedienungsanleitung und Abgabe | Praktikant |

Es handelt sich um Einzelarbeit. Der Praktikant plant, programmiert, testet, dokumentiert und erstellt die ZIP-Datei selbst. Die Betreuungsperson gibt Feedback, übernimmt aber keine Programmieraufgaben.

## 5. Abnahmecheckliste

- [ ] Der Geheimcode besitzt genau vier Positionen.
- [ ] Er wird zufällig aus genau sechs Farben erzeugt.
- [ ] Farben dürfen im Geheimcode und im Tipp mehrfach vorkommen.
- [ ] Ein Tipp besteht aus genau vier gültigen Farben.
- [ ] Ungültige Eingaben werden erklärt und zählen nicht als Versuch.
- [ ] Ein schwarzer Treffer bedeutet richtige Farbe an richtiger Position.
- [ ] Ein weisser Treffer bedeutet richtige Farbe an falscher Position.
- [ ] Bei doppelten Farben wird keine Farbe mehrfach bewertet.
- [ ] Es gibt höchstens sieben gültige Versuche.
- [ ] Das Programm unterscheidet `ONGOING`, `WON` und `LOST`.
- [ ] Bei Sieg und Niederlage wird die Runde beendet und der Geheimcode angezeigt.
- [ ] Eine neue Runde kann gestartet werden.
- [ ] JUnit-Tests und manuelle Tests wurden durchgeführt.
- [ ] README und ZIP-Datei sind vollständig und enthalten keine generierten Dateien.

## 6. Rückverfolgbarkeit der Anforderungen

| Anforderung aus der Aufgabenstellung | Umsetzung gemäss Implementierungsplan | Nachweis bei der Kontrolle |
| --- | --- | --- |
| Vierstelliger geordneter Farbcode | `CODE_LENGTH = 4`, `Color[] secretCode` | Codegenerator- und Feedbacktests |
| Sechs Farben, Wiederholungen erlaubt | Zentrales `Color`-Enum ohne Ausschluss bereits verwendeter Farben | Generator- und Duplikattests |
| Schwarze und weisse Marken | `FeedbackEvaluator` mit zwei Durchläufen | Exakt-, Vertauschungs- und Duplikattests |
| Sieben Versuche | `MAX_ATTEMPTS = 7`, `attemptsUsed` | Test für Niederlage nach Versuch sieben |
| Laufend, gewonnen und verloren | `GameStatus` mit `ONGOING`, `WON`, `LOST` | Spielzustands- und Konsolentests |
| Kommandozeilenoberfläche | `ConsoleUI` mit Legende, Eingabe und Ausgabe | Manueller End-to-End-Test |
| Umgang mit Arrays erklären | Code, Tipp und Historie als ein- bzw. zweidimensionale Arrays | Fachgespräch und kommentierter Code |

## 7. Risiken und Umgang damit

| Risiko | Auswirkung | Vorgehen |
| --- | --- | --- |
| Duplikate werden falsch bewertet | Kernelement der Aufgabe ist fehlerhaft | Zweiphasigen Algorithmus getrennt implementieren und mit festen Beispielen testen |
| JUnit ist in IntelliJ nicht sofort verfügbar | Automatisierte Tests verzögern sich | Manuelle Testtabelle zuerst ausführen, JUnit danach einrichten |
| Zeit reicht nicht für optische Verbesserungen | Unwichtige Arbeiten bleiben offen | Zuerst Pflichtlogik, Tests, README und Abgabe sichern |
| Betreuungscheck fällt aus | Weniger externes Feedback | Eigene Abnahmecheckliste verwenden und offene Fragen dokumentieren |
| Unklare Abgabeform | Falsche oder unvollständige Abgabe | Früh nachfragen; sonst ZIP mit Quellcode, Tests und README bereitstellen |

## 8. Quellen

- Aufgabenstellung: `docs/AufgabenScann.pdf`
- Spielregeln: `docs/Mastermind.md`
- IPERKA-Grundlage: [BZZ - IPERKA](https://wiki.bzz.ch/doku.php?id=modul%3Aarchiv%3Am431%3Alearningunits%3Alu03%3Alu03c_iperka&rev=1750685906)
