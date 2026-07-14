import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnsiTest {
    @Test
    void wrapsTextWithColourAndReset() {
        assertEquals("\u001B[31mRot\u001B[0m", Ansi.colour("\u001B[31m", "Rot"));
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> Ansi.colour(null, "Rot"));
        assertThrows(NullPointerException.class, () -> Ansi.colour("\u001B[31m", null));
    }
}
