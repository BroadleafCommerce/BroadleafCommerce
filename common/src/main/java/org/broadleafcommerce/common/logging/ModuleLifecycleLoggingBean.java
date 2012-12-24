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
