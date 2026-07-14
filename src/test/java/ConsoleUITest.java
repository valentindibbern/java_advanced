import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleUITest {
    @Test
    void invalidGuessDoesNotUseAttemptAndFollowingGuessCanWin() {
        String output = run("Text Text Text Text\n0 1 2 3\nn\n", 0, 1, 2, 3);

        assertTrue(output.contains("Ungültige Eingabe: Verwende ganze Zahlen von 0 bis 5."));
        assertEquals(1, occurrences(output, "Versuch 1 von 7:"));
        assertTrue(output.contains("Tipp 1:"));
        assertTrue(output.contains("Schwarz: 4 | Weiss: 0"));
        assertTrue(output.contains("Gewonnen!"));
        assertTrue(output.contains("\u001B[31mRot\u001B[0m"));
    }

    @Test
    void showsLossAfterSevenFailedGuesses() {
        String input = "4 4 4 4\n".repeat(7) + "n\n";
        String output = run(input, 0, 1, 2, 3);

        assertTrue(output.contains("Versuch 7 von 7:"));
        assertTrue(output.contains("Tipp 7:"));
        assertTrue(output.contains("Verloren!"));
    }

    @Test
    void startsFreshRoundAfterRestartChoice() {
        String output = run("0 1 2 3\nj\n4 5 0 1\nn\n", 0, 1, 2, 3, 4, 5, 0, 1);

        assertEquals(2, occurrences(output, "Gewonnen!"));
        assertEquals(2, occurrences(output, "Versuch 1 von 7:"));
    }

    @Test
    void repeatsRestartPromptForInvalidChoice() {
        String output = run("0 1 2 3\nvielleicht\nn\n", 0, 1, 2, 3);

        assertTrue(output.contains("Ungültige Eingabe: Antworte mit j oder n."));
    }

    @Test
    void stopsCleanlyWhenInputEnds() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsoleUI consoleUI = new ConsoleUI(
                new Scanner(new StringReader("")),
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new CodeGenerator(new SequenceRandom(0, 1, 2, 3)),
                new FeedbackEvaluator()
        );

        assertDoesNotThrow(consoleUI::run);
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Mastermind"));
    }

    private static String run(String input, int... secretNumbers) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsoleUI consoleUI = new ConsoleUI(
                new Scanner(new StringReader(input)),
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new CodeGenerator(new SequenceRandom(secretNumbers)),
                new FeedbackEvaluator()
        );

        consoleUI.run();
        return output.toString(StandardCharsets.UTF_8);
    }

    private static int occurrences(String text, String searchedText) {
        return text.split(java.util.regex.Pattern.quote(searchedText), -1).length - 1;
    }

    private static final class SequenceRandom extends Random {
        private static final long serialVersionUID = 1L;

        private final int[] values;
        private int index;

        private SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            return values[index++ % values.length];
        }
    }
}
