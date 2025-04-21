package com.example.ead.be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

public class ApplicationTest {

    @Test
    public void testApplicationConstructor() {
        assertDoesNotThrow(() -> new Application());
    }

    @Test
    public void testApplicationMainMethod() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            // Mock the static method call to SpringApplication.run
            mocked.when(() -> SpringApplication.run(Application.class, new String[]{}))
                  .thenReturn(null);
            
            // Execute the main method which should now use our mocked SpringApplication.run
            Application.main(new String[]{});
            
            // Verify that the run method was called with expected parameters
            mocked.verify(() -> SpringApplication.run(Application.class, new String[]{}));
        }
    }
} 