package ch.valentindibbern.mastermind;

import java.util.Arrays;
import java.util.Objects;

public class Game {
    static final int CODE_LENGTH = 4;
    static final int MAX_ATTEMPTS = 7;

    private final Color[] secretCode;
    private final Color[][] guessHistory;
    private final Feedback[] feedbackHistory;
    private final FeedbackEvaluator feedbackEvaluator;
    private int attemptsUsed;
    private GameStatus status;

    public Game(CodeGenerator generator, FeedbackEvaluator evaluator) {
        this(Objects.requireNonNull(generator).generateCode(), evaluator);
    }

    public Game(Color[] secretCode, FeedbackEvaluator evaluator) {
        CodeValidator.validateCode(secretCode, "Geheimcode");
        this.secretCode = Arrays.copyOf(secretCode, secretCode.length);
        this.guessHistory = new Color[MAX_ATTEMPTS][CODE_LENGTH];
        this.feedbackHistory = new Feedback[MAX_ATTEMPTS];
        this.feedbackEvaluator = Objects.requireNonNull(evaluator);
        this.attemptsUsed = 0;
        this.status = GameStatus.ONGOING;
    }

    public TurnResult submitGuess(Color[] guess) {
        if (status != GameStatus.ONGOING) {
            throw new IllegalStateException("Die Runde ist bereits beendet.");
        }

        CodeValidator.validateCode(guess, "Tipp");
        Feedback feedback = feedbackEvaluator.evaluate(secretCode, guess);
        Color[] storedGuess = Arrays.copyOf(guess, guess.length);
        guessHistory[attemptsUsed] = storedGuess;
        feedbackHistory[attemptsUsed] = feedback;
        attemptsUsed++;

        if (feedback.getBlackMarks() == CODE_LENGTH) {
            status = GameStatus.WON;
        } else if (attemptsUsed == MAX_ATTEMPTS) {
            status = GameStatus.LOST;
        }

        return new TurnResult(storedGuess, feedback, status, attemptsUsed);
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public Color[] getSecretCode() {
        return Arrays.copyOf(secretCode, secretCode.length);
    }

    public Color[][] getGuessHistory() {
        Color[][] historyCopy = new Color[guessHistory.length][];

        for (int index = 0; index < guessHistory.length; index++) {
            if (guessHistory[index] != null) {
                historyCopy[index] = Arrays.copyOf(guessHistory[index], guessHistory[index].length);
            }
        }

        return historyCopy;
    }

    public Feedback[] getFeedbackHistory() {
        return Arrays.copyOf(feedbackHistory, feedbackHistory.length);
    }
}
