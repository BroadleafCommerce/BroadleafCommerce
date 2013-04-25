/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
