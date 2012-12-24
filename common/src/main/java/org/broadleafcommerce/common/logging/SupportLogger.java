package org.broadleafcommerce.common.logging;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Extend Log4J Logger implementation to provide support for the
 * new SUPPORT log level type.
 *
 * @author Jeff Fischer
 */
public class SupportLogger extends Logger {

    private static final String FQCN = SupportLevel.class.getName();

    protected String moduleName;

    public SupportLogger(String moduleName, String name) {
        super(name);
        this.moduleName = moduleName;
    }

    /**
     * Generate a SUPPORT level log message
     *
     * @param message the log message
     */
    public void support(Object message) {
        if (repository.isDisabled(SupportLevel.SUPPORT_INT))
            return;
        if (SupportLevel.SUPPORT.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, SupportLevel.SUPPORT, moduleName + " - " + message, null);
        }
    }

    /**
     * Generate a SUPPORT level log message with an accompanying Throwable
     *
     * @param message the log message
     * @param t the exception to accompany the log message - will result in a stack track in the log
     */
    public void support(Object message, Throwable t) {
        if (repository.isDisabled(SupportLevel.SUPPORT_INT))
            return;
        if (SupportLevel.SUPPORT.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, SupportLevel.SUPPORT, moduleName + " - " + message, t);
        }
    }

    /**
     * Generate a specialized SUPPORT level log message that includes a LifeCycleEvent
     * in the message.
     *
     * @param lifeCycleEvent The module life cycle type for this log message
     * @param message the log message
     */
    public void lifecycle(LifeCycleEvent lifeCycleEvent, Object message) {
        if (repository.isDisabled(SupportLevel.SUPPORT_INT))
            return;
        if (SupportLevel.SUPPORT.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, SupportLevel.SUPPORT, moduleName + " - " + lifeCycleEvent.toString() + (!StringUtils.isEmpty(message.toString())?" - " + message:""), null);
        }
    }
}
