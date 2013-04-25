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

package org.broadleafcommerce.common.util.dao;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class BatchRetrieveDao {
    
    //Default batch read size
    private int inClauseBatchSize = 300;
    
    @SuppressWarnings("unchecked")
    public <T> List<T> batchExecuteReadQuery(Query query, List<?> params, String parameterName) {
        List<T> response = new ArrayList<T>();
        int start = 0;
        while (start < params.size()) {
            List<?> batchParams = params.subList(start, params.size() < inClauseBatchSize ? params.size() : inClauseBatchSize);
            query.setParameter(parameterName, batchParams);
            response.addAll(query.getResultList());
            start += inClauseBatchSize;
        }
        return response;
    }

    public int getInClauseBatchSize() {
        return inClauseBatchSize;
    }

    public void setInClauseBatchSize(int inClauseBatchSize) {
        this.inClauseBatchSize = inClauseBatchSize;
    }
    
}
