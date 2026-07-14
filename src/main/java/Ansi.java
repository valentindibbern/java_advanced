import java.util.Objects;

public final class Ansi {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\\u001B[1m";
    public static final String ITALIC = "";

    private Ansi() {
    }

    public static String colour(String colour, String text) {
        return Objects.requireNonNull(colour) + Objects.requireNonNull(text) + RESET;
    }
}
