// package com.example.ead.be;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// import java.util.Arrays;

// /**
//  * Simple test for ServiceController without mocking
//  */
// public class ServiceControllerTest {

//     private ServiceController controller = new ServiceController();

//     @Test
//     public void testIndex() {
//         String response = controller.index();
//         assertEquals("Greetings from EAD CA2 Template project 2023-24!", response);
//     }

//     @Test
//     public void testConstructor() {
//         // Just verify that the constructor doesn't throw any exceptions
//         assertDoesNotThrow(() -> new ServiceController());
//     }
// } 

// package com.example.ead.be;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*; // Import Mockito methods

// // Use MockitoExtension to enable mocking
// @ExtendWith(MockitoExtension.class)
// public class ServiceControllerTest {

//     // Create a mock instance of the Persistence dependency
//     @Mock
//     private Persistence mockPersistence; // Assuming ServiceController depends on Persistence

//     // Inject the mock into the ServiceController instance being tested
//     @InjectMocks
//     private ServiceController controller; // This instance will have mockPersistence injected

//     @Test
//     public void testIndex() {
//         // If index() called methods on Persistence, you would define mock behavior here
//         // when(mockPersistence.someMethod()).thenReturn(someValue);

//         String response = controller.index();

//         assertEquals("Greetings from EAD CA2 Template project 2023-24!", response);

//         // If index() called methods on Persistence, you would verify calls here
//         // verify(mockPersistence).someMethod();
//     }

//     // No need for a separate testConstructor test when using MockitoExtension and @InjectMocks
// }

// NOTE: For @Mock and @InjectMocks to work, your ServiceController needs to
// accept Persistence as a dependency, typically like this:
/*
@Service // Or @RestController
public class ServiceController {

    private final Persistence persistence; // Make it final

    // Use constructor injection (recommended)
    // Spring will inject the Persistence bean (or your mock) here
    @Autowired // Optional in recent Spring versions if only one constructor
    public ServiceController(Persistence persistence) {
        this.persistence = persistence;
    }

    public String index() {
        // Use the injected persistence object
        // persistence.someOperation();
        return "Greetings from EAD CA2 Template project 2023-24!";
    }
    // ... other methods
}
*/


package com.example.ead.be;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat; // Using AssertJ for Spring Boot tests

// Use @SpringBootTest to load the full application context
// webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT starts an actual embedded server
@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceControllerTest {

    // Inject the controller if you want to test its methods directly (less common for web endpoints)
    // @Autowired
    // private ServiceController controller;

    // Inject a TestRestTemplate to make actual HTTP requests to the running embedded server
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testIndexEndpoint() {
        // Make an actual HTTP GET request to the / endpoint
        String response = this.restTemplate.getForObject("http://localhost:" + port + "/", String.class);

        // Use assertThat from AssertJ for better assertions
        assertThat(response).isEqualTo("Greetings from EAD CA2 Template project 2023-24!");
    }

    // No need for a separate testConstructor test when using Spring Boot context
    // @Test
    // public void testConstructor() { ... }
}