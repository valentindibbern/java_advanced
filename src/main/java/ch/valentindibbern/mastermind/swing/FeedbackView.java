package ch.valentindibbern.mastermind.swing;

import ch.valentindibbern.mastermind.domain.Feedback;
import ch.valentindibbern.mastermind.domain.GameRules;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;

final class FeedbackView extends JComponent {
    private static final int SIZE = 42;
    private static final int MARKER_SIZE = 13;
    private static final int MARKER_OFFSET = 4;
    private static final int MARKER_DISTANCE = 19;
    private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(235, 235, 235);
    private static final java.awt.Color EMPTY_MARK_COLOR = new java.awt.Color(210, 210, 210);
    private static final java.awt.Color MARK_BORDER_COLOR = new java.awt.Color(55, 55, 55);
    private int blackMarks;
    private int whiteMarks;

    FeedbackView() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setToolTipText("Noch keine Rückmeldung");
    }

    void showFeedback(Feedback feedback) {
        Feedback nonNullFeedback = Objects.requireNonNull(feedback);
        blackMarks = nonNullFeedback.blackMarks();
        whiteMarks = nonNullFeedback.whiteMarks();
        setToolTipText("Schwarz: " + blackMarks + ", Weiss: " + whiteMarks);
        repaint();
    }

    void clear() {
        blackMarks = 0;
        whiteMarks = 0;
        setToolTipText("Noch keine Rückmeldung");
        repaint();
    }

    int getBlackMarks() {
        return blackMarks;
    }

    int getWhiteMarks() {
        return whiteMarks;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        try {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(BACKGROUND_COLOR);
            graphics2D.fillRoundRect(0, 0, Math.max(0, getWidth()), Math.max(0, getHeight()), 8, 8);
            graphics2D.setStroke(new BasicStroke(2F));
            for (int index = 0; index < GameRules.CODE_LENGTH; index++) {
                int x = MARKER_OFFSET + (index % 2) * MARKER_DISTANCE;
                int y = MARKER_OFFSET + (index / 2) * MARKER_DISTANCE;
                graphics2D.setColor(index < blackMarks ? java.awt.Color.BLACK
                        : index < blackMarks + whiteMarks ? java.awt.Color.WHITE : EMPTY_MARK_COLOR);
                graphics2D.fillOval(x, y, MARKER_SIZE, MARKER_SIZE);
                graphics2D.setColor(MARK_BORDER_COLOR);
                graphics2D.drawOval(x, y, MARKER_SIZE, MARKER_SIZE);
            }
        } finally {
            graphics2D.dispose();
        }
    }
}
