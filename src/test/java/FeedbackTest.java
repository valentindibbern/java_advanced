import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackTest {
    @Test
    void storesValidMarkCounts() {
        Feedback feedback = new Feedback(2, 1);

        assertEquals(2, feedback.getBlackMarks());
        assertEquals(1, feedback.getWhiteMarks());
    }

    @Test
    void rejectsNegativeMarksAndMoreThanFourMarks() {
        assertThrows(IllegalArgumentException.class, () -> new Feedback(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Feedback(0, -1));
        assertThrows(IllegalArgumentException.class, () -> new Feedback(3, 2));
    }
}
