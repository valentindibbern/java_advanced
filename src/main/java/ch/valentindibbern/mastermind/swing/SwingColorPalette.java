package ch.valentindibbern.mastermind.swing;

import ch.valentindibbern.mastermind.domain.Color;

import java.util.Objects;

final class SwingColorPalette {
    private SwingColorPalette() {
    }

    static java.awt.Color colorFor(Color color) {
        return switch (Objects.requireNonNull(color)) {
            case RED -> new java.awt.Color(196, 55, 55);
            case GREEN -> new java.awt.Color(55, 142, 68);
            case BLUE -> new java.awt.Color(52, 105, 190);
            case YELLOW -> new java.awt.Color(239, 196, 48);
            case ORANGE -> new java.awt.Color(232, 133, 38);
            case PURPLE -> new java.awt.Color(127, 75, 163);
        };
    }
}
