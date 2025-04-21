package com.example.ead.be;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    @Test
    public void testApplicationConstructor() {
        assertDoesNotThrow(() -> new Application());
    }

    @Test
    public void testApplicationMainMethod() {
        // Just make sure the main method doesn't throw an exception when called with empty args
        assertDoesNotThrow(() -> Application.main(new String[]{}));
    }
} 