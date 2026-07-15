package ch.valentindibbern.mastermind.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackEvaluatorTest {
    private final FeedbackEvaluator evaluator = new FeedbackEvaluator();

    @Test
    void returnsAllBlackMarksForAnExactGuess() {
        Feedback feedback = evaluator.evaluate(code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW),
                code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW));

        assertEquals(new Feedback(4, 0), feedback);
    }

    @Test
    void returnsWhiteMarksForCorrectColoursAtWrongPositions() {
        Feedback feedback = evaluator.evaluate(code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW),
                code(Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED));

        assertEquals(new Feedback(0, 4), feedback);
    }

    @Test
    void doesNotCountDuplicateColoursMoreThanOnce() {
        Feedback feedback = evaluator.evaluate(code(Color.RED, Color.RED, Color.GREEN, Color.BLUE),
                code(Color.RED, Color.GREEN, Color.RED, Color.RED));

        assertEquals(new Feedback(1, 2), feedback);
    }

    @Test
    void rejectsInvalidCodes() {
        Color[] valid = code(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);

        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(new Color[3], valid));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(valid, code(Color.RED, null, Color.BLUE, Color.YELLOW)));
    }

    private static Color[] code(Color first, Color second, Color third, Color fourth) {
        return new Color[]{first, second, third, fourth};
    }
}
