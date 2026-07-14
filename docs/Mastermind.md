# Mastermind - Klassische Spielregeln

## Ziel des Spiels

Eine Person, der **Codierer**, legt einen geheimen Farbcode fest. Die ratende Person versucht, diesen Code innerhalb einer begrenzten Anzahl von Versuchen zu entschlüsseln. Gewonnen ist das Spiel, sobald der geheime Code vollständig - Farbe und Position - erraten wurde.

## Material und Ausgangslage

In der gebräuchlichen klassischen Variante besteht der Geheimcode aus vier farbigen Stiften. Für jede Stelle steht eine Auswahl von sechs Farben zur Verfügung. Eine Farbe darf im Geheimcode mehrmals vorkommen. Der Code bleibt für die ratende Person verborgen.

Der Rater gibt pro Zug ebenfalls eine geordnete Folge aus vier Farben ab. Die Reihenfolge ist wichtig: Derselbe Satz Farben in einer anderen Reihenfolge ist ein anderer Rateversuch.

## Rückmeldung nach einem Zug

Nach jedem Rateversuch erhält der Rater Hinweise, jedoch keine Zuordnung der Hinweise zu einzelnen Positionen:

- Ein **schwarzer Stift** bedeutet: Eine Farbe stimmt und befindet sich an der richtigen Position.
- Ein **weisser Stift** bedeutet: Eine Farbe kommt im Geheimcode vor, wurde aber an einer falschen Position geraten.
- Kein Stift bedeutet: Für die betreffende Farbe gibt es keinen weiteren Treffer.

Schwarze Hinweise werden zuerst ermittelt. Danach werden nur die noch nicht berücksichtigten Farben für weisse Hinweise verglichen. Dadurch wird jede Stelle höchstens einmal gewertet, auch wenn Farben mehrfach vorkommen.

## Spielablauf und Ende

Der Rater wiederholt seine Versuche und nutzt die bisherigen Rückmeldungen, um mögliche Codes einzugrenzen. In vielen klassischen Spielsets stehen zehn bis zwölf Versuche zur Verfügung; die genaue Zahl kann als Spielregel vereinbart werden.

Das Spiel endet sofort mit einem Sieg, wenn vier schwarze Stifte vergeben werden. Sind alle vereinbarten Versuche aufgebraucht, ohne dass der Code vollständig getroffen wurde, gewinnt der Codierer. Anschliessend wird der geheime Code aufgedeckt.

## Beispiel

Geheimcode: **Rot - Blau - Grün - Gelb**  
Rateversuch: **Rot - Grün - Blau - Weiss**

Die Rückmeldung lautet: ein schwarzer Stift (Rot an Position 1) und zwei weisse Stifte (Grün und Blau sind vorhanden, aber vertauscht). Gelb wurde nicht geraten; Weiss kommt nicht im Geheimcode vor.

## Quellen

- [Aufgabenstellung: Java Advanced Mastermind](AufgabenScann.pdf) - Vorgaben für dieses Projekt: vier Stellen, sechs Farben, Wiederholungen erlaubt und sieben Versuche.
- [Hasbro: Mastermind - Produktinformationen und Spielprinzip](https://instructions.hasbro.com/en-au/instruction/mastermind-board-game-for-families-and-kids-the-classic-code-cracking-game-family-gifts-family-games) - Rollen von Codierer und Rater sowie das Ziel, den Geheimcode zu knacken.
- [Mastermind Game Rules (Spielanleitung, PDF)](https://www.buffalolib.org/sites/default/files/gaming-unplugged/inst/Mastermind%20Instructions.pdf) - klassische Regeln zu schwarzen und weissen Hinweisen, mehrfachen Farben und der fehlenden Zuordnung der Hinweise zu Positionen.
