package ch.valentindibbern.mastermind;

import java.util.Random;
import java.util.Scanner;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        CodeGenerator codeGenerator = new CodeGenerator(new Random());
        FeedbackEvaluator feedbackEvaluator = new FeedbackEvaluator();
        GameSession gameSession = new GameSession(codeGenerator, feedbackEvaluator);

        if (shouldUseGui(args)) {
            SwingUtilities.invokeLater(() -> new MastermindFrame(gameSession).setVisible(true));
            return;
        }

        ConsoleUI consoleUI = new ConsoleUI(
                new Scanner(System.in),
                System.out,
                gameSession,
                shouldUseAnsiColours(args, System.getenv("NO_COLOR"))
        );

        consoleUI.run();
    }

    static boolean shouldUseGui(String[] args) {
        for (String arg : args) {
            if ("--gui".equals(arg)) {
                return true;
            }
        }

        return false;
    }

    static boolean shouldUseAnsiColours(String[] args, String noColor) {
        for (String arg : args) {
            if ("--no-color".equals(arg)) {
                return false;
            }
        }

        return noColor == null || noColor.isEmpty();
    }
}
