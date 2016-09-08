/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.broadleafcommerce.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * <p>An SLF4J implementation of SupportLoggerAdapter that will delegate to the
 * configured SLF4J logging framework.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class SLF4JSupportLoggerAdapter extends AbstractSupportLoggerAdapter implements SupportLoggerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SLF4JSupportLoggerAdapter.class);
    public static final String DEFAULT_LEVEL_KEY = "SLF4JSupportLoggerAdapter.defaultLevel";

    private String name;

    @Override
    public void support(String message) {
        mapSupportLevel(StringUtil.sanitize(message), null);
    }

    @Override
    public void support(String message, Throwable t) {
        mapSupportLevel(StringUtil.sanitize(message), t);
    }

    @Override
    public void lifecycle(LifeCycleEvent lifeCycleEvent, String message) {
        mapSupportLevel(StringUtil.sanitize(message), null);
    }

    @Override
    public void debug(String message) {
        LOGGER.debug(StringUtil.sanitize(message));
    }

    @Override
    public void debug(String message, Throwable t) {
        LOGGER.debug(StringUtil.sanitize(message), t);
    }

    @Override
    public void error(String message) {
        LOGGER.error(StringUtil.sanitize(message));
    }

    @Override
    public void error(String message, Throwable t) {
        LOGGER.error(StringUtil.sanitize(message), t);
    }

    /**
     * Mapping FATAL to ERROR as the SLF4J API does not contain a fatal level
     * @param message
     */
    @Override
    public void fatal(String message) {
        LOGGER.error(StringUtil.sanitize(message));
    }

    /**
     * Mapping FATAL to ERROR as the SLF4J API does not contain a fatal level
     * @param message
     * @param t
     */
    @Override
    public void fatal(String message, Throwable t) {
        LOGGER.error(StringUtil.sanitize(message), t);
    }

    @Override
    public void info(String message) {
        LOGGER.info(StringUtil.sanitize(message));
    }

    @Override
    public void info(String message, Throwable t) {
        LOGGER.info(StringUtil.sanitize(message), t);
    }

    @Override
    public void warn(String message) {
        LOGGER.warn(StringUtil.sanitize(message));
    }

    @Override
    public void warn(String message, Throwable t) {
        LOGGER.warn(StringUtil.sanitize(message), t);
    }

    protected void mapSupportLevel(String message, Throwable t) {
        Marker supportMarker = MarkerFactory.getMarker(SUPPORT);

        switch (getSupportLevel()) {
            case LOG_LEVEL_ERROR:
                LOGGER.error(supportMarker, message, t);
                break;
            case LOG_LEVEL_INFO:
                LOGGER.info(supportMarker, message, t);
                break;
            case LOG_LEVEL_DEBUG:
                LOGGER.debug(supportMarker, message, t);
                break;
            case LOG_LEVEL_TRACE:
                LOGGER.trace(supportMarker, message, t);
                break;
            default:
                LOGGER.warn(supportMarker, message, t);
        }

    }

    public int getSupportLevel() {
        String systemProperty = System.getProperty(DEFAULT_LEVEL_KEY, LOG_LEVEL_WARN+"");
        return Integer.valueOf(systemProperty);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
