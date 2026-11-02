/*
 * ChangelogInitializerTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.exception.ChangelogException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;


/**
 * Tests for {@link ChangelogInitializer}.
 *
 * @author patrick
 */
public class ChangelogInitializerTest {

    /**
     * Test that afterPropertiesSet calls update.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testUpdateIsCalled() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        ChangelogInitializer initializer = new ChangelogInitializer(runner, false, null);

        initializer.afterPropertiesSet();

        verify(runner).update();
        verify(runner, never()).clearChecksums();
        verify(runner, never()).tag(org.mockito.ArgumentMatchers.anyString());
    }


    /**
     * Test that clearChecksums is called before update when enabled.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testClearChecksumsBeforeUpdate() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        ChangelogInitializer initializer = new ChangelogInitializer(runner, true, null);

        initializer.afterPropertiesSet();

        InOrder order = inOrder(runner);
        order.verify(runner).clearChecksums();
        order.verify(runner).update();
    }


    /**
     * Test that tag is called after update when set.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testTagAfterUpdate() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        ChangelogInitializer initializer = new ChangelogInitializer(runner, false, "v1.0");

        initializer.afterPropertiesSet();

        InOrder order = inOrder(runner);
        order.verify(runner).update();
        order.verify(runner).tag("v1.0");
    }


    /**
     * Test that empty tag is not applied.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testEmptyTagIsSkipped() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        ChangelogInitializer initializer = new ChangelogInitializer(runner, false, "");

        initializer.afterPropertiesSet();

        verify(runner).update();
        verify(runner, never()).tag(org.mockito.ArgumentMatchers.anyString());
    }


    /**
     * Test that all three operations execute in correct order.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testFullSequence() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        ChangelogInitializer initializer = new ChangelogInitializer(runner, true, "release-2.0");

        initializer.afterPropertiesSet();

        InOrder order = inOrder(runner);
        order.verify(runner).clearChecksums();
        order.verify(runner).update();
        order.verify(runner).tag("release-2.0");
    }


    /**
     * Test that checked ChangelogException is wrapped in RuntimeException.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testCheckedExceptionIsWrapped() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        doThrow(new ChangelogException("parse error")).when(runner).update();
        ChangelogInitializer initializer = new ChangelogInitializer(runner, false, null);

        RuntimeException thrown = assertThrows(RuntimeException.class, initializer::afterPropertiesSet);
        assertEquals("Database changelog migration failed", thrown.getMessage());
        assertTrue(thrown.getCause() instanceof ChangelogException);
    }


    /**
     * Test that RuntimeException is rethrown directly without wrapping.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testRuntimeExceptionIsRethrown() throws Exception {
        IChangelogRunner runner = mock(IChangelogRunner.class);
        IllegalStateException original = new IllegalStateException("connection refused");
        doThrow(original).when(runner).update();
        ChangelogInitializer initializer = new ChangelogInitializer(runner, false, null);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, initializer::afterPropertiesSet);
        assertEquals(original, thrown);
    }
}
