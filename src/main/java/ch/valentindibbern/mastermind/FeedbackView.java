package ch.valentindibbern.mastermind;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class FeedbackView extends JComponent {
    private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(235, 235, 235);
    private static final java.awt.Color EMPTY_MARK_COLOR = new java.awt.Color(210, 210, 210);
    private static final java.awt.Color MARK_BORDER_COLOR = new java.awt.Color(55, 55, 55);
    private int blackMarks;
    private int whiteMarks;

    FeedbackView() {
        setPreferredSize(new Dimension(42, 42));
        setMinimumSize(new Dimension(42, 42));
        setToolTipText("Noch keine Rückmeldung");
    }

    void showFeedback(Feedback feedback) {
        blackMarks = feedback.getBlackMarks();
        whiteMarks = feedback.getWhiteMarks();
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
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        int marker = 13;
        for (int index = 0; index < Game.CODE_LENGTH; index++) {
            int x = 4 + (index % 2) * 19;
            int y = 4 + (index / 2) * 19;
            if (index < blackMarks) {
                graphics2D.setColor(java.awt.Color.BLACK);
            } else if (index < blackMarks + whiteMarks) {
                graphics2D.setColor(java.awt.Color.WHITE);
            } else {
                graphics2D.setColor(EMPTY_MARK_COLOR);
            }
            graphics2D.fillOval(x, y, marker, marker);
            graphics2D.setColor(MARK_BORDER_COLOR);
            graphics2D.setStroke(new BasicStroke(2F));
            graphics2D.drawOval(x, y, marker, marker);
        }
        graphics2D.dispose();
    }
}
