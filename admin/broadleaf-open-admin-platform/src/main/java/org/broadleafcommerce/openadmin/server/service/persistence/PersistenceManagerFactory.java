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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Service("blPersistenceManagerFactory")
public class PersistenceManagerFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static final Map<TargetModeType, PersistenceManager> persistenceManagers = new HashMap<TargetModeType, PersistenceManager>();
    public static final String DEFAULTPERSISTENCEMANAGERREF = "blPersistenceManager";
    protected static String persistenceManagerRef = DEFAULTPERSISTENCEMANAGERREF;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PersistenceManagerFactory.applicationContext = applicationContext;
    }

    public static PersistenceManager getPersistenceManager() {
        if (PersistenceManagerContext.getPersistenceManagerContext() != null) {
            return PersistenceManagerContext.getPersistenceManagerContext().getPersistenceManager();
        }
        throw new IllegalStateException("PersistenceManagerContext is not set on ThreadLocal. If you want to use the " +
                "non-cached version, try getPersistenceManager(TargetModeType)");
    }

    public static PersistenceManager getPersistenceManager(TargetModeType targetModeType) {
        synchronized (persistenceManagers) {
            if (!persistenceManagers.containsKey(targetModeType)) {
                PersistenceManager persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
                persistenceManager.setTargetMode(targetModeType);
                persistenceManagers.put(targetModeType, persistenceManager);
            }
            return persistenceManagers.get(targetModeType);
        }
    }

    public static boolean isPersistenceManagerActive() {
        return applicationContext.containsBean(getPersistenceManagerRef());
    }

    public static void startPersistenceManager(TargetModeType targetModeType) {
        PersistenceManagerContext context = PersistenceManagerContext.getPersistenceManagerContext();
        if (context == null) {
            context = new PersistenceManagerContext();
            PersistenceManagerContext.addPersistenceManagerContext(context);
        }
        context.addPersistenceManager(getPersistenceManager(targetModeType));
    }

    public static void endPersistenceManager() {
        PersistenceManagerContext context = PersistenceManagerContext.getPersistenceManagerContext();
        if (context != null) {
            context.remove();
        }
    }

    public static String getPersistenceManagerRef() {
        return persistenceManagerRef;
    }

    public static void setPersistenceManagerRef(String persistenceManagerRef) {
        PersistenceManagerFactory.persistenceManagerRef = persistenceManagerRef;
    }
}
