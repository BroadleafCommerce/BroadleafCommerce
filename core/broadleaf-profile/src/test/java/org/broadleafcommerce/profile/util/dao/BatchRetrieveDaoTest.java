/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.util.dao;

import junit.framework.TestCase;
import org.broadleafcommerce.common.util.dao.BatchRetrieveDao;
import org.easymock.classextension.EasyMock;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class BatchRetrieveDaoTest extends TestCase {
    
    private static final int BATCHSIZE = 5;
    private BatchRetrieveDao dao;
    private Query queryMock;
    
    @Override
    protected void setUp() throws Exception {
        dao = new BatchRetrieveDao();
        queryMock = EasyMock.createMock(Query.class);
        List<String> response = new ArrayList<String>();
        response.add("test");
        EasyMock.expect(queryMock.getResultList()).andReturn(response).times(2);
        EasyMock.expect(queryMock.setParameter(EasyMock.eq("test"), EasyMock.isA(List.class))).andReturn(queryMock).times(2);
    }

    public void testFilter() throws Exception {
        EasyMock.replay(queryMock);
        dao.setInClauseBatchSize(BATCHSIZE);
        List<Integer> keys = new ArrayList<Integer>();
        for (int j = 0; j < 10; j++) {
            keys.add(j);
        }
        List<Object> response = dao.batchExecuteReadQuery(queryMock, keys, "test");
        assertTrue(response.size() == 2);
        EasyMock.verify(queryMock);
    }

}
