package ch.valentindibbern.mastermind.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TurnResultTest {
    @Test
    void comparesValuesAndDefensivelyCopiesItsGuess() {
        Color[] guess = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        TurnResult first = new TurnResult(guess, new Feedback(1, 2), GameStatus.ONGOING, 1);
        TurnResult second = new TurnResult(guess.clone(), new Feedback(1, 2), GameStatus.ONGOING, 1);

        guess[0] = Color.ORANGE;

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(Color.RED, first.getGuess()[0]);
        assertNotEquals(first, new TurnResult(new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW},
                new Feedback(1, 2), GameStatus.WON, 1));
    }
}
