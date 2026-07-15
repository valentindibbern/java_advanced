package ch.valentindibbern.mastermind.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTest {
    private static final Color[] SECRET = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    @Test
    void copiesTheSecretCodeProvidedAtRoundStart() {
        Color[] providedCode = SECRET.clone();
        Game game = gameWith(() -> providedCode);
        providedCode[0] = Color.PURPLE;

        TurnResult result = game.submitGuess(SECRET);

        assertEquals(GameStatus.WON, result.getStatus());
    }

    @Test
    void rejectsInvalidSecretCodesImmediately() {
        assertThrows(IllegalArgumentException.class, () -> gameWith(() -> null));
        assertThrows(IllegalArgumentException.class, () -> gameWith(() -> new Color[3]));
        assertThrows(IllegalArgumentException.class, () -> gameWith(() -> new Color[]{Color.RED, null, Color.BLUE, Color.YELLOW}));
    }

    @Test
    void exposesOnlyPlayedTurnsAsDefensiveSnapshots() {
        Game game = gameWith(() -> SECRET.clone());
        Color[] guess = {Color.ORANGE, Color.GREEN, Color.BLUE, Color.RED};
        game.submitGuess(guess);
        guess[0] = Color.PURPLE;

        TurnResult[] history = game.getTurnHistory();

        assertEquals(1, history.length);
        assertArrayEquals(new Color[]{Color.ORANGE, Color.GREEN, Color.BLUE, Color.RED}, history[0].getGuess());
        history[0].getGuess()[0] = Color.PURPLE;
        assertEquals(Color.ORANGE, game.getTurnHistory()[0].getGuess()[0]);
    }

    @Test
    void winsBeforeApplyingTheSeventhAttemptLossRule() {
        Game game = gameWith(() -> SECRET.clone());
        Color[] failedGuess = {Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE};
        for (int attempt = 0; attempt < GameRules.MAX_ATTEMPTS - 1; attempt++) {
            game.submitGuess(failedGuess);
        }

        TurnResult result = game.submitGuess(SECRET);

        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(GameRules.MAX_ATTEMPTS, game.getAttemptsUsed());
    }

    @Test
    void losesAfterSevenFailedGuessesAndResetsHistoryForANewRound() {
        Game game = gameWith(() -> SECRET.clone());
        Color[] failedGuess = {Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE};
        for (int attempt = 0; attempt < GameRules.MAX_ATTEMPTS; attempt++) {
            game.submitGuess(failedGuess);
        }

        assertEquals(GameStatus.LOST, game.getStatus());
        assertEquals(GameRules.MAX_ATTEMPTS, game.getTurnHistory().length);
        assertThrows(IllegalStateException.class, () -> game.submitGuess(failedGuess));

        game.startNewRound();

        assertEquals(GameStatus.ONGOING, game.getStatus());
        assertEquals(0, game.getAttemptsUsed());
        assertEquals(0, game.getTurnHistory().length);
    }

    @Test
    void hidesSecretCodeUntilRoundHasEnded() {
        Game game = gameWith(() -> SECRET.clone());

        assertThrows(IllegalStateException.class, game::revealSecretCode);
    }

    private static Game gameWith(SecretCodeProvider provider) {
        return new Game(provider, new FeedbackEvaluator());
    }
}
