public final class Feedback {
    private final int blackMarks;
    private final int whiteMarks;

    Feedback(int blackMarks, int whiteMarks) {
        this.blackMarks = blackMarks;
        this.whiteMarks = whiteMarks;
    }

    public int BlackMarks() {return this.blackMarks;}
    public int WhiteMarks() {return this.whiteMarks;}
}
