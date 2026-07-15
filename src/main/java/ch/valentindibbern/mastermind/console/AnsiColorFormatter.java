package ch.valentindibbern.mastermind.console;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.ui.ColorText;

import java.util.Objects;

final class AnsiColorFormatter {
    private static final String RESET = "\u001B[0m";

    String format(Color color, boolean useAnsiColours) {
        Color nonNullColor = Objects.requireNonNull(color);
        String name = ColorText.displayName(nonNullColor);
        if (!useAnsiColours) {
            return name;
        }
        return ansiCode(nonNullColor) + name + RESET;
    }

    private String ansiCode(Color color) {
        return switch (color) {
            case RED -> "\u001B[31m";
            case GREEN -> "\u001B[32m";
            case BLUE -> "\u001B[34m";
            case YELLOW, ORANGE -> "\u001B[33m";
            case PURPLE -> "\u001B[35m";
        };
    }
}
