package ch.valentindibbern.mastermind.domain;

import java.util.Arrays;
import java.util.Objects;

public final class Game {
    private final SecretCodeProvider secretCodeProvider;
    private final FeedbackEvaluator feedbackEvaluator;
    private final RoundHistory history = new RoundHistory();
    private Color[] secretCode;
    private int attemptsUsed;
    private GameStatus status;

    public Game(SecretCodeProvider secretCodeProvider, FeedbackEvaluator feedbackEvaluator) {
        this.secretCodeProvider = Objects.requireNonNull(secretCodeProvider);
        this.feedbackEvaluator = Objects.requireNonNull(feedbackEvaluator);
        startNewRound();
    }

    public void startNewRound() {
        Color[] generatedCode = secretCodeProvider.createSecretCode();
        CodeValidator.validateSecretCode(generatedCode);
        secretCode = Arrays.copyOf(generatedCode, generatedCode.length);
        history.clear();
        attemptsUsed = 0;
        status = GameStatus.ONGOING;
    }

    public TurnResult submitGuess(Color[] guess) {
        if (status != GameStatus.ONGOING) {
            throw new IllegalStateException("Die Runde ist bereits beendet.");
        }

        CodeValidator.validateGuess(guess);
        Feedback feedback = feedbackEvaluator.evaluate(secretCode, guess);
        attemptsUsed++;
        if (feedback.blackMarks() == GameRules.CODE_LENGTH) {
            status = GameStatus.WON;
        } else if (attemptsUsed == GameRules.MAX_ATTEMPTS) {
            status = GameStatus.LOST;
        }

        history.append(guess, feedback, status);
        return new TurnResult(guess, feedback, status, attemptsUsed);
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public Color[] revealSecretCode() {
        if (status == GameStatus.ONGOING) {
            throw new IllegalStateException("Der Geheimcode darf erst nach dem Rundenende angezeigt werden.");
        }

        return Arrays.copyOf(secretCode, secretCode.length);
    }

    public TurnResult[] getTurnHistory() {
        return history.snapshot();
    }
}
