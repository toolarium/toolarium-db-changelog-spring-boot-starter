/*
 * ChangelogInfoContributorTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter.actuator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.exception.ChangelogException;
import com.github.toolarium.db.changelog.model.IChangelogChangeSetStatus;
import com.github.toolarium.db.changelog.model.IChangelogTrackingEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;


/**
 * Tests for {@link ChangelogInfoContributor}.
 *
 * @author patrick
 */
public class ChangelogInfoContributorTest {

    /**
     * Test happy path with both history and status data.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testContributeWithData() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doReturn(Arrays.asList(
                mock(IChangelogTrackingEntry.class),
                mock(IChangelogTrackingEntry.class),
                mock(IChangelogTrackingEntry.class))).when(runner).history();
        doReturn(Collections.singletonList(
                mock(IChangelogChangeSetStatus.class))).when(runner).status();

        ChangelogInfoContributor contributor = new ChangelogInfoContributor(runner);
        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) info.getDetails().get("dbChangelog");
        assertNotNull(details);
        assertEquals(3, details.get("appliedChangesets"));
        assertEquals(1, details.get("pendingChangesets"));
    }


    /**
     * Test that history failure does not prevent status from being collected.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testHistoryFailureDoesNotBlockStatus() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doThrow(new ChangelogException("timeout")).when(runner).history();
        doReturn(Collections.emptyList()).when(runner).status();

        ChangelogInfoContributor contributor = new ChangelogInfoContributor(runner);
        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) info.getDetails().get("dbChangelog");
        assertNotNull(details);
        assertNull(details.get("appliedChangesets"));
        assertEquals(0, details.get("pendingChangesets"));
    }


    /**
     * Test that status failure does not prevent history from being collected.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testStatusFailureDoesNotBlockHistory() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doReturn(Arrays.asList(
                mock(IChangelogTrackingEntry.class))).when(runner).history();
        doThrow(new ChangelogException("timeout")).when(runner).status();

        ChangelogInfoContributor contributor = new ChangelogInfoContributor(runner);
        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) info.getDetails().get("dbChangelog");
        assertNotNull(details);
        assertEquals(1, details.get("appliedChangesets"));
        assertNull(details.get("pendingChangesets"));
    }


    /**
     * Test that both failures result in no details added.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testBothFailuresNoDetails() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doThrow(new ChangelogException("error1")).when(runner).history();
        doThrow(new ChangelogException("error2")).when(runner).status();

        ChangelogInfoContributor contributor = new ChangelogInfoContributor(runner);
        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        assertFalse(info.getDetails().containsKey("dbChangelog"));
    }


    /**
     * Test that null history and null status are handled gracefully.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testNullResults() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doReturn(null).when(runner).history();
        doReturn(null).when(runner).status();

        ChangelogInfoContributor contributor = new ChangelogInfoContributor(runner);
        Info.Builder builder = new Info.Builder();
        contributor.contribute(builder);
        Info info = builder.build();

        assertFalse(info.getDetails().containsKey("dbChangelog"));
    }
}
