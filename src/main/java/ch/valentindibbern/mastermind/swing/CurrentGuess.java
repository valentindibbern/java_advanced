package ch.valentindibbern.mastermind.swing;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.domain.GameRules;

import java.util.Arrays;
import java.util.Objects;

final class CurrentGuess {
    private final Color[] colors = new Color[GameRules.CODE_LENGTH];
    private int size;

    boolean add(Color color) {
        if (size == colors.length) {
            return false;
        }
        colors[size++] = Objects.requireNonNull(color);
        return true;
    }

    Color removeLast() {
        if (size == 0) {
            return null;
        }
        Color removed = colors[--size];
        colors[size] = null;
        return removed;
    }

    boolean isComplete() {
        return size == colors.length;
    }

    boolean isEmpty() {
        return size == 0;
    }

    int size() {
        return size;
    }

    Color colorAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Ungültige Farbposition: " + index);
        }
        return colors[index];
    }

    Color[] toGuess() {
        if (!isComplete()) {
            throw new IllegalStateException("Der Tipp ist noch nicht vollständig.");
        }
        return Arrays.copyOf(colors, colors.length);
    }

    void clear() {
        Arrays.fill(colors, null);
        size = 0;
    }
}
