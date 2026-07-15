package ch.valentindibbern.mastermind;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class PegView extends JComponent {
    private Color color;
    private boolean hidden;

    PegView(int size) {
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setToolTipText("Leere Farbposition");
    }

    void showColor(Color color) {
        this.color = color;
        hidden = false;
        setToolTipText(color.displayName());
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
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int diameter = Math.min(getWidth(), getHeight()) - 6;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        if (hidden) {
            graphics2D.setColor(new java.awt.Color(95, 95, 95));
        } else if (color == null) {
            graphics2D.setColor(new java.awt.Color(235, 235, 235));
        } else {
            graphics2D.setColor(SwingPalette.toAwtColor(color));
        }
        graphics2D.fillOval(x, y, diameter, diameter);
        graphics2D.setColor(new java.awt.Color(55, 55, 55));
        graphics2D.drawOval(x, y, diameter, diameter);

        if (hidden) {
            graphics2D.setColor(java.awt.Color.WHITE);
            graphics2D.drawString("?", getWidth() / 2 - 3, getHeight() / 2 + 5);
        }
        graphics2D.dispose();
    }
}
