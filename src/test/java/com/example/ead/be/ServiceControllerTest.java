package com.example.ead.be;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Simple test for ServiceController without mocking
 */
public class ServiceControllerTest {

    private ServiceController controller = new ServiceController();

    @Test
    public void testIndex() {
        String response = controller.index();
        assertEquals("Greetings from EAD CA2 Template project 2023-24!", response);
    }

    @Test
    public void testConstructor() {
        // Just verify that the constructor doesn't throw any exceptions
        assertDoesNotThrow(() -> new ServiceController());
    }
} 