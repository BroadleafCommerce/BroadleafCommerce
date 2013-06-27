/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.logging;

import javax.annotation.PostConstruct;

/**
 * A simple bean that when declared in app context will cause a lifecycle
 * logging message to appear in the logging output.
 *
 * @author Jeff Fischer
 */
public class ModuleLifecycleLoggingBean {

    private String moduleName;
    private LifeCycleEvent lifeCycleEvent;

    @PostConstruct
    /**
     * Initialize the bean and cause the logging message to take place
     */
    public void init() {
        if (moduleName == null || lifeCycleEvent == null) {
            throw new IllegalArgumentException("Must supply the moduleName and lifeCycleEvent properties!");
        }
        SupportLogger logger = SupportLogManager.getLogger(moduleName, ModuleLifecycleLoggingBean.class);
        logger.lifecycle(lifeCycleEvent, "");
    }

    /**
     * Retrieve the type of life cycle event for this logging message
     *
     * @return life cycle event type
     */
    public LifeCycleEvent getLifeCycleEvent() {
        return lifeCycleEvent;
    }

    /**
     * Set the type of life cycle event for this logging message
     *
     * @param lifeCycleEvent life cycle event type
     */
    public void setLifeCycleEvent(LifeCycleEvent lifeCycleEvent) {
        this.lifeCycleEvent = lifeCycleEvent;
    }

    /**
     * The name of the module that this log message applies to
     *
     * @return the module name for this logging message
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Set the name of the module that this log message applies to
     *
     * @param moduleName the module name for this logging message
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
