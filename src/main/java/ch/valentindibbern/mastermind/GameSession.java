package ch.valentindibbern.mastermind;

import java.util.Objects;

/**
 * Coordinates the lifetime of one current Mastermind round for a user interface.
 */
public final class GameSession {
    private final CodeGenerator codeGenerator;
    private final FeedbackEvaluator feedbackEvaluator;
    private Game game;

    public GameSession(CodeGenerator codeGenerator, FeedbackEvaluator feedbackEvaluator) {
        this.codeGenerator = Objects.requireNonNull(codeGenerator);
        this.feedbackEvaluator = Objects.requireNonNull(feedbackEvaluator);
        startNewRound();
    }

    public void startNewRound() {
        game = new Game(codeGenerator, feedbackEvaluator);
    }

    public TurnResult submitGuess(Color[] guess) {
        return game.submitGuess(guess);
    }

    public GameStatus getStatus() {
        return game.getStatus();
    }

    public int getAttemptsUsed() {
        return game.getAttemptsUsed();
    }

    public Color[] revealSecretCode() {
        if (game.getStatus() == GameStatus.ONGOING) {
            throw new IllegalStateException("Der Geheimcode darf erst nach dem Rundenende angezeigt werden.");
        }

        return game.getSecretCode();
    }
}
