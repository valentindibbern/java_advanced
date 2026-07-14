import enums.Color;
import enums.GameStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TurnResultTest {
    @Test
    void returnsDefensiveCopiesOfGuess() {
        Color[] guess = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        TurnResult result = new TurnResult(guess, new Feedback(1, 2), GameStatus.ONGOING, 1);
        guess[0] = Color.ORANGE;

        Color[] resultGuess = result.getGuess();
        assertEquals(Color.RED, resultGuess[0]);
        assertNotSame(guess, resultGuess);

        resultGuess[1] = Color.PURPLE;
        assertEquals(Color.GREEN, result.getGuess()[1]);
    }

    @Test
    void rejectsInvalidTurnData() {
        Color[] validGuess = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

        assertThrows(IllegalArgumentException.class,
                () -> new TurnResult(new Color[3], new Feedback(0, 0), GameStatus.ONGOING, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new TurnResult(validGuess, new Feedback(0, 0), GameStatus.ONGOING, 0));
        assertThrows(NullPointerException.class,
                () -> new TurnResult(validGuess, null, GameStatus.ONGOING, 1));
    }
}
