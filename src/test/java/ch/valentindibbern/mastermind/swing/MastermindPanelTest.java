package ch.valentindibbern.mastermind.swing;

import ch.valentindibbern.mastermind.domain.Color;
import ch.valentindibbern.mastermind.domain.FeedbackEvaluator;
import ch.valentindibbern.mastermind.domain.Game;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MastermindPanelTest {
    @Test
    void allowsRepeatedColoursAndEnablesSubmitOnlyForACompleteGuess() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            MastermindPanel panel = panel(false, new AtomicBoolean());
            panel.getColorButton(Color.RED).doClick();
            panel.getColorButton(Color.RED).doClick();
            panel.getColorButton(Color.BLUE).doClick();

            assertFalse(panel.getSubmitButton().isEnabled());
            panel.getColorButton(Color.YELLOW).doClick();

            assertTrue(panel.getSubmitButton().isEnabled());
            assertEquals(Color.RED, panel.getGuessPeg(0, 1).getDisplayedColor());
        });
    }

    @Test
    void revealsSecretAndClosesAfterWinningWhenNoRestartIsChosen() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AtomicBoolean closed = new AtomicBoolean();
            MastermindPanel panel = panel(false, closed);
            for (Color color : new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}) {
                panel.getColorButton(color).doClick();
            }
            panel.getSubmitButton().doClick();

            assertEquals("Gewonnen!", panel.getStatusText());
            assertEquals(Color.RED, panel.getSecretPeg(0).getDisplayedColor());
            assertTrue(closed.get());
        });
    }

    @Test
    void paintsViewsSafelyAtVerySmallSizes() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PegView pegView = new PegView(1);
            FeedbackView feedbackView = new FeedbackView();
            pegView.setSize(1, 1);
            feedbackView.setSize(1, 1);
            BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
            pegView.paint(image.getGraphics());
            feedbackView.paint(image.getGraphics());
            assertTrue(true);
        });
    }

    private static MastermindPanel panel(boolean restart, AtomicBoolean closed) {
        Game game = new Game(
                () -> new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW},
                new FeedbackEvaluator()
        );
        return new MastermindPanel(game, () -> restart, () -> closed.set(true));
    }
}
