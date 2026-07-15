package ch.valentindibbern.mastermind.console;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.domain.Feedback;
import ch.valentindibbern.mastermind.domain.Game;
import ch.valentindibbern.mastermind.domain.GameRules;
import ch.valentindibbern.mastermind.domain.GameStatus;
import ch.valentindibbern.mastermind.domain.TurnResult;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public final class ConsoleUI {
    private final Scanner scanner;
    private final PrintStream out;
    private final Game game;
    private final boolean useAnsiColours;
    private final AnsiColorFormatter colorFormatter = new AnsiColorFormatter();

    public ConsoleUI(Scanner scanner, PrintStream out, Game game, boolean useAnsiColours) {
        this.scanner = Objects.requireNonNull(scanner);
        this.out = Objects.requireNonNull(out);
        this.game = Objects.requireNonNull(game);
        this.useAnsiColours = useAnsiColours;
    }

    public void run() {
        out.println("Mastermind");
        out.println("Errate den geheimen Farbcode in höchstens sieben Versuchen.");
        showLegend();
        out.println("Gib vier Farbnummern ein, zum Beispiel: 0 3 3 5");

        while (playRound()) {
            showEndMessage();
            if (!readRestartChoice()) {
                return;
            }
            game.startNewRound();
        }
    }

    private boolean playRound() {
        while (game.getStatus() == GameStatus.ONGOING) {
            out.printf("Versuch %d von %d:%n", game.getAttemptsUsed() + 1, GameRules.MAX_ATTEMPTS);
            Color[] guess = readGuess();
            if (guess == null) {
                showInputEndedMessage();
                return false;
            }
            showTurnResult(game.submitGuess(guess));
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
            if (values.length != GameRules.CODE_LENGTH) {
                out.println("Ungültige Eingabe: Es werden genau vier Farbnummern erwartet.");
                continue;
            }

            Color[] guess = new Color[GameRules.CODE_LENGTH];
            try {
                for (int index = 0; index < values.length; index++) {
                    guess[index] = Color.fromNumber(Integer.parseInt(values[index]));
                }
                return guess;
            } catch (NumberFormatException exception) {
                out.println("Ungültige Eingabe: Verwende ganze Zahlen: " + availableNumbers() + ".");
            } catch (IllegalArgumentException exception) {
                out.println("Ungültige Eingabe: Erlaubte Farbnummern sind: " + availableNumbers() + ".");
            }
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
        showInputEndedMessage();
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
                feedback.blackMarks(),
                feedback.whiteMarks()
        );
    }

    private void showEndMessage() {
        if (game.getStatus() == GameStatus.WON) {
            out.println("Gewonnen! Du hast den Geheimcode erraten.");
        } else {
            out.println("Verloren! Du hast alle sieben Versuche verbraucht.");
        }
        out.println("Der Geheimcode war: " + formatColors(game.revealSecretCode()));
        out.println("Neue Runde? (j/n)");
    }

    private void showInputEndedMessage() {
        out.println("Eingabe beendet. Auf Wiedersehen.");
    }

    private String formatColor(Color color) {
        return colorFormatter.format(color, useAnsiColours);
    }

    private String formatColors(Color[] code) {
        return Arrays.stream(code)
                .map(this::formatColor)
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    private String availableNumbers() {
        return Arrays.stream(Color.values())
                .map(color -> Integer.toString(color.number()))
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }
}
