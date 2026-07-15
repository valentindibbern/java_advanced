package ch.valentindibbern.mastermind;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final PrintStream out;
    private final GameSession gameSession;
    private final boolean useAnsiColours;

    public ConsoleUI(
            Scanner scanner,
            PrintStream out,
            GameSession gameSession,
            boolean useAnsiColours
    ) {
        this.scanner = Objects.requireNonNull(scanner);
        this.out = Objects.requireNonNull(out);
        this.gameSession = Objects.requireNonNull(gameSession);
        this.useAnsiColours = useAnsiColours;
    }

    public void run() {
        out.println("Mastermind");
        out.println("Errate den geheimen Farbcode in höchstens sieben Versuchen.");
        showLegend();
        out.println("Gib vier Farbnummern ein, zum Beispiel: 0 3 3 5");

        while (true) {
            if (!playRound()) {
                return;
            }

            showEndMessage();
            if (!readRestartChoice()) {
                return;
            }

            gameSession.startNewRound();
        }
    }

    private boolean playRound() {
        while (gameSession.getStatus() == GameStatus.ONGOING) {
            out.printf("Versuch %d von %d:%n", gameSession.getAttemptsUsed() + 1, Game.MAX_ATTEMPTS);
            Color[] guess = readGuess();

            if (guess == null) {
                return false;
            }

            showTurnResult(gameSession.submitGuess(guess));
        }

        return true;
    }

    private Color[] readGuess() {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                out.println("Ungültige Eingabe: Gib vier Farbnummern ein.");
                continue;
            }

            String[] values = line.split("\\s+");
            if (values.length != Game.CODE_LENGTH) {
                out.println("Ungültige Eingabe: Es werden genau vier Farbnummern erwartet.");
                continue;
            }

            Color[] guess = new Color[Game.CODE_LENGTH];
            try {
                for (int index = 0; index < values.length; index++) {
                    guess[index] = Color.fromNumber(Integer.parseInt(values[index]));
                }
            } catch (NumberFormatException exception) {
                out.println("Ungültige Eingabe: Verwende ganze Zahlen von 0 bis 5.");
                continue;
            } catch (IllegalArgumentException exception) {
                out.println("Ungültige Eingabe: Farbnummern müssen zwischen 0 und 5 liegen.");
                continue;
            }

            return guess;
        }

        return null;
    }

    private boolean readRestartChoice() {
        while (scanner.hasNextLine()) {
            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("j")) {
                return true;
            }
            if (choice.equalsIgnoreCase("n")) {
                return false;
            }

            out.println("Ungültige Eingabe: Antworte mit j oder n.");
        }

        return false;
    }

    private void showLegend() {
        out.println("Farblegende:");
        for (Color color : Color.values()) {
            out.printf("%d = %s%n", color.number(), formatColor(color));
        }
    }

    private void showTurnResult(TurnResult result) {
        Feedback feedback = result.getFeedback();
        out.printf(
                "Tipp %d: %s | Schwarz: %d | Weiss: %d%n",
                result.getAttemptNumber(),
                formatColors(result.getGuess()),
                feedback.getBlackMarks(),
                feedback.getWhiteMarks()
        );
    }

    private void showEndMessage() {
        if (gameSession.getStatus() == GameStatus.WON) {
            out.println("Gewonnen! Du hast den Geheimcode erraten.");
        } else {
            out.println("Verloren! Du hast alle sieben Versuche verbraucht.");
        }

        out.println("Der Geheimcode war: " + formatColors(gameSession.revealSecretCode()));
        out.println("Neue Runde? (j/n)");
    }

    private String formatColor(Color color) {
        if (!useAnsiColours) {
            return color.displayName();
        }

        return Ansi.colour(color.ansiCode(), color.displayName());
    }

    private String formatColors(Color[] code) {
        StringBuilder formattedColors = new StringBuilder();
        for (int index = 0; index < code.length; index++) {
            if (index > 0) {
                formattedColors.append(' ');
            }
            formattedColors.append(formatColor(code[index]));
        }

        return formattedColors.toString();
    }
}
