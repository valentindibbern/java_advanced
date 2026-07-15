package ch.valentindibbern.mastermind.domain;

import java.util.Arrays;
import java.util.Objects;

final class RoundHistory {
    private final Color[][] guesses = new Color[GameRules.MAX_ATTEMPTS][];
    private final Feedback[] feedback = new Feedback[GameRules.MAX_ATTEMPTS];
    private final GameStatus[] statuses = new GameStatus[GameRules.MAX_ATTEMPTS];
    private int size;

    void append(Color[] guess, Feedback turnFeedback, GameStatus status) {
        if (size == GameRules.MAX_ATTEMPTS) {
            throw new IllegalStateException("Es können keine weiteren Tipps gespeichert werden.");
        }

        CodeValidator.validateGuess(guess);
        feedback[size] = Objects.requireNonNull(turnFeedback);
        statuses[size] = Objects.requireNonNull(status);
        guesses[size] = Arrays.copyOf(guess, guess.length);
        size++;
    }

    void clear() {
        Arrays.fill(guesses, null);
        Arrays.fill(feedback, null);
        Arrays.fill(statuses, null);
        size = 0;
    }

    TurnResult[] snapshot() {
        TurnResult[] results = new TurnResult[size];
        for (int index = 0; index < size; index++) {
            results[index] = new TurnResult(guesses[index], feedback[index], statuses[index], index + 1);
        }
        return results;
    }
}
