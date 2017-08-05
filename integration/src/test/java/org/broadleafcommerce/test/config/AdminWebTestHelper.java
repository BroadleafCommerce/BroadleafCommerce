/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.test.config;

import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Jeff Fischer
 */
public class AdminWebTestHelper {

    @Autowired
    @Qualifier("blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;


    public void pause(Long wait) {
        try {
            Thread.sleep(wait);
        } catch (Throwable e) {
            //do nothing
        }
    }

    public void tryWithPause(Integer retryCount, Long wait, Runnable runnable) {
        int count = 0;
        while (count < retryCount) {
            try {
                pause(wait);
                runnable.run();
            } catch (AssertionError e) {
                if (count > retryCount) {
                    throw e;
                }
            } finally {
                count++;
            }
        }
    }

    public void startView() {
        EntityManagerFactory emf = ((JpaTransactionManager) transUtil.getTransactionManager()).getEntityManagerFactory();
        boolean isEntityManagerInView = TransactionSynchronizationManager.hasResource(emf);
        if (!isEntityManagerInView) {
            EntityManager em = emf.createEntityManager();
            EntityManagerHolder emHolder = new EntityManagerHolder(em);
            TransactionSynchronizationManager.bindResource(emf, emHolder);
        }
    }

    public void endView() {
        EntityManagerFactory emf = ((JpaTransactionManager) transUtil.getTransactionManager()).getEntityManagerFactory();
        boolean isEntityManagerInView = TransactionSynchronizationManager.hasResource(emf);
        if (isEntityManagerInView) {
            EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.unbindResource(emf);
            EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
        }
    }

}
