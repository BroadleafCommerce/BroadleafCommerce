package org.broadleafcommerce.common.logging;

import javax.annotation.PostConstruct;

/**
 * @author Jeff Fischer
 */
public class ModuleLifecycleLoggingBean {

    private String moduleName;
    private LifeCycleEvent lifeCycleEvent;

    @PostConstruct
    public void init() {
        if (moduleName == null || lifeCycleEvent == null) {
            throw new IllegalArgumentException("Must supply the moduleName and lifeCycleEvent properties!");
        }
        SupportLogger logger = SupportLogManager.getLogger(moduleName, ModuleLifecycleLoggingBean.class);
        logger.lifecycle(lifeCycleEvent, "");
    }

    public LifeCycleEvent getLifeCycleEvent() {
        return lifeCycleEvent;
    }

    public void setLifeCycleEvent(LifeCycleEvent lifeCycleEvent) {
        this.lifeCycleEvent = lifeCycleEvent;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
