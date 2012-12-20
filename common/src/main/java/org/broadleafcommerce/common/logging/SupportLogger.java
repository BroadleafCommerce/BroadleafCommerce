package org.broadleafcommerce.common.logging;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Jeff Fischer
 */
public class SupportLogger extends Logger {

    private static final String FQCN = SupportLevel.class.getName();

    protected String moduleName;

    public SupportLogger(String moduleName, String name) {
        super(name);
        this.moduleName = moduleName;
    }

    public void support(Object message) {
        if (repository.isDisabled(SupportLevel.SUPPORT_INT))
            return;
        if (SupportLevel.SUPPORT.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, SupportLevel.SUPPORT, moduleName + " - " + message, null);
        }
    }

    public void support(Object message, Throwable t) {
        if (repository.isDisabled(SupportLevel.SUPPORT_INT))
            return;
        if (SupportLevel.SUPPORT.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, SupportLevel.SUPPORT, moduleName + " - " + message, t);
        }
    }

    public void lifecycle(LifeCycleEvent lifeCycleEvent, Object message) {
        if (repository.isDisabled(SupportLevel.SUPPORT_INT))
            return;
        if (SupportLevel.SUPPORT.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, SupportLevel.SUPPORT, moduleName + " - " + lifeCycleEvent.toString() + (!StringUtils.isEmpty(message.toString())?" - " + message:""), null);
        }
    }
}
