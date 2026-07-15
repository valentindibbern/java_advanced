package ch.valentindibbern.mastermind;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @Test
    void usesAnsiColoursByDefault() {
        assertTrue(Main.shouldUseAnsiColours(new String[0], null));
        assertTrue(Main.shouldUseAnsiColours(new String[0], ""));
    }

    @Test
    void disablesAnsiColoursWithNoColorArgument() {
        assertFalse(Main.shouldUseAnsiColours(new String[]{"--no-color"}, null));
    }

    @Test
    void disablesAnsiColoursWithNoColorEnvironmentVariable() {
        assertFalse(Main.shouldUseAnsiColours(new String[0], "1"));
    }

    @Test
    void usesGuiOnlyWhenGuiFlagIsPresent() {
        assertFalse(Main.shouldUseGui(new String[0]));
        assertFalse(Main.shouldUseGui(new String[]{"--no-color"}));
        assertTrue(Main.shouldUseGui(new String[]{"--gui"}));
        assertTrue(Main.shouldUseGui(new String[]{"--gui", "--no-color"}));
    }
}
