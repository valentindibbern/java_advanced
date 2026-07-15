package ch.valentindibbern.mastermind.app;

import ch.valentindibbern.mastermind.console.ConsoleUI;
import ch.valentindibbern.mastermind.domain.FeedbackEvaluator;
import ch.valentindibbern.mastermind.domain.Game;
import ch.valentindibbern.mastermind.domain.RandomSecretCodeProvider;
import ch.valentindibbern.mastermind.swing.MastermindFrame;

import javax.swing.SwingUtilities;
import java.util.Random;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        LaunchOptions options;
        try {
            options = LaunchOptions.parse(args, System.getenv("NO_COLOR"));
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
            printUsage();
            return;
        }

        if (options.showHelp()) {
            printUsage();
            return;
        }

        Game game = new Game(new RandomSecretCodeProvider(new Random()), new FeedbackEvaluator());
        if (options.useGui()) {
            SwingUtilities.invokeLater(() -> new MastermindFrame(game).setVisible(true));
            return;
        }

        new ConsoleUI(new Scanner(System.in), System.out, game, options.useAnsiColours()).run();
    }

    private static void printUsage() {
        System.out.println("Verwendung: java -jar mastermind.jar [--gui] [--no-color] [--help]");
        System.out.println("  --gui       Startet die grafische Oberfläche.");
        System.out.println("  --no-color  Deaktiviert ANSI-Farben in der Konsole.");
        System.out.println("  --help      Zeigt diese Hilfe an.");
    }
}
