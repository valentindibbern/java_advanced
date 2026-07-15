package ch.valentindibbern.mastermind.domain;

import java.util.Objects;
import java.util.Random;

public final class RandomSecretCodeProvider implements SecretCodeProvider {
    private final Random random;

    public RandomSecretCodeProvider(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Color[] createSecretCode() {
        Color[] code = new Color[GameRules.CODE_LENGTH];
        Color[] colors = Color.values();
        for (int index = 0; index < code.length; index++) {
            code[index] = colors[random.nextInt(colors.length)];
        }
        return code;
    }
}
