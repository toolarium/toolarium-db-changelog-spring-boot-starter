/*
 * ChangelogInitializer.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import com.github.toolarium.db.changelog.IChangelogRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * Runs database changelog migrations during bean initialization.
 * Implements {@link InitializingBean} so that other beans can depend on this
 * bean via DependsOn, ensuring migrations complete before JPA or JDBC beans
 * access the database.
 *
 * @author patrick
 */
public class ChangelogInitializer implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(ChangelogInitializer.class);
    private final IChangelogRunner changelogRunner;
    private final boolean clearChecksums;
    private final String tag;


    /**
     * Constructor for ChangelogInitializer.
     *
     * @param changelogRunner the changelog runner
     * @param clearChecksums whether to clear checksums before running migrations
     * @param tag the tag to apply after migrations, or null for no tagging
     */
    public ChangelogInitializer(IChangelogRunner changelogRunner, boolean clearChecksums, String tag) {
        this.changelogRunner = changelogRunner;
        this.clearChecksums = clearChecksums;
        this.tag = tag;
    }


    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if (clearChecksums) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Clearing stored checksums...");
                }
                changelogRunner.clearChecksums();
            }

            LOG.info("Running database changelog migrations...");
            changelogRunner.update();
            LOG.info("Database changelog migrations completed successfully.");

            if (tag != null && !tag.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Tagging database with: " + tag);
                }
                changelogRunner.tag(tag);
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Database changelog migration failed: " + e.getMessage(), e);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Database changelog migration failed", e);
        }
    }
}
