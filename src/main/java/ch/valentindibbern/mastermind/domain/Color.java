package ch.valentindibbern.mastermind.domain;

public enum Color {
    RED(0),
    GREEN(1),
    BLUE(2),
    YELLOW(3),
    ORANGE(4),
    PURPLE(5);

    private final int number;

    Color(int number) {
        this.number = number;
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
}
