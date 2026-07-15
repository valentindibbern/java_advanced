package ch.valentindibbern.mastermind.domain;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomSecretCodeProviderTest {
    @Test
    void createsDeterministicCodesForTheSameSeed() {
        Color[] first = new RandomSecretCodeProvider(new Random(42)).createSecretCode();
        Color[] second = new RandomSecretCodeProvider(new Random(42)).createSecretCode();

        assertArrayEquals(first, second);
        assertEquals(GameRules.CODE_LENGTH, first.length);
    }

    @Test
    void rejectsANullRandomGenerator() {
        assertThrows(NullPointerException.class, () -> new RandomSecretCodeProvider(null));
    }
}
