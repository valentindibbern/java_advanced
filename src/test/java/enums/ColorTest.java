package enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorTest {
    @Test
    void returnsMatchingColourForEveryValidNumber() {
        assertEquals(Color.RED, Color.fromNumber(0));
        assertEquals(Color.GREEN, Color.fromNumber(1));
        assertEquals(Color.BLUE, Color.fromNumber(2));
        assertEquals(Color.YELLOW, Color.fromNumber(3));
        assertEquals(Color.ORANGE, Color.fromNumber(4));
        assertEquals(Color.PURPLE, Color.fromNumber(5));
    }

    @Test
    void rejectsInvalidColourNumbers() {
        assertThrows(IllegalArgumentException.class, () -> Color.fromNumber(-1));
        assertThrows(IllegalArgumentException.class, () -> Color.fromNumber(6));
    }

    @Test
    void exposesGermanDisplayNamesAndAnsiCodes() {
        assertEquals("Rot", Color.RED.displayName());
        assertEquals("Violett", Color.PURPLE.displayName());
        assertEquals("\u001B[31m", Color.RED.ansiCode());
        assertEquals("\u001B[35m", Color.PURPLE.ansiCode());
    }
}
