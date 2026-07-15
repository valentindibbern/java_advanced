package ch.valentindibbern.mastermind;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MastermindPanelTest {
    @Test
    void enablesSubmissionOnlyAfterFourSelectedColours() throws Exception {
        runOnEventDispatchThread(() -> {
            MastermindPanel panel = panelWithCodes((parent, status, secret) -> false, () -> { }, 0, 1, 2, 3);

            assertFalse(panel.getSubmitButton().isEnabled());
            assertFalse(panel.getRemoveButton().isEnabled());
            click(panel, Color.RED);
            click(panel, Color.RED);
            click(panel, Color.BLUE);

            assertEquals(Color.RED, panel.getGuessPeg(0, 0).getDisplayedColor());
            assertEquals(Color.RED, panel.getGuessPeg(0, 1).getDisplayedColor());
            assertFalse(panel.getSubmitButton().isEnabled());
            assertTrue(panel.getRemoveButton().isEnabled());

            click(panel, Color.YELLOW);

            assertTrue(panel.getSubmitButton().isEnabled());
            assertFalse(panel.getColorButton(Color.PURPLE).isEnabled());
        });
    }

    @Test
    void removesOnlyTheLastSelectedColour() throws Exception {
        runOnEventDispatchThread(() -> {
            MastermindPanel panel = panelWithCodes((parent, status, secret) -> false, () -> { }, 0, 1, 2, 3);
            click(panel, Color.RED);
            click(panel, Color.GREEN);

            panel.getRemoveButton().doClick();

            assertEquals(Color.RED, panel.getGuessPeg(0, 0).getDisplayedColor());
            assertEquals(null, panel.getGuessPeg(0, 1).getDisplayedColor());
            assertTrue(panel.getColorButton(Color.PURPLE).isEnabled());
            assertFalse(panel.getSubmitButton().isEnabled());
        });
    }

    @Test
    void displaysGuessAndFeedbackThenActivatesNextAttempt() throws Exception {
        runOnEventDispatchThread(() -> {
            MastermindPanel panel = panelWithCodes((parent, status, secret) -> false, () -> { }, 0, 1, 2, 3);
            click(panel, Color.RED);
            click(panel, Color.BLUE);
            click(panel, Color.GREEN);
            click(panel, Color.ORANGE);

            panel.getSubmitButton().doClick();

            assertEquals(Color.RED, panel.getGuessPeg(0, 0).getDisplayedColor());
            assertEquals(Color.BLUE, panel.getGuessPeg(0, 1).getDisplayedColor());
            assertEquals(1, panel.getFeedbackView(0).getBlackMarks());
            assertEquals(2, panel.getFeedbackView(0).getWhiteMarks());
            assertEquals("Versuch 2 von 7", panel.getStatusText());
            assertEquals(null, panel.getGuessPeg(1, 0).getDisplayedColor());
            assertTrue(panel.getColorButton(Color.RED).isEnabled());
        });
    }

    @Test
    void revealsSecretAndClosesWhenRoundEndPromptDeclinesRestart() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        runOnEventDispatchThread(() -> {
            MastermindPanel panel = panelWithCodes((parent, status, secret) -> false, () -> closed.set(true), 0, 1, 2, 3);
            submitExactGuess(panel);

            assertTrue(closed.get());
            assertEquals("Gewonnen!", panel.getStatusText());
            assertEquals(Color.RED, panel.getSecretPeg(0).getDisplayedColor());
            assertFalse(panel.getSecretPeg(0).isHidden());
            assertFalse(panel.getSubmitButton().isEnabled());
            assertFalse(panel.getColorButton(Color.RED).isEnabled());
        });
    }

    @Test
    void startsFreshRoundWhenRoundEndPromptAcceptsRestart() throws Exception {
        AtomicBoolean prompted = new AtomicBoolean();
        runOnEventDispatchThread(() -> {
            MastermindPanel panel = panelWithCodes(
                    (parent, status, secret) -> {
                        prompted.set(true);
                        return true;
                    },
                    () -> { },
                    0, 1, 2, 3, 4, 5, 0, 1
            );
            submitExactGuess(panel);

            assertTrue(prompted.get());
            assertEquals("Versuch 1 von 7", panel.getStatusText());
            assertTrue(panel.getSecretPeg(0).isHidden());
            assertEquals(null, panel.getGuessPeg(0, 0).getDisplayedColor());
            assertEquals(0, panel.getFeedbackView(0).getBlackMarks());
            assertTrue(panel.getColorButton(Color.RED).isEnabled());
            assertFalse(panel.getSubmitButton().isEnabled());
        });
    }

    private static void submitExactGuess(MastermindPanel panel) {
        click(panel, Color.RED);
        click(panel, Color.GREEN);
        click(panel, Color.BLUE);
        click(panel, Color.YELLOW);
        panel.getSubmitButton().doClick();
    }

    private static void click(MastermindPanel panel, Color color) {
        panel.getColorButton(color).doClick();
    }

    private static MastermindPanel panelWithCodes(
            RoundEndPrompt prompt,
            Runnable closeAction,
            int... secretNumbers
    ) {
        GameSession session = new GameSession(
                new CodeGenerator(new SequenceRandom(secretNumbers)),
                new FeedbackEvaluator()
        );
        return new MastermindPanel(session, prompt, closeAction);
    }

    private static void runOnEventDispatchThread(Runnable action) throws Exception {
        SwingUtilities.invokeAndWait(action);
    }

    private static final class SequenceRandom extends Random {
        private static final long serialVersionUID = 1L;
        private final int[] values;
        private int index;

        private SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            return values[index++];
        }
    }
}
