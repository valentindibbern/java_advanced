package ch.valentindibbern.mastermind;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;

public final class MastermindFrame extends JFrame {
    public MastermindFrame(GameSession gameSession) {
        super("Mastermind");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        add(new MastermindPanel(gameSession, MastermindFrame::requestRestart, this::dispose), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private static boolean requestRestart(Component parent, GameStatus status, Color[] secretCode) {
        String message = status == GameStatus.WON
                ? "Gewonnen! Du hast den Geheimcode erraten."
                : "Verloren! Du hast alle sieben Versuche verbraucht.";
        String secret = formatColors(secretCode);
        int answer = JOptionPane.showConfirmDialog(
                parent,
                message + "\nDer Geheimcode war: " + secret + "\nNeue Runde starten?",
                "Mastermind",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );
        return answer == JOptionPane.YES_OPTION;
    }

    private static String formatColors(Color[] colors) {
        StringBuilder formattedColors = new StringBuilder();
        for (int index = 0; index < colors.length; index++) {
            if (index > 0) {
                formattedColors.append(' ');
            }
            formattedColors.append(colors[index].displayName());
        }
        return formattedColors.toString();
    }
}
