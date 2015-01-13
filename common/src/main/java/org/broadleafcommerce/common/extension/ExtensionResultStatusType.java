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
