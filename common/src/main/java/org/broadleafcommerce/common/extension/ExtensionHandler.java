/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.extension;



/**
 * <p>An extension handler represents a generic pattern used in BroadleafCommerce when an out-of-box service
 * with complex logic provides implementation hooks.</p>
 * 
 * <p>The pattern is primarily used internally by Broadleaf as a mechanism to provide extension points for 
 * Broadleaf modules.</p>
 * 
 * <p>Consumers of BroadleafCommerce framework typically would not need to use this pattern and instead would opt. 
 * for more typical extension patterns including overriding or extending the actual component for which 
 * alternate behavior is desired.</p>
 * 
 * <p>ExtensionHandler api methods should always return an instance of {@link ExtensionResultStatusType} and will usually
 * extend from {@link AbstractExtensionHandler}</p>
 * 
 * <p>In order to associate an {@link ExtensionHandler) with an {@link ExtensionManager}, each handler should have an @PostConstruct
 * override and associate itself with the manager:</p>
 * 
 * <pre>
 *  {@code
 *    {@literal @}Resource(name = "blSomeExtensionManager")
 *    protected ExtensionManager extensionManager;
 *
 *    {@literal @}PostConstruct
 *    public void init() {
 *       if (isEnabled()) {
 *           extensionManager.registerHandler(this);
 *       }
 *    }
 *  }
 * </pre>
 * 
 * 
 * @author bpolster
 * @see {@link AbstractExtensionHandler}
 */
public interface ExtensionHandler {

    /**
     * Determines the priority of this extension handler.
     * @return
     */
    public int getPriority();

    /**
     * If false, the ExtensionManager should skip this Handler.
     * @return
     */
    public boolean isEnabled();
}
