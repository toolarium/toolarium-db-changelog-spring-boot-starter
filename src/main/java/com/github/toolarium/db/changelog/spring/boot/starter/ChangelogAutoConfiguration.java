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
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Spring Boot auto-configuration for toolarium-db-changelog.
 * Configures an {@link IChangelogRunner} bean and runs migrations on startup.
 *
 * @author patrick
 */
@Configuration(proxyBeanMethods = false)
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
        DataSource migrationDataSource = dataSource;
        if (properties.hasSeparateDataSource()) {
            migrationDataSource = createMigrationDataSource(dataSource, properties);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating changelog runner with changelog path: " + properties.getChangeLog());
        }

        ChangelogRunnerBuilder builder = ChangelogRunnerBuilder.builder()
                .dataSource(migrationDataSource)
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
     * @param properties the changelog properties
     * @return the changelog initializer
     */
    @Bean
    @ConditionalOnMissingBean
    public ChangelogInitializer changelogInitializer(IChangelogRunner changelogRunner, ChangelogProperties properties) {
        return new ChangelogInitializer(changelogRunner, properties.isClearChecksums(), properties.getTag());
    }


    /**
     * Ensure JPA EntityManagerFactory beans depend on the changelog initializer.
     *
     * @return the post processor
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.orm.jpa.AbstractEntityManagerFactoryBean")
    public static ChangelogDependsOnPostProcessor changelogJpaDependsOnPostProcessor() {
        return new ChangelogDependsOnPostProcessor("org.springframework.orm.jpa.AbstractEntityManagerFactoryBean");
    }


    /**
     * Ensure JdbcOperations beans depend on the changelog initializer.
     *
     * @return the post processor
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.jdbc.core.JdbcOperations")
    public static ChangelogDependsOnPostProcessor changelogJdbcDependsOnPostProcessor() {
        return new ChangelogDependsOnPostProcessor("org.springframework.jdbc.core.JdbcOperations");
    }


    /**
     * Ensure NamedParameterJdbcOperations beans depend on the changelog initializer.
     *
     * @return the post processor
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations")
    public static ChangelogDependsOnPostProcessor changelogNamedJdbcDependsOnPostProcessor() {
        return new ChangelogDependsOnPostProcessor(
                "org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations");
    }


    /**
     * Create a dedicated DataSource for migrations using the configured credentials.
     * Falls back to the primary DataSource properties for any value not explicitly set.
     *
     * @param primaryDataSource the primary application DataSource
     * @param properties the changelog properties
     * @return the migration DataSource
     * @throws IllegalArgumentException if url is not configured
     */
    private DataSource createMigrationDataSource(DataSource primaryDataSource, ChangelogProperties properties) {
        if (properties.getUrl() == null || properties.getUrl().isEmpty()) {
            throw new IllegalArgumentException(
                    "toolarium.db.changelog.url is required when user or password is configured for a separate migration DataSource.");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating dedicated migration DataSource.");
        }

        DataSourceBuilder<?> builder = DataSourceBuilder.create().url(properties.getUrl());
        if (properties.getUser() != null && !properties.getUser().isEmpty()) {
            builder.username(properties.getUser());
        }
        if (properties.getPassword() != null && !properties.getPassword().isEmpty()) {
            builder.password(properties.getPassword());
        }
        if (properties.getDriverClassName() != null && !properties.getDriverClassName().isEmpty()) {
            builder.driverClassName(properties.getDriverClassName());
        }
        return builder.build();
    }
}
