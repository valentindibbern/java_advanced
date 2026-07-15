package ch.valentindibbern.mastermind.domain;

final class CodeValidator {
    private CodeValidator() {
    }

    static void validateSecretCode(Color[] code) {
        validateCode(code, "Geheimcode");
    }

    static void validateGuess(Color[] guess) {
        validateCode(guess, "Tipp");
    }

    private static void validateCode(Color[] code, String name) {
        if (code == null || code.length != GameRules.CODE_LENGTH) {
            throw new IllegalArgumentException(name + " muss genau vier Farben enthalten.");
        }

        for (Color color : code) {
            if (color == null) {
                throw new IllegalArgumentException(name + " darf keine leeren Farben enthalten.");
            }
        }
    }
}
