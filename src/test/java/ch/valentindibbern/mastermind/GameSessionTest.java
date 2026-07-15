package ch.valentindibbern.mastermind;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameSessionTest {
    @Test
    void startsWithAnOngoingRoundAndDelegatesGuesses() {
        GameSession session = sessionWithCodes(0, 1, 2, 3);

        TurnResult result = session.submitGuess(new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW});

        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(GameStatus.WON, session.getStatus());
        assertEquals(1, session.getAttemptsUsed());
    }

    @Test
    void doesNotRevealSecretCodeBeforeRoundEnds() {
        GameSession session = sessionWithCodes(0, 1, 2, 3);

        assertThrows(IllegalStateException.class, session::revealSecretCode);
    }

    @Test
    void revealsDefensiveSecretCopyAfterRoundEnds() {
        GameSession session = sessionWithCodes(0, 1, 2, 3);
        session.submitGuess(new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW});

        Color[] revealed = session.revealSecretCode();
        revealed[0] = Color.PURPLE;

        assertArrayEquals(
                new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW},
                session.revealSecretCode()
        );
    }

    @Test
    void startsFreshRoundWithNextGeneratedCode() {
        GameSession session = sessionWithCodes(0, 1, 2, 3, 4, 5, 0, 1);
        session.submitGuess(new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW});

        session.startNewRound();

        assertEquals(GameStatus.ONGOING, session.getStatus());
        assertEquals(0, session.getAttemptsUsed());
        session.submitGuess(new Color[]{Color.ORANGE, Color.PURPLE, Color.RED, Color.GREEN});
        assertEquals(GameStatus.WON, session.getStatus());
    }

    private static GameSession sessionWithCodes(int... values) {
        return new GameSession(new CodeGenerator(new SequenceRandom(values)), new FeedbackEvaluator());
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
            return values[index++];
        }
    }
}
