package ch.valentindibbern.mastermind.console;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.domain.FeedbackEvaluator;
import ch.valentindibbern.mastermind.domain.Game;
import ch.valentindibbern.mastermind.domain.SecretCodeProvider;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleUITest {
    @Test
    void keepsInvalidInputOutOfTheAttemptCountAndWinsWithAValidGuess() {
        String output = run("not a code\n0 1 2 3\nn\n");

        assertTrue(output.contains("Ungültige Eingabe"));
        assertTrue(output.contains("Gewonnen!"));
        assertTrue(output.contains("Tipp 1"));
    }

    @Test
    void reportsEndOfInputDuringRestartChoice() {
        String output = run("0 1 2 3\n");

        assertTrue(output.contains("Gewonnen!"));
        assertTrue(output.contains("Eingabe beendet. Auf Wiedersehen."));
    }

    @Test
    void omitsAnsiSequencesWhenDisabled() {
        String output = run("0 1 2 3\nn\n", false);

        assertTrue(output.contains("0 = Rot"));
        assertTrue(!output.contains("\u001B["));
    }

    private static String run(String input) {
        return run(input, false);
    }

    private static String run(String input, boolean useAnsiColours) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        SecretCodeProvider provider = () -> new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        ConsoleUI consoleUI = new ConsoleUI(
                new Scanner(input),
                new PrintStream(output),
                new Game(provider, new FeedbackEvaluator()),
                useAnsiColours
        );
        consoleUI.run();
        return output.toString();
    }
}
