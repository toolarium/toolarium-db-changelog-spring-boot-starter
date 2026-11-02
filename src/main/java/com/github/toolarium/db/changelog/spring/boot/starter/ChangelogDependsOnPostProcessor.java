/*
 * ChangelogDependsOnPostProcessor.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.spring.boot.starter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


/**
 * A {@link BeanFactoryPostProcessor} that ensures beans of a given type depend on the
 * {@code changelogInitializer} bean. This guarantees that database migrations complete
 * before JPA EntityManagerFactory or JDBC beans access the database.
 *
 * <p>Uses string-based class name resolution to avoid compile-time dependencies
 * on JPA or Spring JDBC modules.</p>
 *
 * @author patrick
 */
class ChangelogDependsOnPostProcessor implements BeanFactoryPostProcessor {
    private static final String INITIALIZER_BEAN = "changelogInitializer";
    private final String dependentBeanTypeName;


    /**
     * Constructor for ChangelogDependsOnPostProcessor.
     *
     * @param dependentBeanTypeName the fully qualified class name of the dependent bean type
     */
    ChangelogDependsOnPostProcessor(String dependentBeanTypeName) {
        this.dependentBeanTypeName = dependentBeanTypeName;
    }


    /**
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(
     *      org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!beanFactory.containsBean(INITIALIZER_BEAN)) {
            return;
        }

        Class<?> dependentType;
        try {
            dependentType = Class.forName(dependentBeanTypeName, false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return;
        }

        String[] beanNames = beanFactory.getBeanNamesForType(dependentType, true, false);
        for (String beanName : beanNames) {
            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
            String[] currentDependsOn = definition.getDependsOn();
            List<String> dependencies;
            if (currentDependsOn != null) {
                dependencies = new ArrayList<>(Arrays.asList(currentDependsOn));
            } else {
                dependencies = new ArrayList<>();
            }

            if (!dependencies.contains(INITIALIZER_BEAN)) {
                dependencies.add(INITIALIZER_BEAN);
                definition.setDependsOn(dependencies.toArray(new String[0]));
            }
        }
    }
}
