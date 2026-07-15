package ch.valentindibbern.mastermind;

import java.util.Objects;
import java.util.Random;

public class CodeGenerator {
    private final Random random;

    public CodeGenerator(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    public Color[] generateCode() {
        Color[] code = new Color[Game.CODE_LENGTH];
        Color[] colors = Color.values();

        // Jede Position wird unabhängig gewählt; Wiederholungen sind erlaubt.
        for (int index = 0; index < code.length; index++) {
            code[index] = colors[random.nextInt(colors.length)];
        }

        return code;
    }
}
