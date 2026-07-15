package ch.valentindibbern.mastermind;

public class FeedbackEvaluator {
    public Feedback evaluate(Color[] secret, Color[] guess) {
        CodeValidator.validateCode(secret, "Geheimcode");
        CodeValidator.validateCode(guess, "Tipp");

        boolean[] usedSecretPositions = new boolean[Game.CODE_LENGTH];
        boolean[] usedGuessPositions = new boolean[Game.CODE_LENGTH];
        int blackMarks = 0;
        int whiteMarks = 0;

        // Reserve exact matches first to avoid double-counting duplicate colours.
        for (int index = 0; index < Game.CODE_LENGTH; index++) {
            if (secret[index] == guess[index]) {
                blackMarks++;
                usedSecretPositions[index] = true;
                usedGuessPositions[index] = true;
            }
        }

        // Match the remaining colours only once, regardless of position.
        for (int guessIndex = 0; guessIndex < Game.CODE_LENGTH; guessIndex++) {
            if (usedGuessPositions[guessIndex]) {
                continue;
            }

            for (int secretIndex = 0; secretIndex < Game.CODE_LENGTH; secretIndex++) {
                if (!usedSecretPositions[secretIndex] && secret[secretIndex] == guess[guessIndex]) {
                    whiteMarks++;
                    usedSecretPositions[secretIndex] = true;
                    usedGuessPositions[guessIndex] = true;
                    break;
                }
            }
        }

        return new Feedback(blackMarks, whiteMarks);
    }
}
