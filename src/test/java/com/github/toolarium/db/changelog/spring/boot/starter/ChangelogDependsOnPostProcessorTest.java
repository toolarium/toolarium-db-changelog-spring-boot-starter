/*
 * ChangelogDependsOnPostProcessorTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


/**
 * Tests for {@link ChangelogDependsOnPostProcessor}.
 *
 * @author patrick
 */
public class ChangelogDependsOnPostProcessorTest {
    private static final String INITIALIZER_BEAN = "changelogInitializer";
    private static final String DATA_SOURCE_BEAN = "dataSource";
    private static final String DATA_SOURCE_TYPE = "javax.sql.DataSource";


    /**
     * Test that dependsOn is added to matching beans.
     */
    @Test
    void testAddsDependsOn() {
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(beanFactory.containsBean(INITIALIZER_BEAN)).thenReturn(true);
        when(beanFactory.getBeanNamesForType(DataSource.class, true, false)).thenReturn(new String[]{DATA_SOURCE_BEAN});
        when(beanFactory.getBeanDefinition(DATA_SOURCE_BEAN)).thenReturn(beanDef);
        when(beanDef.getDependsOn()).thenReturn(null);

        ChangelogDependsOnPostProcessor processor = new ChangelogDependsOnPostProcessor(DATA_SOURCE_TYPE);
        processor.postProcessBeanFactory(beanFactory);

        verify(beanDef).setDependsOn(new String[]{INITIALIZER_BEAN});
    }


    /**
     * Test that existing dependsOn entries are preserved.
     */
    @Test
    void testPreservesExistingDependsOn() {
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(beanFactory.containsBean(INITIALIZER_BEAN)).thenReturn(true);
        when(beanFactory.getBeanNamesForType(DataSource.class, true, false)).thenReturn(new String[]{DATA_SOURCE_BEAN});
        when(beanFactory.getBeanDefinition(DATA_SOURCE_BEAN)).thenReturn(beanDef);
        when(beanDef.getDependsOn()).thenReturn(new String[]{"existingBean"});

        ChangelogDependsOnPostProcessor processor = new ChangelogDependsOnPostProcessor(DATA_SOURCE_TYPE);
        processor.postProcessBeanFactory(beanFactory);

        verify(beanDef).setDependsOn(new String[]{"existingBean", INITIALIZER_BEAN});
    }


    /**
     * Test that duplicate dependsOn entries are not added.
     */
    @Test
    void testNoDuplicateDependsOn() {
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        BeanDefinition beanDef = mock(BeanDefinition.class);

        when(beanFactory.containsBean(INITIALIZER_BEAN)).thenReturn(true);
        when(beanFactory.getBeanNamesForType(DataSource.class, true, false)).thenReturn(new String[]{DATA_SOURCE_BEAN});
        when(beanFactory.getBeanDefinition(DATA_SOURCE_BEAN)).thenReturn(beanDef);
        when(beanDef.getDependsOn()).thenReturn(new String[]{INITIALIZER_BEAN});

        ChangelogDependsOnPostProcessor processor = new ChangelogDependsOnPostProcessor(DATA_SOURCE_TYPE);
        processor.postProcessBeanFactory(beanFactory);

        verify(beanDef, never()).setDependsOn(org.mockito.ArgumentMatchers.any(String[].class));
    }


    /**
     * Test no-op when changelogInitializer is not in context.
     */
    @Test
    void testNoOpWithoutInitializerBean() {
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(beanFactory.containsBean(INITIALIZER_BEAN)).thenReturn(false);

        ChangelogDependsOnPostProcessor processor = new ChangelogDependsOnPostProcessor(DATA_SOURCE_TYPE);
        processor.postProcessBeanFactory(beanFactory);

        verify(beanFactory, never()).getBeanNamesForType(
                org.mockito.ArgumentMatchers.any(Class.class),
                org.mockito.ArgumentMatchers.anyBoolean(),
                org.mockito.ArgumentMatchers.anyBoolean());
    }


    /**
     * Test no-op when dependent type is not on classpath.
     */
    @Test
    void testNoOpWhenClassNotFound() {
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(beanFactory.containsBean(INITIALIZER_BEAN)).thenReturn(true);

        ChangelogDependsOnPostProcessor processor =
                new ChangelogDependsOnPostProcessor("com.nonexistent.SomeClass");
        processor.postProcessBeanFactory(beanFactory);

        verify(beanFactory, never()).getBeanNamesForType(
                org.mockito.ArgumentMatchers.any(Class.class),
                org.mockito.ArgumentMatchers.anyBoolean(),
                org.mockito.ArgumentMatchers.anyBoolean());
    }
}
