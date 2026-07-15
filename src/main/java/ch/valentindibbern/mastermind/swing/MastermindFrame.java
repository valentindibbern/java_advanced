package ch.valentindibbern.mastermind.swing;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.domain.Game;
import ch.valentindibbern.mastermind.domain.GameStatus;
import ch.valentindibbern.mastermind.ui.ColorText;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.util.Arrays;

public final class MastermindFrame extends JFrame {
    public MastermindFrame(Game game) {
        super("Mastermind");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        add(new MastermindPanel(game, () -> requestRestart(game), this::dispose), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private boolean requestRestart(Game game) {
        String message = game.getStatus() == GameStatus.WON
                ? "Gewonnen! Du hast den Geheimcode erraten."
                : "Verloren! Du hast alle sieben Versuche verbraucht.";
        int answer = JOptionPane.showConfirmDialog(
                this,
                message + "\nDer Geheimcode war: " + formatColors(game.revealSecretCode()) + "\nNeue Runde starten?",
                "Mastermind",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );
        return answer == JOptionPane.YES_OPTION;
    }

    private static String formatColors(Color[] colors) {
        return Arrays.stream(colors)
                .map(ColorText::displayName)
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }
}
