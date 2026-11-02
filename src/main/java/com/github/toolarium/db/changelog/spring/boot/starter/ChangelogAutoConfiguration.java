/*
 * ChangelogAutoConfiguration.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import com.github.toolarium.db.changelog.ChangelogRunnerBuilder;
import com.github.toolarium.db.changelog.IChangelogRunner;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Spring Boot auto-configuration for toolarium-db-changelog.
 * Configures an {@link IChangelogRunner} bean and runs migrations on startup.
 *
 * @author patrick
 */
@Configuration
@ConditionalOnClass(IChangelogRunner.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = "toolarium.db.changelog", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ChangelogProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class ChangelogAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ChangelogAutoConfiguration.class);


    /**
     * Create the {@link IChangelogRunner} bean.
     *
     * @param dataSource the data source from the Spring context
     * @param properties the changelog properties
     * @return the configured changelog runner
     */
    @Bean
    @ConditionalOnMissingBean
    public IChangelogRunner changelogRunner(DataSource dataSource, ChangelogProperties properties) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating changelog runner with changelog path: " + properties.getChangeLog());
        }

        ChangelogRunnerBuilder builder = ChangelogRunnerBuilder.builder()
                .dataSource(dataSource)
                .changelog(properties.getChangeLog())
                .trackingTable(properties.getTrackingTable())
                .lockTable(properties.getLockTable())
                .leaderElection(properties.isLeaderElection())
                .snapshotPolicy(properties.getSnapshotPolicy())
                .rollbackPolicy(properties.getRollbackPolicy())
                .objectQuotingStrategy(properties.getObjectQuotingStrategy());

        if (properties.getContexts() != null) {
            builder.contexts(properties.getContexts());
        }
        if (properties.getLabels() != null) {
            builder.labels(properties.getLabels());
        }

        return builder.build();
    }


    /**
     * Create the {@link ChangelogInitializer} bean that runs migrations on startup.
     *
     * @param changelogRunner the changelog runner
     * @return the changelog initializer
     */
    @Bean
    @ConditionalOnMissingBean
    public ChangelogInitializer changelogInitializer(IChangelogRunner changelogRunner) {
        return new ChangelogInitializer(changelogRunner);
    }
}
