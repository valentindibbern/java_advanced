package ch.valentindibbern.mastermind.domain;

public record Feedback(int blackMarks, int whiteMarks) {
    public Feedback {
        if (blackMarks < 0 || whiteMarks < 0) {
            throw new IllegalArgumentException("Marken dürfen nicht negativ sein.");
        }
        if (blackMarks + whiteMarks > GameRules.CODE_LENGTH) {
            throw new IllegalArgumentException("Es sind höchstens vier Marken erlaubt.");
        }
    }
}
