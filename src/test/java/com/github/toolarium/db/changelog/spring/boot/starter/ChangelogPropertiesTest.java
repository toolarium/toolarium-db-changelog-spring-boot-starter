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
        assertEquals(ChangelogSnapshotPolicy.NONE, props.getSnapshotPolicy());
        assertEquals(ChangelogRollbackPolicy.WARN, props.getRollbackPolicy());
        assertEquals(ChangelogObjectQuotingStrategy.NONE, props.getObjectQuotingStrategy());
        assertNull(props.getUrl());
        assertNull(props.getUser());
        assertNull(props.getPassword());
        assertNull(props.getDriverClassName());
        assertNull(props.getTag());
        assertFalse(props.isClearChecksums());
        assertFalse(props.hasSeparateDataSource());
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

        props.setSnapshotPolicy(ChangelogSnapshotPolicy.ALL);
        assertEquals(ChangelogSnapshotPolicy.ALL, props.getSnapshotPolicy());

        props.setRollbackPolicy(ChangelogRollbackPolicy.FAIL);
        assertEquals(ChangelogRollbackPolicy.FAIL, props.getRollbackPolicy());

        props.setObjectQuotingStrategy(ChangelogObjectQuotingStrategy.QUOTE_ALL);
        assertEquals(ChangelogObjectQuotingStrategy.QUOTE_ALL, props.getObjectQuotingStrategy());

        props.setTag("v1.0");
        assertEquals("v1.0", props.getTag());

        props.setClearChecksums(true);
        assertTrue(props.isClearChecksums());

        props.setUrl("jdbc:h2:mem:migration");
        assertEquals("jdbc:h2:mem:migration", props.getUrl());

        props.setUser("admin");
        assertEquals("admin", props.getUser());

        props.setPassword("secret");
        assertEquals("secret", props.getPassword());

        props.setDriverClassName("org.h2.Driver");
        assertEquals("org.h2.Driver", props.getDriverClassName());
    }


    /**
     * Test separate DataSource detection.
     */
    @Test
    void testHasSeparateDataSource() {
        ChangelogProperties props = new ChangelogProperties();
        assertFalse(props.hasSeparateDataSource());

        props.setUrl("jdbc:h2:mem:migration");
        assertTrue(props.hasSeparateDataSource());

        props = new ChangelogProperties();
        props.setUser("admin");
        assertTrue(props.hasSeparateDataSource());

        props = new ChangelogProperties();
        props.setPassword("secret");
        assertTrue(props.hasSeparateDataSource());
    }


    /**
     * Test that empty strings do not trigger separate DataSource.
     */
    @Test
    void testHasSeparateDataSourceEmptyStrings() {
        ChangelogProperties props = new ChangelogProperties();
        props.setUrl("");
        props.setUser("");
        props.setPassword("");
        assertFalse(props.hasSeparateDataSource());
    }
}
