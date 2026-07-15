package ch.valentindibbern.mastermind;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class FeedbackView extends JComponent {
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
        int marker = 13;
        for (int index = 0; index < Game.CODE_LENGTH; index++) {
            int x = 4 + (index % 2) * 19;
            int y = 4 + (index / 2) * 19;
            if (index < blackMarks) {
                graphics2D.setColor(java.awt.Color.BLACK);
            } else if (index < blackMarks + whiteMarks) {
                graphics2D.setColor(java.awt.Color.WHITE);
            } else {
                graphics2D.setColor(new java.awt.Color(225, 225, 225));
            }
            graphics2D.fillOval(x, y, marker, marker);
            graphics2D.setColor(new java.awt.Color(55, 55, 55));
            graphics2D.drawOval(x, y, marker, marker);
        }
        graphics2D.dispose();
    }
}
