package ch.valentindibbern.mastermind.swing;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.domain.Game;
import ch.valentindibbern.mastermind.domain.GameRules;
import ch.valentindibbern.mastermind.domain.GameStatus;
import ch.valentindibbern.mastermind.domain.TurnResult;
import ch.valentindibbern.mastermind.ui.ColorText;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;

final class MastermindPanel extends JPanel {
    private static final int PADDING = 16;
    private static final int PEG_SIZE = 30;
    private static final int SECRET_PEG_SIZE = 36;
    private final Game game;
    private final BooleanSupplier restartChoice;
    private final Runnable closeAction;
    private final PegView[] secretPegs = new PegView[GameRules.CODE_LENGTH];
    private final PegView[][] guessPegs = new PegView[GameRules.MAX_ATTEMPTS][GameRules.CODE_LENGTH];
    private final FeedbackView[] feedbackViews = new FeedbackView[GameRules.MAX_ATTEMPTS];
    private final CurrentGuess currentGuess = new CurrentGuess();
    private final Map<Color, JButton> colorButtons = new EnumMap<>(Color.class);
    private final JLabel statusLabel = new JLabel();
    private final JButton removeButton = new JButton("Letzte Farbe löschen");
    private final JButton submitButton = new JButton("Tipp prüfen");

    MastermindPanel(Game game, BooleanSupplier restartChoice, Runnable closeAction) {
        this.game = Objects.requireNonNull(game);
        this.restartChoice = Objects.requireNonNull(restartChoice);
        this.closeAction = Objects.requireNonNull(closeAction);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        add(createHeader(), BorderLayout.NORTH);
        add(createBoard(), BorderLayout.CENTER);
        add(createControls(), BorderLayout.SOUTH);
        resetBoard();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Mastermind", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24F));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel secretPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 4));
        secretPanel.add(new JLabel("Geheimcode:"));
        for (int index = 0; index < GameRules.CODE_LENGTH; index++) {
            secretPegs[index] = new PegView(SECRET_PEG_SIZE);
            secretPanel.add(secretPegs[index]);
        }

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(statusLabel);
        header.add(Box.createVerticalStrut(6));
        header.add(secretPanel);
        return header;
    }

    private JPanel createBoard() {
        JPanel board = new JPanel(new GridBagLayout());
        board.setBorder(BorderFactory.createTitledBorder("Spielbrett"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 6, 3, 6);
        constraints.anchor = GridBagConstraints.CENTER;

        for (int row = 0; row < GameRules.MAX_ATTEMPTS; row++) {
            constraints.gridx = 0;
            constraints.gridy = row;
            board.add(new JLabel("Versuch " + (row + 1)), constraints);

            JPanel guessPanel = new JPanel(new GridLayout(1, GameRules.CODE_LENGTH, 3, 0));
            for (int column = 0; column < GameRules.CODE_LENGTH; column++) {
                guessPegs[row][column] = new PegView(PEG_SIZE);
                guessPanel.add(guessPegs[row][column]);
            }
            constraints.gridx = 1;
            board.add(guessPanel, constraints);

            feedbackViews[row] = new FeedbackView();
            constraints.gridx = 2;
            board.add(feedbackViews[row], constraints);
        }
        return board;
    }

    private JPanel createControls() {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        JPanel palette = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        palette.setBorder(BorderFactory.createTitledBorder("Farbe wählen"));
        for (Color color : Color.values()) {
            JButton button = new JButton(ColorText.displayName(color));
            button.setToolTipText(ColorText.displayName(color));
            button.getAccessibleContext().setAccessibleName(ColorText.displayName(color));
            button.setBackground(SwingColorPalette.colorFor(color));
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.addActionListener(event -> selectColor(color));
            colorButtons.put(color, button);
            palette.add(button);
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        removeButton.addActionListener(event -> removeLastColor());
        submitButton.addActionListener(event -> submitGuess());
        actions.add(removeButton);
        actions.add(submitButton);
        controls.add(palette);
        controls.add(Box.createRigidArea(new Dimension(0, 4)));
        controls.add(actions);
        return controls;
    }

    private void selectColor(Color color) {
        if (game.getStatus() == GameStatus.ONGOING) {
            currentGuess.add(color);
            renderCurrentTurn();
        }
    }

    private void removeLastColor() {
        if (game.getStatus() == GameStatus.ONGOING) {
            currentGuess.removeLast();
            renderCurrentTurn();
        }
    }

    private void submitGuess() {
        if (game.getStatus() != GameStatus.ONGOING || !currentGuess.isComplete()) {
            return;
        }

        TurnResult result = game.submitGuess(currentGuess.toGuess());
        feedbackViews[rowFor(result)].showFeedback(result.getFeedback());
        currentGuess.clear();
        if (result.getStatus() == GameStatus.ONGOING) {
            renderCurrentTurn();
            return;
        }

        revealSecretCode();
        updateControls();
        if (restartChoice.getAsBoolean()) {
            game.startNewRound();
            resetBoard();
        } else {
            closeAction.run();
        }
    }

    private void resetBoard() {
        currentGuess.clear();
        for (PegView secretPeg : secretPegs) {
            secretPeg.showHidden();
        }
        for (int row = 0; row < GameRules.MAX_ATTEMPTS; row++) {
            for (int column = 0; column < GameRules.CODE_LENGTH; column++) {
                guessPegs[row][column].clear();
            }
            feedbackViews[row].clear();
        }
        renderCurrentTurn();
    }

    private void renderCurrentTurn() {
        int row = currentRow();
        if (game.getStatus() == GameStatus.ONGOING && row < GameRules.MAX_ATTEMPTS) {
            for (int column = 0; column < GameRules.CODE_LENGTH; column++) {
                if (column < currentGuess.size()) {
                    guessPegs[row][column].showColor(currentGuess.colorAt(column));
                } else {
                    guessPegs[row][column].clear();
                }
            }
        }
        updateStatus();
        updateControls();
    }

    private int currentRow() {
        return game.getAttemptsUsed();
    }

    private int rowFor(TurnResult result) {
        return result.getAttemptNumber() - 1;
    }

    private void revealSecretCode() {
        Color[] secretCode = game.revealSecretCode();
        for (int index = 0; index < secretCode.length; index++) {
            secretPegs[index].showColor(secretCode[index]);
        }
        statusLabel.setText(game.getStatus() == GameStatus.WON ? "Gewonnen!" : "Verloren!");
    }

    private void updateStatus() {
        if (game.getStatus() == GameStatus.ONGOING) {
            statusLabel.setText("Versuch " + (game.getAttemptsUsed() + 1) + " von " + GameRules.MAX_ATTEMPTS);
        }
    }

    private void updateControls() {
        boolean ongoing = game.getStatus() == GameStatus.ONGOING;
        boolean hasSpace = !currentGuess.isComplete();
        for (JButton colorButton : colorButtons.values()) {
            colorButton.setEnabled(ongoing && hasSpace);
        }
        removeButton.setEnabled(ongoing && !currentGuess.isEmpty());
        submitButton.setEnabled(ongoing && currentGuess.isComplete());
    }

    JButton getColorButton(Color color) {
        return colorButtons.get(color);
    }

    JButton getRemoveButton() {
        return removeButton;
    }

    JButton getSubmitButton() {
        return submitButton;
    }

    String getStatusText() {
        return statusLabel.getText();
    }

    PegView getGuessPeg(int row, int column) {
        return guessPegs[row][column];
    }

    PegView getSecretPeg(int index) {
        return secretPegs[index];
    }

    FeedbackView getFeedbackView(int row) {
        return feedbackViews[row];
    }
}
