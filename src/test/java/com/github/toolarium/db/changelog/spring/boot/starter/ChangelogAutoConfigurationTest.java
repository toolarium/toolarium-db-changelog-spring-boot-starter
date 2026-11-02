/*
 * ChangelogAutoConfigurationTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 * Tests for {@link ChangelogAutoConfiguration}.
 *
 * @author patrick
 */
public class ChangelogAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DataSourceAutoConfiguration.class,
                    ChangelogAutoConfiguration.class))
            .withPropertyValues(
                    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                    "spring.datasource.driver-class-name=org.h2.Driver",
                    "toolarium.db.changelog.change-log=classpath:db/changelog/changelog-master.db.yaml");


    /**
     * Test that auto-configuration creates the changelog runner bean when a DataSource is present.
     */
    @Test
    void testAutoConfigurationCreatesRunner() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("changelogRunner"));
            assertNotNull(context.getBean("changelogRunner"));
        });
    }


    /**
     * Test that auto-configuration creates the changelog initializer bean.
     */
    @Test
    void testAutoConfigurationCreatesInitializer() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("changelogInitializer"));
            assertNotNull(context.getBean(ChangelogInitializer.class));
        });
    }


    /**
     * Test that auto-configuration is disabled when the enabled property is false.
     */
    @Test
    void testDisabledByProperty() {
        contextRunner
                .withPropertyValues("toolarium.db.changelog.enabled=false")
                .run(context -> {
                    assertFalse(context.containsBean("changelogRunner"));
                });
    }
}
