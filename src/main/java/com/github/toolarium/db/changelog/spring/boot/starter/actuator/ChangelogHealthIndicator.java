/*
 * ChangelogHealthIndicator.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter.actuator;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.model.IChangelogChangeSetStatus;
import java.util.List;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;


/**
 * Actuator health indicator for toolarium-db-changelog.
 * Reports UP when no pending changesets exist, OUT_OF_SERVICE when pending changesets are found.
 *
 * @author patrick
 */
public class ChangelogHealthIndicator extends AbstractHealthIndicator {
    private final IChangelogRunner changelogRunner;


    /**
     * Constructor for ChangelogHealthIndicator.
     *
     * @param changelogRunner the changelog runner
     */
    public ChangelogHealthIndicator(IChangelogRunner changelogRunner) {
        super("Changelog health check failed");
        this.changelogRunner = changelogRunner;
    }


    /**
     * @see org.springframework.boot.actuate.health.AbstractHealthIndicator#doHealthCheck(
     *      org.springframework.boot.actuate.health.Health.Builder)
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        List<IChangelogChangeSetStatus> pending = changelogRunner.status();
        if (pending == null || pending.isEmpty()) {
            builder.up().withDetail("status", "All changesets applied");
        } else {
            builder.outOfService()
                    .withDetail("status", "Pending changesets found")
                    .withDetail("pendingCount", pending.size());
        }
    }
}
