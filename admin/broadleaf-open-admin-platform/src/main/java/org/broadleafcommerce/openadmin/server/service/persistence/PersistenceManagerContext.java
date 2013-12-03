/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence;

import java.util.Stack;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;

/**
 * @author Jeff Fischer
 */
public class PersistenceManagerContext {

    private static final ThreadLocal<PersistenceManagerContext> BROADLEAF_PERSISTENCE_MANAGER_CONTEXT = ThreadLocalManager.createThreadLocal(PersistenceManagerContext.class);

    public static PersistenceManagerContext getPersistenceManagerContext() {
        return BROADLEAF_PERSISTENCE_MANAGER_CONTEXT.get();
    }

    public static void addPersistenceManagerContext(PersistenceManagerContext persistenceManagerContext) {
        BROADLEAF_PERSISTENCE_MANAGER_CONTEXT.set(persistenceManagerContext);
    }

    private static void clear() {
        BROADLEAF_PERSISTENCE_MANAGER_CONTEXT.remove();
    }

    private final Stack<PersistenceManager> persistenceManager = new Stack<PersistenceManager>();

    public void addPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager.push(persistenceManager);
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager.peek();
    }

    public void remove() {
        if (!persistenceManager.empty()) {
            persistenceManager.pop();
        }
        if (persistenceManager.empty()) {
            PersistenceManagerContext.clear();
        }
    }
}
