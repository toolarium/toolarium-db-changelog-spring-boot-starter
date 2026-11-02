/*
 * ChangelogHealthIndicatorTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter.actuator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.exception.ChangelogException;
import com.github.toolarium.db.changelog.model.IChangelogChangeSetStatus;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;


/**
 * Tests for {@link ChangelogHealthIndicator}.
 *
 * @author patrick
 */
public class ChangelogHealthIndicatorTest {

    /**
     * Test UP status when no pending changesets.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testUpWhenNoPendingChangesets() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doReturn(Collections.emptyList()).when(runner).status();

        ChangelogHealthIndicator indicator = new ChangelogHealthIndicator(runner);
        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("All changesets applied", health.getDetails().get("status"));
    }


    /**
     * Test UP status when status returns null.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testUpWhenStatusReturnsNull() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doReturn(null).when(runner).status();

        ChangelogHealthIndicator indicator = new ChangelogHealthIndicator(runner);
        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
    }


    /**
     * Test OUT_OF_SERVICE when pending changesets found.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testOutOfServiceWhenPendingChangesets() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        IChangelogChangeSetStatus pending1 = mock(IChangelogChangeSetStatus.class);
        IChangelogChangeSetStatus pending2 = mock(IChangelogChangeSetStatus.class);
        doReturn(Arrays.asList(pending1, pending2)).when(runner).status();

        ChangelogHealthIndicator indicator = new ChangelogHealthIndicator(runner);
        Health health = indicator.health();

        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
        assertEquals("Pending changesets found", health.getDetails().get("status"));
        assertEquals(2, health.getDetails().get("pendingCount"));
    }


    /**
     * Test DOWN status when status throws exception.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testDownWhenExceptionThrown() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doThrow(new ChangelogException("connection lost")).when(runner).status();

        ChangelogHealthIndicator indicator = new ChangelogHealthIndicator(runner);
        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
