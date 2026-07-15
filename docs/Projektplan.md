# Projektplan - Mastermind

## 1. Auftrag, Ziel und Abgrenzung

Im Schnupperpraktikum wird innerhalb von zwei Arbeitstagen ein einfaches Mastermind-Spiel mit Konsolenmodus und optionaler Swing-GUI entwickelt. Der Spieler versucht einen verdeckten Code aus vier Farben zu erraten. Der Code wird aus sechs Farben zufällig erstellt; Farben dürfen mehrfach vorkommen. Nach jedem gültigen Tipp zeigt das Programm die Anzahl schwarzer Marken für Farbe und Position richtig sowie weisser Marken für Farbe richtig, Position falsch. Nach spätestens sieben gültigen Tipps gewinnt oder verliert der Spieler. Danach kann eine neue Runde gestartet werden.

Das Hauptziel ist ein zuverlässiges und gut erklärbares Pflichtprogramm. Es bleibt bewusst klein und trennt die gemeinsame Fachlogik von Konsole und GUI. Dadurch bleiben spätere Erweiterungen wie andere Schwierigkeitsstufen oder mehr Farben möglich, ohne die Spielregeln umzubauen.

Nicht Teil dieser Version sind eine Bestenliste, Benutzerkonten, Dateispeicherung, verschiedene Schwierigkeitsstufen, Animationen oder Netzwerkfunktionen. Die GUI bleibt bewusst auf ein klassisches Spielbrett ohne zusätzliche Spielfunktionen beschränkt.

## 2. Arbeitsmethode: IPERKA

IPERKA gliedert die Arbeit in Informieren, Planen, Entscheiden, Realisieren, Kontrollieren und Auswerten. Der Ablauf hilft dabei, die Entwicklung nicht nur umzusetzen, sondern begründet und nachvollziehbar zu dokumentieren.

### 2.1 Informieren

Zu Beginn werden die Aufgabenstellung, die Spielregeln und die Repository-Vorgaben gelesen. Daraus werden die Pflichtanforderungen in eine Abnahmecheckliste übertragen. Wichtig sind insbesondere die vier Codepositionen, sechs Farben, erlaubte Wiederholungen, sieben Versuche, schwarze und weisse Marken sowie die Spielzustände.

Weiter wird die vorhandene Arbeitsumgebung geprüft: Windows-PC, IntelliJ IDEA, installiertes JDK und die vorhandene Projektstruktur. Unklarheiten zur Abgabe werden früh bei der Betreuungsperson angesprochen. Falls keine Vorlage verlangt wird, gehört eine kurze README zur Abgabe.

**Ergebnis:** Der Auftrag, die Rahmenbedingungen und die Erfolgskriterien sind verstanden.

### 2.2 Planen

Die Arbeit wird in zwei Tage mit klaren Zwischenzielen aufgeteilt. Die beiden 20-Minuten-Pausen bleiben flexibel; die Mittagspause ist von 12:00 bis 13:00 Uhr. Pro Tag stehen dadurch 6 Stunden und 20 Minuten Nettoarbeitszeit zur Verfügung, insgesamt 12 Stunden und 40 Minuten.

Die Fachlogik wird von beiden Oberflächen getrennt. `Game` kapselt den Rundenablauf für Konsole und GUI, einschliesslich eines vollständig zurückgesetzten Neustarts. Der Aufbau verwendet kleine Klassen mit je einer klaren Aufgabe und zentrale Konstanten für Farben, Codelänge und maximale Versuchszahl.

**Ergebnis:** Ablauf, Ressourcen, Prioritäten, Risiken und Meilensteine sind festgelegt.

### 2.3 Entscheiden

Folgende Entscheidungen gelten für die Pflichtversion:

- Die Konsole bleibt der Standardmodus. Das Flag `--gui` startet eine einfache Swing-GUI; Swing benötigt mit JDK 21 keine zusätzlichen Abhängigkeiten.
- Farben werden als vier durch Leerzeichen getrennte Nummern `0` bis `5` eingegeben. Die Konsole zeigt vorher eine eindeutige Legende.
- Die Farben heissen Rot, Grün, Blau, Gelb, Orange und Violett.
- Nach einem Spielende wird eine neue Runde mit `j` oder `n` angeboten.
- Maven wird mit Wrapper eingeführt. Das Projekt wird mit `.\mvnw.cmd test` kompiliert und getestet; IntelliJ bleibt nur ein mögliches Entwicklungswerkzeug.
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

Mögliche Erweiterungen werden festgehalten, aber nicht mehr umgesetzt: andere Codelängen, mehr Farben, Schwierigkeitsstufen, Statistik oder Speicherung.

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
| IntelliJ IDEA | Projektverwaltung und Debugging | Praktikant |
| Maven Wrapper | Reproduzierbares Kompilieren und Ausführen der JUnit-Tests | Praktikant |
| Aufgabenstellung und Spielregeln | Verbindliche Anforderungen und Testfälle | Praktikant |
| Betreuungsperson | Rückfragen und zwei kurze Zwischenchecks | Betreuungsperson / Praktikant |
| README und ZIP-Datei | Bedienungsanleitung und Abgabe | Praktikant |

Es handelt sich um Einzelarbeit. Der Praktikant plant, programmiert, testet, dokumentiert und erstellt die ZIP-Datei selbst. Die Betreuungsperson gibt Feedback, übernimmt aber keine Programmieraufgaben.

## 5. Abnahmecheckliste

- [x] Der Geheimcode besitzt genau vier Positionen.
- [x] Er wird zufällig aus genau sechs Farben erzeugt.
- [x] Farben dürfen im Geheimcode und im Tipp mehrfach vorkommen.
- [x] Ein Tipp besteht aus genau vier gültigen Farben.
- [x] Ungültige Eingaben werden erklärt und zählen nicht als Versuch.
- [x] Ein schwarzer Treffer bedeutet richtige Farbe an richtiger Position.
- [x] Ein weisser Treffer bedeutet richtige Farbe an falscher Position.
- [x] Bei doppelten Farben wird keine Farbe mehrfach bewertet.
- [x] Es gibt höchstens sieben gültige Versuche.
- [x] Das Programm unterscheidet `ONGOING`, `WON` und `LOST`.
- [x] Bei Sieg und Niederlage wird die Runde beendet und der Geheimcode angezeigt.
- [x] Eine neue Runde kann gestartet werden.
- [x] JUnit-Tests und manuelle Tests wurden durchgeführt.
- [x] Die Konsolenoberfläche bleibt ohne `--gui` verfügbar.
- [x] Die Swing-GUI startet mit `--gui`, zeigt sieben Tippzeilen und erlaubt Neustarts.
- [x] README ist vollständig und enthält keine generierten Dateien.
- [ ] Die ZIP-Datei wird erst bei der Abgabe erstellt und enthält keine generierten Dateien.

## 6. Rückverfolgbarkeit der Anforderungen

| Anforderung aus der Aufgabenstellung | Umsetzung gemäss Implementierungsplan | Nachweis bei der Kontrolle |
| --- | --- | --- |
| Vierstelliger geordneter Farbcode | `CODE_LENGTH = 4`, `Color[] secretCode` | Codegenerator- und Feedbacktests |
| Sechs Farben, Wiederholungen erlaubt | Zentrales `Color`-Enum ohne Ausschluss bereits verwendeter Farben | Generator- und Duplikattests |
| Schwarze und weisse Marken | `FeedbackEvaluator` mit zwei Durchläufen | Exakt-, Vertauschungs- und Duplikattests |
| Sieben Versuche | `MAX_ATTEMPTS = 7`, `attemptsUsed` | Test für Niederlage nach Versuch sieben |
| Laufend, gewonnen und verloren | `GameStatus` mit `ONGOING`, `WON`, `LOST` | Spielzustands- und Konsolentests |
| Konsolenoberfläche | `ConsoleUI` mit Legende, Eingabe und Ausgabe | Manueller End-to-End-Test |
| Optionale grafische Oberfläche | `MastermindPanel` und `MastermindFrame`, gestartet mit `--gui` | GUI-Tests und manueller End-to-End-Test |
| Umgang mit Arrays erklären | Code, Tipp und Historie als ein- bzw. zweidimensionale Arrays | Fachgespräch und kommentierter Code |

## 7. Risiken und Umgang damit

| Risiko | Auswirkung | Vorgehen |
| --- | --- | --- |
| Duplikate werden falsch bewertet | Kernelement der Aufgabe ist fehlerhaft | Zweiphasigen Algorithmus getrennt implementieren und mit festen Beispielen testen |
| Maven-Abhängigkeiten können beim ersten Lauf nicht geladen werden | Automatisierte Tests verzögern sich | Internetverbindung prüfen und `.\mvnw.cmd test` erneut ausführen |
| Zeit reicht nicht für optische Verbesserungen | Unwichtige Arbeiten bleiben offen | Zuerst Pflichtlogik, Tests, README und Abgabe sichern |
| GUI verändert Spielregeln oder Rundenablauf | Konsole und GUI verhalten sich unterschiedlich | Gemeinsame `Game`-Instanz verwenden und beide Modi testen |
| Betreuungscheck fällt aus | Weniger externes Feedback | Eigene Abnahmecheckliste verwenden und offene Fragen dokumentieren |
| Unklare Abgabeform | Falsche oder unvollständige Abgabe | Früh nachfragen; sonst ZIP mit Quellcode, Tests und README bereitstellen |

## 8. Quellen

- Aufgabenstellung: `docs/AufgabenScann.pdf`
- Spielregeln: `docs/Mastermind.md`
- IPERKA-Grundlage: [BZZ - IPERKA](https://wiki.bzz.ch/doku.php?id=modul%3Aarchiv%3Am431%3Alearningunits%3Alu03%3Alu03c_iperka&rev=1750685906)
