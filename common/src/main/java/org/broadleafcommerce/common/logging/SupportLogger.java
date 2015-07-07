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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>SupportLogger class that provides support for the new SUPPORT log level type.
 * The SUPPORT log level is independent of any configured logging framework and should be able to be configured independently.</p>
 *
 * <p>This Logger was originally built as an extension to Log4j's {@link Logger}. As a result,
 * other levels must be supported to maintain backwards compatibility.</p>
 *
 * <p>It is important to note that the SupportLogger can be called outside a Spring Context.
 * Therefore, it is possible to instantiate a different SupportLogger adapter using
 * the fully qualified class name of an implementation using a System Property.
 * By default, it will instantiate a {@link SystemSupportLoggerAdapter} if none is specified.
 * For example, you may wish to disable all logs made to the Support Logger by setting the following System Property:
 * </p>
 *
 * <ul>
 * <li><code>-DSupportLogger.adapter.fqcn=org.broadleafcommerce.common.logging.DisableSupportLoggerAdapter</code></li>
 * </ul>
 *
 * <p>
 * The main requirements for SUPPORT level logging are to:
 * <ul>
 * <li>show up in the logs as a SUPPORT item (not ERROR or WARN etc...)</li>
 * <li>always show up unless configured otherwise, as we want users to always see SUPPORT messages</li>
 * <li>allow state messages to be associated with the log (e.g. "Enterprise Module — …")</li>
 * </ul>
 * </p>
 *
 * @author Jeff Fischer
 * @author elbertbautista
 *
 */
public class SupportLogger {

    private static final Log LOG = LogFactory.getLog(SupportLogger.class);
    public static final String FQCN_KEY =  "SupportLogger.adapter.fqcn";

    private String moduleName;
    private SupportLoggerAdapter adapter;

    public SupportLogger(String moduleName, String name) {
        this.moduleName = moduleName;

        String fqcn = getSupportLoggerAdapterFQCN();
        if (StringUtils.isNotBlank(fqcn)) {
            try {
                adapter = (SupportLoggerAdapter) Class.forName(fqcn).newInstance();
                adapter.setName(name);
            } catch (InstantiationException e) {
                LOG.error("Unable to create instance of SupportLogger [" + fqcn + "] Creating default logger.", e);
            } catch (IllegalAccessException e) {
                LOG.error("Unable to create instance of SupportLogger [" + fqcn + "] Creating default logger.", e);
            } catch (ClassNotFoundException e) {
                LOG.error("Unable to create instance of SupportLogger [" + fqcn + "] Creating default logger.", e);
            }
        }

        if (adapter == null) {
            adapter = new SystemSupportLoggerAdapter();
            adapter.setName(name);
        }
    }

    /**
     * emit a SUPPORT level message
     * @param message
     */
    public void support(Object message) {
        adapter.support(moduleName + " - " + message);
    }

    /**
     * emit a SUPPORT level message with throwable
     * @param message
     * @param t
     */
    public void support(Object message, Throwable t) {
        adapter.support(moduleName + " - " + message, t);
    }

    /**
     * emit a SUPPORT lifecycle message
     * @param lifeCycleEvent
     * @param message
     */
    public void lifecycle(LifeCycleEvent lifeCycleEvent, Object message) {
        adapter.lifecycle(lifeCycleEvent, moduleName + " - " + lifeCycleEvent.toString() + (!StringUtils.isEmpty(message.toString())?" - " + message:""));
    }

    /**
     * In order to be backwards compatible. The support logger should also support
     * the debug, error, fatal, info, and warn levels as well.
     * @param message
     */

    public void debug(Object message) {
        adapter.debug(moduleName + " - " + message);
    }

    public void debug(Object message, Throwable t) {
        adapter.debug(moduleName + " - " + message, t);
    }

    public void error(Object message) {
        adapter.error(moduleName + " - " + message);
    }

    public void error(Object message, Throwable t) {
        adapter.error(moduleName + " - " + message, t);
    }

    public void fatal(Object message) {
        adapter.fatal(moduleName + " - " + message);
    }

    public void fatal(Object message, Throwable t) {
        adapter.fatal(moduleName + " - " + message, t);
    }

    public void info(Object message) {
        adapter.info(moduleName + " - " + message);
    }

    public void info(Object message, Throwable t) {
        adapter.info(moduleName + " - " + message, t);
    }

    public void warn(Object message) {
        adapter.warn(moduleName + " - " + message);
    }

    public void warn(Object message, Throwable t) {
        adapter.warn(moduleName + " - " + message, t);
    }

    public static String getSupportLoggerAdapterFQCN() {
        return System.getProperty(FQCN_KEY);
    }

}
