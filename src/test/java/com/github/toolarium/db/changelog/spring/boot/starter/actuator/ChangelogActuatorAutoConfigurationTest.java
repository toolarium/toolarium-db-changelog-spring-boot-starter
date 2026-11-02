/*
 * ChangelogActuatorAutoConfigurationTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter.actuator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.github.toolarium.db.changelog.IChangelogRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 * Tests for {@link ChangelogActuatorAutoConfiguration}.
 *
 * @author patrick
 */
public class ChangelogActuatorAutoConfigurationTest {

    /**
     * Test that health indicator and info contributor are created when runner bean exists.
     */
    @Test
    void testActuatorBeansCreated() {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ChangelogActuatorAutoConfiguration.class))
                .withBean("changelogRunner", IChangelogRunner.class, () -> runner)
                .run(context -> {
                    assertTrue(context.containsBean("changelogHealthIndicator"));
                    assertTrue(context.containsBean("changelogInfoContributor"));
                });
    }


    /**
     * Test that actuator beans are not created without a changelog runner.
     */
    @Test
    void testNoBeansWithoutRunner() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ChangelogActuatorAutoConfiguration.class))
                .run(context -> {
                    assertFalse(context.containsBean("changelogHealthIndicator"));
                    assertFalse(context.containsBean("changelogInfoContributor"));
                });
    }
}
