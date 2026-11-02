/*
 * ChangelogProperties.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import com.github.toolarium.db.changelog.config.ChangelogObjectQuotingStrategy;
import com.github.toolarium.db.changelog.config.ChangelogRollbackPolicy;
import com.github.toolarium.db.changelog.config.ChangelogSnapshotPolicy;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Spring Boot configuration properties for toolarium-db-changelog.
 *
 * @author patrick
 */
@ConfigurationProperties(prefix = "toolarium.db.changelog")
public class ChangelogProperties {

    /** Whether to enable the changelog auto-configuration. */
    private boolean enabled = true;

    /** Path to the changelog file (classpath or file system). */
    private String changeLog = "classpath:db/changelog/changelog-master.db.yaml";

    /** Name of the tracking table. */
    private String trackingTable = "DB_CHANGELOG";

    /** Name of the lock table. */
    private String lockTable = "DB_CHANGELOG_LOCK";

    /** Whether to enable leader election for distributed locking. */
    private boolean leaderElection = false;

    /** Active contexts for filtering changesets. */
    private List<String> contexts;

    /** Active labels for filtering changesets. */
    private List<String> labels;

    /** Default author for changesets. */
    private String defaultAuthor;

    /** Snapshot policy for rollback support. */
    private ChangelogSnapshotPolicy snapshotPolicy = ChangelogSnapshotPolicy.NONE;

    /** Rollback policy for missing rollback definitions. */
    private ChangelogRollbackPolicy rollbackPolicy = ChangelogRollbackPolicy.WARN;

    /** Object quoting strategy for SQL generation. */
    private ChangelogObjectQuotingStrategy objectQuotingStrategy = ChangelogObjectQuotingStrategy.NONE;


    /**
     * Check whether the changelog auto-configuration is enabled.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }


    /**
     * Set whether the changelog auto-configuration is enabled.
     *
     * @param enabled true to enable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    /**
     * Get the changelog path.
     *
     * @return the changelog path
     */
    public String getChangeLog() {
        return changeLog;
    }


    /**
     * Set the changelog path.
     *
     * @param changeLog the changelog path
     */
    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }


    /**
     * Get the tracking table name.
     *
     * @return the tracking table name
     */
    public String getTrackingTable() {
        return trackingTable;
    }


    /**
     * Set the tracking table name.
     *
     * @param trackingTable the tracking table name
     */
    public void setTrackingTable(String trackingTable) {
        this.trackingTable = trackingTable;
    }


    /**
     * Get the lock table name.
     *
     * @return the lock table name
     */
    public String getLockTable() {
        return lockTable;
    }


    /**
     * Set the lock table name.
     *
     * @param lockTable the lock table name
     */
    public void setLockTable(String lockTable) {
        this.lockTable = lockTable;
    }


    /**
     * Check if leader election is enabled.
     *
     * @return true if leader election is enabled
     */
    public boolean isLeaderElection() {
        return leaderElection;
    }


    /**
     * Set whether leader election is enabled.
     *
     * @param leaderElection true to enable leader election
     */
    public void setLeaderElection(boolean leaderElection) {
        this.leaderElection = leaderElection;
    }


    /**
     * Get the active contexts.
     *
     * @return the contexts
     */
    public List<String> getContexts() {
        return contexts;
    }


    /**
     * Set the active contexts.
     *
     * @param contexts the contexts
     */
    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }


    /**
     * Get the active labels.
     *
     * @return the labels
     */
    public List<String> getLabels() {
        return labels;
    }


    /**
     * Set the active labels.
     *
     * @param labels the labels
     */
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }


    /**
     * Get the default author.
     *
     * @return the default author
     */
    public String getDefaultAuthor() {
        return defaultAuthor;
    }


    /**
     * Set the default author.
     *
     * @param defaultAuthor the default author
     */
    public void setDefaultAuthor(String defaultAuthor) {
        this.defaultAuthor = defaultAuthor;
    }


    /**
     * Get the snapshot policy.
     *
     * @return the snapshot policy
     */
    public ChangelogSnapshotPolicy getSnapshotPolicy() {
        return snapshotPolicy;
    }


    /**
     * Set the snapshot policy.
     *
     * @param snapshotPolicy the snapshot policy
     */
    public void setSnapshotPolicy(ChangelogSnapshotPolicy snapshotPolicy) {
        this.snapshotPolicy = snapshotPolicy;
    }


    /**
     * Get the rollback policy.
     *
     * @return the rollback policy
     */
    public ChangelogRollbackPolicy getRollbackPolicy() {
        return rollbackPolicy;
    }


    /**
     * Set the rollback policy.
     *
     * @param rollbackPolicy the rollback policy
     */
    public void setRollbackPolicy(ChangelogRollbackPolicy rollbackPolicy) {
        this.rollbackPolicy = rollbackPolicy;
    }


    /**
     * Get the object quoting strategy.
     *
     * @return the object quoting strategy
     */
    public ChangelogObjectQuotingStrategy getObjectQuotingStrategy() {
        return objectQuotingStrategy;
    }


    /**
     * Set the object quoting strategy.
     *
     * @param objectQuotingStrategy the object quoting strategy
     */
    public void setObjectQuotingStrategy(ChangelogObjectQuotingStrategy objectQuotingStrategy) {
        this.objectQuotingStrategy = objectQuotingStrategy;
    }
}
