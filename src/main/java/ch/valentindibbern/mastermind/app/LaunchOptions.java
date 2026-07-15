package ch.valentindibbern.mastermind.app;

import java.util.Objects;

public record LaunchOptions(boolean useGui, boolean useAnsiColours, boolean showHelp) {
    public static LaunchOptions parse(String[] args, String noColorValue) {
        Objects.requireNonNull(args);
        boolean useGui = false;
        boolean noColor = noColorValue != null && !noColorValue.isEmpty();
        boolean showHelp = false;

        for (String arg : args) {
            switch (arg) {
                case "--gui" -> useGui = true;
                case "--no-color" -> noColor = true;
                case "--help" -> showHelp = true;
                default -> throw new IllegalArgumentException("Unbekannte Option: " + arg);
            }
        }

        return new LaunchOptions(useGui, !noColor, showHelp);
    }
}
