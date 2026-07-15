package ch.valentindibbern.mastermind.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LaunchOptionsTest {
    @Test
    void usesAnsiByDefaultAndRespectsNoColorSources() {
        assertTrue(LaunchOptions.parse(new String[0], null).useAnsiColours());
        assertTrue(LaunchOptions.parse(new String[0], "").useAnsiColours());
        assertFalse(LaunchOptions.parse(new String[0], "1").useAnsiColours());
        assertFalse(LaunchOptions.parse(new String[]{"--no-color"}, null).useAnsiColours());
    }

    @Test
    void parsesGuiAndHelpOptions() {
        assertTrue(LaunchOptions.parse(new String[]{"--gui"}, null).useGui());
        assertTrue(LaunchOptions.parse(new String[]{"--help"}, null).showHelp());
    }

    @Test
    void rejectsUnknownOptions() {
        assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse(new String[]{"--unknown"}, null));
    }
}
