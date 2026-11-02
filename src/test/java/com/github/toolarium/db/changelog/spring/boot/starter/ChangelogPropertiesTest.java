/*
 * ChangelogPropertiesTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.db.changelog.config.ChangelogObjectQuotingStrategy;
import com.github.toolarium.db.changelog.config.ChangelogRollbackPolicy;
import com.github.toolarium.db.changelog.config.ChangelogSnapshotPolicy;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link ChangelogProperties}.
 *
 * @author patrick
 */
public class ChangelogPropertiesTest {

    /**
     * Test default values.
     */
    @Test
    void testDefaults() {
        ChangelogProperties props = new ChangelogProperties();

        assertTrue(props.isEnabled());
        assertEquals("classpath:db/changelog/changelog-master.db.yaml", props.getChangeLog());
        assertEquals("DB_CHANGELOG", props.getTrackingTable());
        assertEquals("DB_CHANGELOG_LOCK", props.getLockTable());
        assertFalse(props.isLeaderElection());
        assertNull(props.getContexts());
        assertNull(props.getLabels());
        assertNull(props.getDefaultAuthor());
        assertEquals(ChangelogSnapshotPolicy.NONE, props.getSnapshotPolicy());
        assertEquals(ChangelogRollbackPolicy.WARN, props.getRollbackPolicy());
        assertEquals(ChangelogObjectQuotingStrategy.NONE, props.getObjectQuotingStrategy());
    }


    /**
     * Test setters and getters.
     */
    @Test
    void testSettersAndGetters() {
        ChangelogProperties props = new ChangelogProperties();

        props.setEnabled(false);
        assertFalse(props.isEnabled());

        props.setChangeLog("classpath:custom.db.yaml");
        assertEquals("classpath:custom.db.yaml", props.getChangeLog());

        props.setTrackingTable("MY_CHANGELOG");
        assertEquals("MY_CHANGELOG", props.getTrackingTable());

        props.setLockTable("MY_LOCK");
        assertEquals("MY_LOCK", props.getLockTable());

        props.setLeaderElection(true);
        assertTrue(props.isLeaderElection());

        List<String> contexts = Arrays.asList("prod", "test");
        props.setContexts(contexts);
        assertEquals(contexts, props.getContexts());

        List<String> labels = Arrays.asList("1.0", "core");
        props.setLabels(labels);
        assertEquals(labels, props.getLabels());

        props.setDefaultAuthor("john");
        assertEquals("john", props.getDefaultAuthor());

        props.setSnapshotPolicy(ChangelogSnapshotPolicy.ALL);
        assertEquals(ChangelogSnapshotPolicy.ALL, props.getSnapshotPolicy());

        props.setRollbackPolicy(ChangelogRollbackPolicy.FAIL);
        assertEquals(ChangelogRollbackPolicy.FAIL, props.getRollbackPolicy());

        props.setObjectQuotingStrategy(ChangelogObjectQuotingStrategy.QUOTE_ALL);
        assertEquals(ChangelogObjectQuotingStrategy.QUOTE_ALL, props.getObjectQuotingStrategy());
    }
}
