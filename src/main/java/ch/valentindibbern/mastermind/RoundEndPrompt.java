package ch.valentindibbern.mastermind;

import java.awt.Component;

@FunctionalInterface
interface RoundEndPrompt {
    boolean requestRestart(Component parent, GameStatus status, Color[] secretCode);
}
