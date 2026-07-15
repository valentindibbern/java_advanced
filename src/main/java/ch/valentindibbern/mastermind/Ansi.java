package ch.valentindibbern.mastermind;

import java.util.Objects;

public final class Ansi {
    public static final String RESET = "\u001B[0m";

    private Ansi() {
    }

    public static String colour(String colour, String text) {
        return Objects.requireNonNull(colour) + Objects.requireNonNull(text) + RESET;
    }
}
