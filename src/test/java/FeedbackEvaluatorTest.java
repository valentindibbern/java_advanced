import enums.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackEvaluatorTest {
    private final FeedbackEvaluator evaluator = new FeedbackEvaluator();

    @Test
    void returnsNoMarksForColoursOutsideSecretCode() {
        Feedback feedback = evaluator.evaluate(
                code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW),
                code(Color.ORANGE, Color.PURPLE, Color.ORANGE, Color.PURPLE)
        );

        assertFeedback(feedback, 0, 0);
    }

    @Test
    void returnsFourBlackMarksForExactCode() {
        Color[] code = code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);

        assertFeedback(evaluator.evaluate(code, code), 4, 0);
    }

    @Test
    void returnsFourWhiteMarksForCompletelyReorderedCode() {
        Feedback feedback = evaluator.evaluate(
                code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW),
                code(Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED)
        );

        assertFeedback(feedback, 0, 4);
    }

    @Test
    void countsEachDuplicateColourOnlyOnce() {
        Feedback feedback = evaluator.evaluate(
                code(Color.RED, Color.RED, Color.GREEN, Color.BLUE),
                code(Color.RED, Color.GREEN, Color.RED, Color.RED)
        );

        assertFeedback(feedback, 1, 2);
    }

    @Test
    void countsBlackMarksBeforeWhiteMarks() {
        Feedback feedback = evaluator.evaluate(
                code(Color.RED, Color.RED, Color.BLUE, Color.BLUE),
                code(Color.RED, Color.BLUE, Color.BLUE, Color.RED)
        );

        assertFeedback(feedback, 2, 2);
    }

    @Test
    void rejectsInvalidCodes() {
        Color[] validCode = code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);

        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(null, validCode));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(new Color[3], validCode));
        assertThrows(IllegalArgumentException.class,
                () -> evaluator.evaluate(code(Color.RED, null, Color.BLUE, Color.YELLOW), validCode));
    }

    private static Color[] code(Color first, Color second, Color third, Color fourth) {
        return new Color[]{first, second, third, fourth};
    }

    private static void assertFeedback(Feedback feedback, int blackMarks, int whiteMarks) {
        assertEquals(blackMarks, feedback.getBlackMarks());
        assertEquals(whiteMarks, feedback.getWhiteMarks());
    }
}
