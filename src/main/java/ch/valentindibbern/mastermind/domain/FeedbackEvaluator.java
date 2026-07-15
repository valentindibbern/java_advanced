package ch.valentindibbern.mastermind.domain;

public final class FeedbackEvaluator {
    public Feedback evaluate(Color[] secret, Color[] guess) {
        CodeValidator.validateSecretCode(secret);
        CodeValidator.validateGuess(guess);

        boolean[] usedSecretPositions = new boolean[GameRules.CODE_LENGTH];
        boolean[] usedGuessPositions = new boolean[GameRules.CODE_LENGTH];
        int blackMarks = 0;
        int whiteMarks = 0;

        // Reserve exact matches first so duplicate colours are never counted twice.
        for (int index = 0; index < GameRules.CODE_LENGTH; index++) {
            if (secret[index] == guess[index]) {
                blackMarks++;
                usedSecretPositions[index] = true;
                usedGuessPositions[index] = true;
            }
        }

        for (int guessIndex = 0; guessIndex < GameRules.CODE_LENGTH; guessIndex++) {
            if (usedGuessPositions[guessIndex]) {
                continue;
            }

            for (int secretIndex = 0; secretIndex < GameRules.CODE_LENGTH; secretIndex++) {
                if (!usedSecretPositions[secretIndex] && secret[secretIndex] == guess[guessIndex]) {
                    whiteMarks++;
                    usedSecretPositions[secretIndex] = true;
                    break;
                }
            }
        }

        return new Feedback(blackMarks, whiteMarks);
    }
}
