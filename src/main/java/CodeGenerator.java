import enums.Color;

import java.util.Random;

public class CodeGenerator {

    private final Random random;

    CodeGenerator() {
        this.random = new Random();
    }

    public Color[] generateCode(int length) {
        Color[] colors = new Color[length];
        for (int i = 0; i < length; i++) {
            colors[i] = Color.fromNumber(random.nextInt(Color.values().length));
        }
        return colors;
    }
}
