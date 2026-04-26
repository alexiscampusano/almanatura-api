package com.almanatura.api;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that need a real MySQL instance.
 *
 * <p>Subclasses get a fully booted Spring context wired against a throw-away MySQL 8 container,
 * with Flyway migrations applied automatically. Use this for repository tests and end-to-end
 * controller tests that need the real database engine.
 *
 * <p>Unit tests that only need a JPA layer should keep using {@code application-test.properties}
 * (in-memory H2) for speed.
 */
@Tag("integration")
@ActiveProfiles("integration")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @Container @ServiceConnection
    protected static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0.36")
                    .withDatabaseName("almanatura_it")
                    .withUsername("almanatura")
                    .withPassword("almanatura")
                    .withReuse(true);
}
