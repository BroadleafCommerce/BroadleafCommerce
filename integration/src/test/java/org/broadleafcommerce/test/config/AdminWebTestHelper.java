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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxManagement;
import org.broadleafcommerce.common.sandbox.domain.SandBoxManagementImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.sandbox.service.SandBoxService;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.DeployBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 * Helper class for working with MockMVC and {@link AdminWebTestContextConfiguration} based integration tests.
 *
 * @author Jeff Fischer
 */
public class AdminWebTestHelper {

    public static final String DEFAULT_SB = "Default";

    @Autowired
    @Qualifier("blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected SandBoxService sandBoxService;

    /**
     * Simple pause
     *
     * @param wait
     */
    public void pause(Long wait) {
        try {
            Thread.sleep(wait);
        } catch (Throwable e) {
            //do nothing
        }
    }

    /**
     * Try a test assertion repeatedly encapsulated in a Runnable. This is useful for
     * checking state of an async operation. In enterprise, this is useful for checking on the
     * finish state of a admin promotion or deployment.
     *
     * @param retryCount
     * @param wait
     * @param runnable
     */
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
        startView(null, null);
    }

    public void startView(Long siteId) {
        startView(siteId, null);
    }

    /**
     * Useful to start an EntityManager-In-View. This allows test operations that want to read directly from the database
     * to work without lazy init exceptions, etc... This is not needed for MockMVC#perform operations, since those
     * requests will include the OpenEntityManagerInView filter as part of their flow. At the completion of the test
     * operation, the {@link #endView()} method should be called to end the scope of the view.
     */
    public void startView(Long siteId, String sandBoxName) {
        EntityManagerFactory emf = ((JpaTransactionManager) transUtil.getTransactionManager()).getEntityManagerFactory();
        boolean isEntityManagerInView = TransactionSynchronizationManager.hasResource(emf);
        if (!isEntityManagerInView) {
            EntityManager em = emf.createEntityManager();
            em.clear();
            EntityManagerHolder emHolder = new EntityManagerHolder(em);
            TransactionSynchronizationManager.bindResource(emf, emHolder);
            Stack<Integer> stack = new Stack<>();
            TransactionSynchronizationManager.bindResource("emStack", stack);
            if (siteId != null) {
                Site site = siteService.retrievePersistentSiteById(siteId);
                ThreadLocalManager.remove();
                BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
                context.setSite(site);
                context.setDeployBehavior(DeployBehavior.CLONE_PARENT);
                context.setAdmin(true);
                if (!StringUtils.isEmpty(sandBoxName)) {
                    SandBox sb = findSandBox(sandBoxName, em);
                    context.setSandBox(sb);
                }
            }
        } else {
            Stack<Integer> stack = (Stack<Integer>) TransactionSynchronizationManager.getResource("emStack");
            stack.push(1);
        }
    }

    /**
     * Complete the scope of the EntityManager-In-View operation. See {@link #startView(Long, String)}.
     */
    public void endView() {
        EntityManagerFactory emf = ((JpaTransactionManager) transUtil.getTransactionManager()).getEntityManagerFactory();
        boolean isEntityManagerInView = TransactionSynchronizationManager.hasResource(emf);
        if (isEntityManagerInView) {
            Stack<Integer> stack = (Stack<Integer>) TransactionSynchronizationManager.getResource("emStack");
            if (stack.empty()) {
                EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.unbindResource(emf);
                EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
                TransactionSynchronizationManager.unbindResource("emStack");
                ThreadLocalManager.remove();
            } else {
                stack.pop();
            }
        }
    }

    public SandBox findSandBox(Long siteId, String sandBoxName) {
        if (DEFAULT_SB.equals(sandBoxName)) {
            return getDefaultSandBox(siteId);
        } else {
            startView(siteId, sandBoxName);
            try {
                EntityManagerFactory emf = ((JpaTransactionManager) transUtil.getTransactionManager()).getEntityManagerFactory();

                EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
                return findSandBox(sandBoxName, emHolder.getEntityManager());
            } finally {
                endView();
            }
        }
    }

    private SandBox getDefaultSandBox(Long siteId) {
        startView(siteId);
        try {
            return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<SandBox, RuntimeException>() {
                @Override
                public SandBox execute() {
                    List<SandBox> defaultSandBoxes = sandBoxService.retrieveSandBoxesByType(SandBoxType.DEFAULT);
                    if (defaultSandBoxes.size() > 1) {
                        throw new IllegalStateException("Only one sandbox should be configured as default");
                    }

                    SandBox defaultSandBox;
                    if (defaultSandBoxes.size() == 1) {
                        defaultSandBox = defaultSandBoxes.get(0);
                    } else {
                        defaultSandBox = sandBoxService.createDefaultSandBox();
                    }

                    SandBox sandBox = sandBoxService.retrieveUserSandBoxForParent(-1L, defaultSandBox.getId());
                    if (sandBox == null) {
                        sandBox = sandBoxService.createUserSandBox(-1L, defaultSandBox);
                    }
                    return sandBox;
                }
            }, siteService.retrievePersistentSiteById(siteId));
        } finally {
            endView();
        }
    }

    private SandBox findSandBox(String sandBoxName, EntityManager em) {
        String queryString = "select root from " + SandBoxManagementImpl.class.getName() + " root where root.sandBox.name = '"+sandBoxName+"' and root.sandBox.sandboxType = 'USER'";
        TypedQuery<SandBoxManagement> query = em.createQuery(queryString, SandBoxManagement.class);
        return query.getSingleResult().getSandBox();
    }

}
