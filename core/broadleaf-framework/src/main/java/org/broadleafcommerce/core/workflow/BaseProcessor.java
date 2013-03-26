/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.workflow;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Base class for all Workflow Processors.  Responsible of keeping track of an ordered collection
 * of {@link Activity Activities}
 * 
 * @since March 1, 2005
 * @see Activity
 * 
 */
public abstract class BaseProcessor implements InitializingBean, BeanNameAware, BeanFactoryAware, Processor {

    private BeanFactory beanFactory;
    private String beanName;
    private List<Activity<ProcessContext>> activities;
    private ErrorHandler defaultErrorHandler;

    @Value("${workflow.auto.rollback.on.error}")
    private boolean autoRollbackOnError = true;

    /* Sets name of the spring bean in the application context that this
     * processor is configured under
     * (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;

    }

    /* Sets the spring bean factroy bean that is responsible for this processor.
     * (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Whether or not the ActivityStateManager should automatically attempt to rollback any RollbackHandlers registered.
     * If false, rolling back will require an explicit call to ActivityStateManagerImpl.getStateManager().rollbackAllState().
     * The default value is true.
     *
     * @return Whether or not the ActivityStateManager should automatically attempt to rollback
     */
    public boolean getAutoRollbackOnError() {
        return autoRollbackOnError;
    }

    /**
     * Set whether or not the ActivityStateManager should automatically attempt to rollback any RollbackHandlers registered.
     * If false, rolling back will require an explicit call to ActivityStateManagerImpl.getStateManager().rollbackAllState().
     * The default value is true.
     *
     * @param autoRollbackOnError Whether or not the ActivityStateManager should automatically attempt to rollback
     */
    public void setAutoRollbackOnError(boolean autoRollbackOnError) {
        this.autoRollbackOnError = autoRollbackOnError;
    }

    /*
         * Called after the properties have been set, Ensures the list of activities
         *  is not empty and each activity is supported by this Workflow Processor
         * (non-Javadoc)
         *
         * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
         */
    @Override
    public void afterPropertiesSet() throws Exception {

        if(!(beanFactory instanceof ListableBeanFactory)) {
            throw new BeanInitializationException("The workflow processor ["+beanName+"] " +
                    "is not managed by a ListableBeanFactory, please re-deploy using some derivative of ListableBeanFactory such as" +
            "ClassPathXmlApplicationContext ");
        }

        if (activities == null || activities.isEmpty()) {
            throw new UnsatisfiedDependencyException(getBeanDesc(), beanName, "activities",
            "No activities were wired for this workflow");
        }

        for (Iterator<Activity<ProcessContext>> iter = activities.iterator(); iter.hasNext();) {
            Activity<? extends ProcessContext> activity = iter.next();
            if( !supports(activity))
                throw new BeanInitializationException("The workflow processor ["+beanName+"] does " +
                        "not support the activity of type"+activity.getClass().getName());
        }

    }

    /**
     * Returns the bean description if the current bean factory allows it.
     * @return spring bean description configure via the spring description tag
     */
    protected String getBeanDesc() {
        return (beanFactory instanceof ConfigurableListableBeanFactory) ?
                ((ConfigurableListableBeanFactory) beanFactory).getBeanDefinition(beanName).getResourceDescription()
                : "Workflow Processor: " + beanName;
    }

    /**
     * Sets the collection of Activities to be executed by the Workflow Process
     * 
     * @param activities ordered collection (List) of activities to be executed by the processor
     */
    @Override
    public void setActivities(List<Activity<ProcessContext>> activities) {
        this.activities = activities;
    }

    @Override
    public void setDefaultErrorHandler(ErrorHandler defaultErrorHandler) {
        this.defaultErrorHandler = defaultErrorHandler;
    }

    public List<Activity<ProcessContext>> getActivities() {
        return activities;
    }

    public String getBeanName() {
        return beanName;
    }

    public ErrorHandler getDefaultErrorHandler() {
        return defaultErrorHandler;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
