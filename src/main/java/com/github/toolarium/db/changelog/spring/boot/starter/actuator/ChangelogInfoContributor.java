/*
 * ChangelogInfoContributor.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter.actuator;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.model.IChangelogChangeSetStatus;
import com.github.toolarium.db.changelog.model.IChangelogTrackingEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;


/**
 * Actuator info contributor for toolarium-db-changelog.
 * Exposes applied changeset count and pending changeset count.
 *
 * @author patrick
 */
public class ChangelogInfoContributor implements InfoContributor {
    private static final Logger LOG = LoggerFactory.getLogger(ChangelogInfoContributor.class);
    private final IChangelogRunner changelogRunner;


    /**
     * Constructor for ChangelogInfoContributor.
     *
     * @param changelogRunner the changelog runner
     */
    public ChangelogInfoContributor(IChangelogRunner changelogRunner) {
        this.changelogRunner = changelogRunner;
    }


    /**
     * @see org.springframework.boot.actuate.info.InfoContributor#contribute(org.springframework.boot.actuate.info.Info.Builder)
     */
    @Override
    public void contribute(Info.Builder builder) {
        try {
            Map<String, Object> details = new LinkedHashMap<>();

            List<IChangelogTrackingEntry> history = changelogRunner.history();
            if (history != null) {
                details.put("appliedChangesets", history.size());
            }

            List<IChangelogChangeSetStatus> pending = changelogRunner.status();
            if (pending != null) {
                details.put("pendingChangesets", pending.size());
            }

            builder.withDetail("dbChangelog", details);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to collect changelog info: " + e.getMessage(), e);
            }
        }
    }
}
