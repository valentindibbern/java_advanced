package ch.valentindibbern.mastermind.swing;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.ui.ColorText;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;

final class PegView extends JComponent {
    private static final int INSET = 6;
    private static final java.awt.Color HIDDEN_COLOR = new java.awt.Color(95, 95, 95);
    private static final java.awt.Color EMPTY_COLOR = new java.awt.Color(235, 235, 235);
    private static final java.awt.Color BORDER_COLOR = new java.awt.Color(55, 55, 55);
    private Color color;
    private boolean hidden;

    PegView(int size) {
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setToolTipText("Leere Farbposition");
    }

    void showColor(Color color) {
        this.color = Objects.requireNonNull(color);
        hidden = false;
        setToolTipText(ColorText.displayName(color));
        repaint();
    }

    void showHidden() {
        color = null;
        hidden = true;
        setToolTipText("Verdeckte Farbe");
        repaint();
    }

    void clear() {
        color = null;
        hidden = false;
        setToolTipText("Leere Farbposition");
        repaint();
    }

    Color getDisplayedColor() {
        return color;
    }

    boolean isHidden() {
        return hidden;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        try {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int diameter = Math.max(0, Math.min(getWidth(), getHeight()) - INSET);
            int x = Math.max(0, (getWidth() - diameter) / 2);
            int y = Math.max(0, (getHeight() - diameter) / 2);
            graphics2D.setColor(hidden ? HIDDEN_COLOR : color == null ? EMPTY_COLOR : SwingColorPalette.colorFor(color));
            graphics2D.fillOval(x, y, diameter, diameter);
            graphics2D.setColor(BORDER_COLOR);
            graphics2D.drawOval(x, y, diameter, diameter);
            if (hidden) {
                graphics2D.setColor(java.awt.Color.WHITE);
                graphics2D.drawString("?", getWidth() / 2 - 3, getHeight() / 2 + 5);
            }
        } finally {
            graphics2D.dispose();
        }
    }
}
