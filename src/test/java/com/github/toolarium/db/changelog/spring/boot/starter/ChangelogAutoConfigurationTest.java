/*
 * ChangelogAutoConfigurationTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.github.toolarium.db.changelog.IChangelogRunner;
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
                    "spring.datasource.url=jdbc:h2:mem:testdb_" + System.nanoTime(),
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
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        DataSourceAutoConfiguration.class,
                        ChangelogAutoConfiguration.class))
                .withPropertyValues(
                        "spring.datasource.url=jdbc:h2:mem:disableddb_" + System.nanoTime(),
                        "spring.datasource.driver-class-name=org.h2.Driver",
                        "toolarium.db.changelog.enabled=false")
                .run(context -> {
                    assertFalse(context.containsBean("changelogInitializer"));
                });
    }


    /**
     * Test that user-provided IChangelogRunner bean takes precedence.
     */
    @Test
    void testConditionalOnMissingBean() {
        IChangelogRunner customRunner = mock(IChangelogRunner.class);
        contextRunner
                .withBean("changelogRunner", IChangelogRunner.class, () -> customRunner)
                .run(context -> {
                    assertSame(customRunner, context.getBean(IChangelogRunner.class));
                });
    }


    /**
     * Test that auto-configuration does not activate without a DataSource.
     */
    @Test
    void testNoBeansWithoutDataSource() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ChangelogAutoConfiguration.class))
                .run(context -> {
                    assertFalse(context.containsBean("changelogRunner"));
                    assertFalse(context.containsBean("changelogInitializer"));
                });
    }


    /**
     * Test that DependsOn post processors are registered when JDBC is on the classpath.
     */
    @Test
    void testDependsOnPostProcessorsRegistered() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("changelogJdbcDependsOnPostProcessor"));
            assertTrue(context.containsBean("changelogNamedJdbcDependsOnPostProcessor"));
        });
    }
}
