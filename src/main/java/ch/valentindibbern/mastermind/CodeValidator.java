package ch.valentindibbern.mastermind;

final class CodeValidator {
    private CodeValidator() {
    }

    static void validateCode(Color[] code, String context) {
        if (code == null || code.length != Game.CODE_LENGTH) {
            throw new IllegalArgumentException(context + " muss genau vier Farben enthalten.");
        }

        for (Color color : code) {
            if (color == null) {
                throw new IllegalArgumentException(context + " darf keine leeren Farben enthalten.");
            }
        }
    }
}
