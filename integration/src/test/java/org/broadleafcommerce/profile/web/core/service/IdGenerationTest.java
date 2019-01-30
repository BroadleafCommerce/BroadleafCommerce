/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.profile.web.core.service;

import org.broadleafcommerce.common.id.domain.IdGeneration;
import org.broadleafcommerce.common.id.domain.IdGenerationImpl;
import org.broadleafcommerce.common.id.service.IdGenerationService;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class IdGenerationTest extends TestNGSiteIntegrationSetup {

    @Resource
    private IdGenerationService idGenerationService;
    
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    List<Long> userIds = new ArrayList<>();

    List<String> userNames = new ArrayList<>();

    @Test(groups = "createId")
    @Rollback(false)
    @Transactional
    public void createId() {
        IdGeneration idGeneration = new IdGenerationImpl();
        idGeneration.setType("IdGenerationTest");
        idGeneration.setBatchStart(1L);
        idGeneration.setBatchSize(10L);
        em.persist(idGeneration);
    }

    @Test(groups = "findIds", dependsOnGroups = "createId")
    @Rollback(true)
    public void findIds() {
        for (int i = 1; i < 101; i++) {
            Long id = idGenerationService.findNextId("IdGenerationTest");
            assert id == i;
        }
    }

    @Test(groups = "createIdForBeginEndSequence")
    @Rollback(false)
    @Transactional
    public void createIdForBeginEndSequence() {
        IdGeneration idGeneration = new IdGenerationImpl();
        idGeneration.setType("IdGenerationBeginEndTest");
        idGeneration.setBegin(1L);
        idGeneration.setEnd(10L);
        idGeneration.setBatchStart(1L);
        idGeneration.setBatchSize(3L);
        em.persist(idGeneration);
    }

    @Test(groups = "findIdsForBeginEndSequence", dependsOnGroups = "createIdForBeginEndSequence")
    @Rollback(true)
    public void findIdsForBeginEndSequence() {
        for (int i = 1; i < 101; i++) {
            Long id = idGenerationService.findNextId("IdGenerationBeginEndTest");
            int expected = i % 10;
            if (expected == 0) {
                expected = 10;
            }
            //System.out.println("jbtest: i=" + i + ", id=" + id + ", expected=" + expected);
            assert id == expected;
        }
    }
}
