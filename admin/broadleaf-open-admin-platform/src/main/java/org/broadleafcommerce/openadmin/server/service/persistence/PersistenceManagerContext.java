/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;

import java.util.Stack;

/**
 * @author Jeff Fischer
 */
public class PersistenceManagerContext {

    private static final ThreadLocal<PersistenceManagerContext> BROADLEAF_PERSISTENCE_MANAGER_CONTEXT = ThreadLocalManager.createThreadLocal(PersistenceManagerContext.class, false);

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
        this.persistenceManager.add(persistenceManager);
    }

    public PersistenceManager getPersistenceManager() {
        return !persistenceManager.empty()?persistenceManager.peek():null;
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
