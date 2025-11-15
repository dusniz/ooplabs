package ru.ssau.tk.enjoyers.ooplabs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
