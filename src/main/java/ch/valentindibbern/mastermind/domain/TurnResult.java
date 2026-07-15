package ch.valentindibbern.mastermind.domain;

import java.util.Arrays;
import java.util.Objects;

public final class TurnResult {
    private final Color[] guess;
    private final Feedback feedback;
    private final GameStatus status;
    private final int attemptNumber;

    public TurnResult(Color[] guess, Feedback feedback, GameStatus status, int attemptNumber) {
        CodeValidator.validateGuess(guess);
        if (attemptNumber < 1 || attemptNumber > GameRules.MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Ungültige Versuchszahl: " + attemptNumber);
        }

        this.guess = Arrays.copyOf(guess, guess.length);
        this.feedback = Objects.requireNonNull(feedback);
        this.status = Objects.requireNonNull(status);
        this.attemptNumber = attemptNumber;
    }

    public Color[] getGuess() {
        return Arrays.copyOf(guess, guess.length);
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TurnResult result)) {
            return false;
        }
        return attemptNumber == result.attemptNumber
                && Arrays.equals(guess, result.guess)
                && feedback.equals(result.feedback)
                && status == result.status;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(guess);
        result = 31 * result + feedback.hashCode();
        result = 31 * result + status.hashCode();
        return 31 * result + attemptNumber;
    }

    @Override
    public String toString() {
        return "TurnResult[guess=" + Arrays.toString(guess)
                + ", feedback=" + feedback
                + ", status=" + status
                + ", attemptNumber=" + attemptNumber + "]";
    }
}
