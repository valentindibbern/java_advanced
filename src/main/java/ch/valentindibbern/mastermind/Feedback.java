package ch.valentindibbern.mastermind;

public final class Feedback {
    private final int blackMarks;
    private final int whiteMarks;

    public Feedback(int blackMarks, int whiteMarks) {
        if (blackMarks < 0 || whiteMarks < 0) {
            throw new IllegalArgumentException("Marken dürfen nicht negativ sein.");
        }
        if (blackMarks + whiteMarks > Game.CODE_LENGTH) {
            throw new IllegalArgumentException("Es sind höchstens vier Marken erlaubt.");
        }

        this.blackMarks = blackMarks;
        this.whiteMarks = whiteMarks;
    }

    public int getBlackMarks() {
        return blackMarks;
    }

    public int getWhiteMarks() {
        return whiteMarks;
    }
}
