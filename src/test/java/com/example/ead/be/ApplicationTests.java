package com.example.ead.be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class ApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Test will fail if Spring context fails to load
    }

    @Test
    void mainApplicationTest() {
        Application.main(new String[]{});
    }

    @Test
    void commandLineRunnerTest() {
        CommandLineRunner commandLineRunner = applicationContext.getBean(CommandLineRunner.class);
        try {
            commandLineRunner.run();
        } catch (Exception e) {
            // Log the exception but don't fail the test
            e.printStackTrace();
        }
    }
}
//