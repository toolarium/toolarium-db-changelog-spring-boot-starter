/*
 * ChangelogActuatorAutoConfiguration.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter.actuator;

import com.github.toolarium.db.changelog.IChangelogRunner;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Auto-configuration for toolarium-db-changelog actuator endpoints.
 * Provides a health indicator and an info contributor when Spring Boot Actuator is on the classpath.
 *
 * @author patrick
 */
@Configuration
@ConditionalOnClass(HealthIndicator.class)
@ConditionalOnBean(IChangelogRunner.class)
@AutoConfigureAfter(name = "com.github.toolarium.db.changelog.spring.boot.starter.ChangelogAutoConfiguration")
public class ChangelogActuatorAutoConfiguration {

    /**
     * Create the health indicator bean.
     *
     * @param changelogRunner the changelog runner
     * @return the health indicator
     */
    @Bean
    @ConditionalOnMissingBean(name = "changelogHealthIndicator")
    public ChangelogHealthIndicator changelogHealthIndicator(IChangelogRunner changelogRunner) {
        return new ChangelogHealthIndicator(changelogRunner);
    }


    /**
     * Create the info contributor bean.
     *
     * @param changelogRunner the changelog runner
     * @return the info contributor
     */
    @Bean
    @ConditionalOnMissingBean(name = "changelogInfoContributor")
    public ChangelogInfoContributor changelogInfoContributor(IChangelogRunner changelogRunner) {
        return new ChangelogInfoContributor(changelogRunner);
    }
}
