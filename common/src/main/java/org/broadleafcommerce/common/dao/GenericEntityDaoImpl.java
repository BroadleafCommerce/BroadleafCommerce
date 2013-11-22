/*
 * #%L
 * BroadleafCommerce Profile Web
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

package org.broadleafcommerce.common.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Repository("blGenericEntityDao")
public class GenericEntityDaoImpl implements GenericEntityDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    protected DynamicDaoHelperImpl daoHelper = new DynamicDaoHelperImpl();
    
    @Override
    @SuppressWarnings("rawtypes")
    public Object readGenericEntity(Class<?> clazz, Object id) {
        Map<String, Object> md = daoHelper.getIdMetadata(clazz, (HibernateEntityManager) em);
        AbstractSingleColumnStandardBasicType type = (AbstractSingleColumnStandardBasicType) md.get("type");
        
        if (type instanceof LongType) {
            id = Long.parseLong(String.valueOf(id));
        } else if (type instanceof IntegerType) {
            id = Integer.parseInt(String.valueOf(id));
        }

        return em.find(clazz, id);
    }

    @Override
    public Class<?> getImplClass(String className) {
        Class<?> clazz = entityConfiguration.lookupEntityClass(className);
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return clazz;
    }

}
