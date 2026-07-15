package ch.valentindibbern.mastermind;

import java.util.Arrays;
import java.util.Objects;

public final class TurnResult {
    private final Color[] guess;
    private final Feedback feedback;
    private final GameStatus status;
    private final int attemptNumber;

    public TurnResult(Color[] guess, Feedback feedback, GameStatus status, int attemptNumber) {
        if (guess == null || guess.length != Game.CODE_LENGTH) {
            throw new IllegalArgumentException("Der Tipp muss genau vier Farben enthalten.");
        }
        if (Arrays.stream(guess).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Der Tipp darf keine leeren Farben enthalten.");
        }
        if (attemptNumber < 1 || attemptNumber > Game.MAX_ATTEMPTS) {
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
}
