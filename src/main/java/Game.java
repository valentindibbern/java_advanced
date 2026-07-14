import enums.Color;
import enums.GameStatus;

public class Game {
    private final static int CODE_LENGTH = 4;
    private final static int MAX_ATTEMPTS = 7;

    Game() {
        GameStatus status = GameStatus.TRANSITION;

        Feedback[] feedbackHistory = new Feedback[MAX_ATTEMPTS];
        CodeGenerator codeGenerator = new CodeGenerator();
        FeedbackEvaluator feedbackEvaluator = new FeedbackEvaluator();

        Color[] secretCode = codeGenerator.generateCode(CODE_LENGTH);
        Color[][] guessHistory = new Color[MAX_ATTEMPTS - 1][CODE_LENGTH];
        int attemptsUsed = 0;

        status = GameStatus.ONGOING;
    }
}
