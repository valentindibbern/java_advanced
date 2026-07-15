package ch.valentindibbern.mastermind;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CodeGeneratorTest {
    @Test
    void generatesFourKnownColoursFromSeededRandom() {
        CodeGenerator generator = new CodeGenerator(new Random(17));

        Color[] code = generator.generateCode();

        assertEquals(Game.CODE_LENGTH, code.length);
        for (Color color : code) {
            assertNotNull(color);
        }
    }

    @Test
    void createsReproducibleCodesForSameSeed() {
        Color[] firstCode = new CodeGenerator(new Random(42)).generateCode();
        Color[] secondCode = new CodeGenerator(new Random(42)).generateCode();

        assertArrayEquals(firstCode, secondCode);
    }

    @Test
    void allowsRepeatedColours() {
        CodeGenerator generator = new CodeGenerator(new FixedRandom(2, 2, 2, 2));

        assertArrayEquals(
                new Color[]{Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE},
                generator.generateCode()
        );
    }

    @Test
    void rejectsMissingRandomSource() {
        assertThrows(NullPointerException.class, () -> new CodeGenerator(null));
    }

    private static final class FixedRandom extends Random {
        private static final long serialVersionUID = 1L;

        private final int[] values;
        private int index;

        private FixedRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            return values[index++ % values.length];
        }
    }
}
