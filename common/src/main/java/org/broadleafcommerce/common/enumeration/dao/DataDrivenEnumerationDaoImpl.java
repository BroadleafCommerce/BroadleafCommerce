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

package org.broadleafcommerce.common.enumeration.dao;

import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValue;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


@Repository("blDataDrivenEnumerationDao")
public class DataDrivenEnumerationDaoImpl implements DataDrivenEnumerationDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public DataDrivenEnumeration readEnumByKey(String enumKey) {
        TypedQuery<DataDrivenEnumeration> query = new TypedQueryBuilder<DataDrivenEnumeration>(DataDrivenEnumeration.class, "dde")
            .addRestriction("dde.key", "=", enumKey)
            .toQuery(em);
        return query.getSingleResult();
    }
    
    @Override
    public DataDrivenEnumerationValue readEnumValueByKey(String enumKey, String enumValueKey) {
        TypedQuery<DataDrivenEnumerationValue> query = 
                new TypedQueryBuilder<DataDrivenEnumerationValue>(DataDrivenEnumerationValue.class, "ddev")
            .addRestriction("ddev.type.key", "=", enumKey)
            .addRestriction("ddev.key", "=", enumValueKey)
            .toQuery(em);
        return query.getSingleResult();
    }

}
