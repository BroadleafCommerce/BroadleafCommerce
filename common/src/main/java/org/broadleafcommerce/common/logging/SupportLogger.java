/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
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
