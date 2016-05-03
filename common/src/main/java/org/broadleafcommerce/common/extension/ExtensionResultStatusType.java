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
package org.broadleafcommerce.common.extension;


public enum ExtensionResultStatusType {
    /**
     * <p>
     * In short, this value should only be returned from {@link ExtensionManager}s and not from {@link ExtensionHandler}s.
     * 
     * <p>
     * When you are returning results from an {@link ExtensionHandler} then it is less ambiguous to {@link ExtensionManager}s
     * to instead return {@link ExtensionResultStatusType#HANDLED_CONTINUE} or
     * {@link ExtensionResultStatusType#HANDLED_CONTINUE} instead. However, when checking the result of invoking an
     * {@link ExtensionManager} to see if it was handled by <b>ANY</b> {@link ExtensionHandler} then it makes sense
     * to return this enum instead
     */
    HANDLED,
    
    /**
     * The extension was handled by the {@link ExtensionHandler} and it recommends continuing with additional
     * {@link ExtensionHandler}s (if more are available).
     */
    HANDLED_CONTINUE,
    
    /**
     * An {@link ExtensionHandler} has handled this and it recommends that the {@link ExtensionManager} should not invoke
     * any more {@link ExtensionHandler}s that it may have to execute
     */
    HANDLED_STOP,
    
    /**
     * This was not handled by the {@link ExtensionHandler}. In the context of the result of an {@link ExtensionManager},
     * this indicates that it was not executed by any of the registered {@link ExtensionHandler}s, or that none were
     * registered in the first place
     */
    NOT_HANDLED
}
