package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.IdGeneration;
import org.broadleafcommerce.profile.domain.IdGenerationImpl;
import org.broadleafcommerce.profile.service.IdGenerationService;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class IdGenerationTest extends BaseTest {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private IdGenerationService idGenerationService;

    List<Long> userIds = new ArrayList<Long>();

    List<String> userNames = new ArrayList<String>();

    @Test(groups = "createId")
    @Rollback(false)
    public void createId() {
        IdGeneration idGeneration = new IdGenerationImpl();
        idGeneration.setType("CustomerImpl");
        idGeneration.setBatchStart(1L);
        idGeneration.setBatchSize(10L);
        em.persist(idGeneration);
    }

    @Test(groups = "findIds", dependsOnGroups = "createId")
    @Rollback(true)
    public void findIds() {
        for (int i = 1; i < 101; i++) {
            Long id = idGenerationService.findNextId("CustomerImpl");
            assert id == i;
        }
    }
}
