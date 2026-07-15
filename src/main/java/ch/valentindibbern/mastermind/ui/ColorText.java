package ch.valentindibbern.mastermind.ui;

import ch.valentindibbern.mastermind.domain.Color;

import java.util.Objects;

public final class ColorText {
    private ColorText() {
    }

    public static String displayName(Color color) {
        return switch (Objects.requireNonNull(color)) {
            case RED -> "Rot";
            case GREEN -> "Grün";
            case BLUE -> "Blau";
            case YELLOW -> "Gelb";
            case ORANGE -> "Orange";
            case PURPLE -> "Violett";
        };
    }
}
