package ch.valentindibbern.mastermind;

final class CodeValidator {
    private CodeValidator() {
    }

    static void validateCode(Color[] code, String context) {
        // Ein gültiger Code besteht immer aus genau vier gesetzten Farben.
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
