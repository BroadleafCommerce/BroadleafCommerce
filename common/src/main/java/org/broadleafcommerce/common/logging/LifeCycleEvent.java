package org.broadleafcommerce.common.logging;

/**
 * Enumeration describes the type of event that is being logged in the
 * SupportLogger.lifecycle method.
 *
 * @author Jeff Fischer
 */
public enum LifeCycleEvent {
    START,
    END,
    TRANSFORM,
    LOADING,
    CONFIG
}
