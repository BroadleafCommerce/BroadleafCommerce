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

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.TargetModeType;
import org.broadleafcommerce.common.service.EntityManagerIdentificationService;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
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

    protected static EntityManagerIdentificationService emIdentificationService;

    @Autowired
    public PersistenceManagerFactory(EntityManagerIdentificationService emIdentificationService) {
        PersistenceManagerFactory.emIdentificationService = emIdentificationService;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PersistenceManagerFactory.applicationContext = applicationContext;
    }

    /**
     * @deprecated in favor of {@link #getPersistenceManager(String)} which configures the {@link PersistenceManager}
     *  correctly, based on the provided className. If the {@link PersistenceManager} is configured with the
     *  incorrect {@link javax.persistence.EntityManager} for the given class, then requests in the Admin are likely to fail.
     */
    @Deprecated
    public static PersistenceManager getPersistenceManager() {
        return getDefaultPersistenceManager();
    }

    public static PersistenceManager getDefaultPersistenceManager() {
        return getPersistenceManager(TargetModeType.SANDBOX);
    }

    public static PersistenceManager getPersistenceManager(PersistencePackageRequest ppr) {
        String className = ppr.getCeilingEntityClassname();
        return getPersistenceManager(className);
    }

    public static PersistenceManager getPersistenceManager(PersistencePackage pkg) {
        String className = pkg.getCeilingEntityFullyQualifiedClassname();
        return getPersistenceManager(className);
    }

    public static PersistenceManager getPersistenceManager(String className) {
        try {
            TargetModeType targetModeType = emIdentificationService.identifyTargetModeTypeForClass(className);
            return PersistenceManagerFactory.getPersistenceManager(targetModeType);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
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
