package ch.valentindibbern.mastermind;

public enum Color {
    RED(0, "Rot", "\u001B[31m"),
    GREEN(1, "Grün", "\u001B[32m"),
    BLUE(2, "Blau", "\u001B[34m"),
    YELLOW(3, "Gelb", "\u001B[33m"),
    ORANGE(4, "Orange", "\u001B[33m"),
    PURPLE(5, "Violett", "\u001B[35m");

    private final int number;
    private final String displayName;
    private final String ansiCode;

    Color(int number, String displayName, String ansiCode) {
        this.number = number;
        this.displayName = displayName;
        this.ansiCode = ansiCode;
    }

    public static Color fromNumber(int number) {
        for (Color color : values()) {
            if (color.number == number) {
                return color;
            }
        }

        throw new IllegalArgumentException("Ungültige Farbnummer: " + number);
    }

    public int number() {
        return number;
    }

    public String displayName() {
        return displayName;
    }

    public String ansiCode() {
        return ansiCode;
    }
}
