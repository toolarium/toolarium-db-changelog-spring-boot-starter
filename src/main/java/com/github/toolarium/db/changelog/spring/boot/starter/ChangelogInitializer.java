/*
 * ChangelogInitializer.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.exception.ChangelogException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;


/**
 * Runs database changelog migrations after all singleton beans have been initialized.
 *
 * @author patrick
 */
public class ChangelogInitializer implements SmartInitializingSingleton {
    private static final Logger LOG = LoggerFactory.getLogger(ChangelogInitializer.class);
    private final IChangelogRunner changelogRunner;


    /**
     * Constructor for ChangelogInitializer.
     *
     * @param changelogRunner the changelog runner
     */
    public ChangelogInitializer(IChangelogRunner changelogRunner) {
        this.changelogRunner = changelogRunner;
    }


    /**
     * @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
     */
    @Override
    public void afterSingletonsInstantiated() {
        try {
            LOG.info("Running database changelog migrations...");
            changelogRunner.update();
            LOG.info("Database changelog migrations completed successfully.");
        } catch (ChangelogException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Database changelog migration failed: " + e.getMessage(), e);
            }
            throw new RuntimeException("Database changelog migration failed: " + e.getMessage(), e);
        }
    }
}
