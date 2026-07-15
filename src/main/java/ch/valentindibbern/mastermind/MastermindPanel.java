package ch.valentindibbern.mastermind;

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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

final class MastermindPanel extends JPanel {
    private final GameSession gameSession;
    private final RoundEndPrompt roundEndPrompt;
    private final Runnable closeAction;
    private final PegView[] secretPegs = new PegView[Game.CODE_LENGTH];
    private final PegView[][] guessPegs = new PegView[Game.MAX_ATTEMPTS][Game.CODE_LENGTH];
    private final FeedbackView[] feedbackViews = new FeedbackView[Game.MAX_ATTEMPTS];
    // Holds the colours selected for the current, not yet submitted guess.
    private final Color[] selectedGuess = new Color[Game.CODE_LENGTH];
    private final Map<Color, JButton> colorButtons = new EnumMap<>(Color.class);
    private final JLabel statusLabel = new JLabel();
    private final JButton removeButton = new JButton("Letzte Farbe löschen");
    private final JButton submitButton = new JButton("Tipp prüfen");
    private int selectedCount;

    MastermindPanel(GameSession gameSession, RoundEndPrompt roundEndPrompt, Runnable closeAction) {
        this.gameSession = Objects.requireNonNull(gameSession);
        this.roundEndPrompt = Objects.requireNonNull(roundEndPrompt);
        this.closeAction = Objects.requireNonNull(closeAction);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
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
        for (int index = 0; index < Game.CODE_LENGTH; index++) {
            secretPegs[index] = new PegView(36);
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

        for (int row = 0; row < Game.MAX_ATTEMPTS; row++) {
            constraints.gridx = 0;
            constraints.gridy = row;
            board.add(new JLabel("Versuch " + (row + 1)), constraints);

            JPanel guessPanel = new JPanel(new GridLayout(1, Game.CODE_LENGTH, 3, 0));
            for (int column = 0; column < Game.CODE_LENGTH; column++) {
                guessPegs[row][column] = new PegView(30);
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
            JButton button = new JButton(color.displayName());
            button.setActionCommand(color.name());
            button.setToolTipText(color.displayName());
            button.getAccessibleContext().setAccessibleName(color.displayName());
            button.setBackground(SwingPalette.toAwtColor(color));
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
        if (selectedCount == Game.CODE_LENGTH || gameSession.getStatus() != GameStatus.ONGOING) {
            return;
        }

        selectedGuess[selectedCount] = color;
        guessPegs[gameSession.getAttemptsUsed()][selectedCount].showColor(color);
        selectedCount++;
        updateControls();
    }

    private void removeLastColor() {
        if (selectedCount == 0 || gameSession.getStatus() != GameStatus.ONGOING) {
            return;
        }

        selectedCount--;
        selectedGuess[selectedCount] = null;
        guessPegs[gameSession.getAttemptsUsed()][selectedCount].clear();
        updateControls();
    }

    private void submitGuess() {
        if (selectedCount != Game.CODE_LENGTH || gameSession.getStatus() != GameStatus.ONGOING) {
            return;
        }

        // Submit a copy because the selection array is cleared for the next attempt.
        TurnResult result = gameSession.submitGuess(selectedGuess.clone());
        feedbackViews[result.getAttemptNumber() - 1].showFeedback(result.getFeedback());
        selectedCount = 0;
        Arrays.fill(selectedGuess, null);

        if (result.getStatus() == GameStatus.ONGOING) {
            updateStatus();
            updateControls();
            return;
        }

        revealSecretCode();
        updateControls();
        if (roundEndPrompt.requestRestart(this, result.getStatus(), gameSession.revealSecretCode())) {
            gameSession.startNewRound();
            resetBoard();
        } else {
            closeAction.run();
        }
    }

    private void resetBoard() {
        selectedCount = 0;
        for (int index = 0; index < selectedGuess.length; index++) {
            selectedGuess[index] = null;
            secretPegs[index].showHidden();
        }
        for (int row = 0; row < Game.MAX_ATTEMPTS; row++) {
            for (int column = 0; column < Game.CODE_LENGTH; column++) {
                guessPegs[row][column].clear();
            }
            feedbackViews[row].clear();
        }
        updateStatus();
        updateControls();
    }

    private void revealSecretCode() {
        Color[] secretCode = gameSession.revealSecretCode();
        for (int index = 0; index < secretCode.length; index++) {
            secretPegs[index].showColor(secretCode[index]);
        }
        statusLabel.setText(gameSession.getStatus() == GameStatus.WON ? "Gewonnen!" : "Verloren!");
    }

    private void updateStatus() {
        statusLabel.setText("Versuch " + (gameSession.getAttemptsUsed() + 1) + " von " + Game.MAX_ATTEMPTS);
    }

    private void updateControls() {
        boolean ongoing = gameSession.getStatus() == GameStatus.ONGOING;
        boolean hasSpace = selectedCount < Game.CODE_LENGTH;
        for (JButton colorButton : colorButtons.values()) {
            colorButton.setEnabled(ongoing && hasSpace);
        }
        removeButton.setEnabled(ongoing && selectedCount > 0);
        submitButton.setEnabled(ongoing && selectedCount == Game.CODE_LENGTH);
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

    PegView getSecretPeg(int index) {return secretPegs[index];}

    FeedbackView getFeedbackView(int row) {
        return feedbackViews[row];
    }
}
