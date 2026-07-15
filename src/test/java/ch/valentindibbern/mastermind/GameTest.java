package ch.valentindibbern.mastermind;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTest {
    private static final Color[] SECRET = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW
    };
    private static final Color[] WRONG_GUESS = {
            Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE
    };

    @Test
    void startsInOngoingStateWithoutAttempts() {
        Game game = new Game(SECRET, new FeedbackEvaluator());

        assertEquals(GameStatus.ONGOING, game.getStatus());
        assertEquals(0, game.getAttemptsUsed());
        assertNull(game.getGuessHistory()[0][0]);
        assertNull(game.getFeedbackHistory()[0]);
    }

    @Test
    void winsWithExactGuessOnFirstAttempt() {
        Game game = new Game(SECRET, new FeedbackEvaluator());

        TurnResult result = game.submitGuess(SECRET);

        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(1, result.getAttemptNumber());
        assertEquals(GameStatus.WON, game.getStatus());
        assertEquals(1, game.getAttemptsUsed());
    }

    @Test
    void winsInsteadOfLosingWhenSeventhGuessIsExact() {
        Game game = new Game(SECRET, new FeedbackEvaluator());
        for (int attempt = 0; attempt < 6; attempt++) {
            game.submitGuess(WRONG_GUESS);
        }

        TurnResult result = game.submitGuess(SECRET);

        assertEquals(7, result.getAttemptNumber());
        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(GameStatus.WON, game.getStatus());
    }

    @Test
    void losesAfterSevenFailedGuesses() {
        Game game = new Game(SECRET, new FeedbackEvaluator());
        for (int attempt = 0; attempt < Game.MAX_ATTEMPTS; attempt++) {
            game.submitGuess(WRONG_GUESS);
        }

        assertEquals(GameStatus.LOST, game.getStatus());
        assertEquals(Game.MAX_ATTEMPTS, game.getAttemptsUsed());
    }

    @Test
    void rejectsGuessAfterRoundHasEnded() {
        Game game = new Game(SECRET, new FeedbackEvaluator());
        game.submitGuess(SECRET);

        assertThrows(IllegalStateException.class, () -> game.submitGuess(WRONG_GUESS));
    }

    @Test
    void protectsSecretGuessAndHistoryFromExternalChanges() {
        Color[] secret = SECRET.clone();
        Game game = new Game(secret, new FeedbackEvaluator());
        secret[0] = Color.PURPLE;

        Color[] guess = WRONG_GUESS.clone();
        game.submitGuess(guess);
        guess[0] = Color.RED;

        Color[] secretCopy = game.getSecretCode();
        assertEquals(Color.RED, secretCopy[0]);
        secretCopy[0] = Color.PURPLE;
        assertEquals(Color.RED, game.getSecretCode()[0]);

        Color[][] history = game.getGuessHistory();
        assertNotSame(history, game.getGuessHistory());
        assertEquals(Color.ORANGE, history[0][0]);
        history[0][0] = Color.RED;
        assertEquals(Color.ORANGE, game.getGuessHistory()[0][0]);
    }

    @Test
    void rejectsInvalidGuessesWithoutUsingAttempt() {
        Game game = new Game(SECRET, new FeedbackEvaluator());

        assertThrows(IllegalArgumentException.class, () -> game.submitGuess(new Color[3]));
        assertThrows(IllegalArgumentException.class,
                () -> game.submitGuess(new Color[]{Color.RED, null, Color.BLUE, Color.YELLOW}));
        assertEquals(0, game.getAttemptsUsed());
        assertEquals(GameStatus.ONGOING, game.getStatus());
    }
}
