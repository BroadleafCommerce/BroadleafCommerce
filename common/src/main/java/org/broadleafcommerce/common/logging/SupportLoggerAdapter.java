/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.logging;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface SupportLoggerAdapter {

    String getName();

    void setName(String name);

    /**
     * emit a SUPPORT level message
     *
     * @param message
     */
    void support(String message);

    /**
     * emit a SUPPORT level message with throwable
     *
     * @param message
     * @param t
     */
    void support(String message, Throwable t);

    /**
     * emit a SUPPORT lifecycle message
     *
     * @param lifeCycleEvent
     * @param message
     */
    void lifecycle(LifeCycleEvent lifeCycleEvent, String message);

    /**
     * In order to be backwards compatible. The support logger should also support
     * the debug, error, fatal, info, and warn levels as well.
     *
     * @param message
     */
    void debug(String message);

    void debug(String message, Throwable t);

    void error(String message);

    void error(String message, Throwable t);

    void fatal(String message);

    void fatal(String message, Throwable t);

    void info(String message);

    void info(String message, Throwable t);

    void warn(String message);

    void warn(String message, Throwable t);

}
