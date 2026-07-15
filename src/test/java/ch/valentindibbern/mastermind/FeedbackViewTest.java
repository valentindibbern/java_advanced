package ch.valentindibbern.mastermind;

import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeedbackViewTest {
    @Test
    void drawsWhiteMarkWithDarkBorderOnGreyBackground() {
        FeedbackView view = new FeedbackView();
        view.showFeedback(new Feedback(0, 1));
        view.setSize(42, 42);
        BufferedImage image = new BufferedImage(42, 42, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        view.paint(graphics);
        graphics.dispose();

        assertEquals(java.awt.Color.WHITE.getRGB(), image.getRGB(10, 10));
        assertEquals(new java.awt.Color(55, 55, 55).getRGB(), image.getRGB(4, 10));
        assertEquals(new java.awt.Color(235, 235, 235).getRGB(), image.getRGB(40, 20));
    }
}
