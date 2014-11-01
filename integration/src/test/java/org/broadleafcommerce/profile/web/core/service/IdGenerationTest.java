/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.profile.web.core.service;

import org.broadleafcommerce.profile.core.domain.IdGeneration;
import org.broadleafcommerce.profile.core.domain.IdGenerationImpl;
import org.broadleafcommerce.profile.core.service.IdGenerationService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class IdGenerationTest extends BaseTest {

    @Resource
    private IdGenerationService idGenerationService;

    List<Long> userIds = new ArrayList<Long>();

    List<String> userNames = new ArrayList<String>();

    @Test(groups = "createId")
    @Rollback(false)
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
