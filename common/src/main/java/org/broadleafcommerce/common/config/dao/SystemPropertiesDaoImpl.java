/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.config.dao;

import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.domain.SystemPropertyImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * This DAO enables access to manage system properties that can be stored in the database.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
@Repository("blSystemPropertiesDao")
public class SystemPropertiesDaoImpl implements SystemPropertiesDao{

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public SystemProperty readById(Long id) {
        return em.find(SystemPropertyImpl.class, id);
    }

    @Override
    public SystemProperty saveSystemProperty(SystemProperty systemProperty) {
        return em.merge(systemProperty);
    }

    @Override
    public void deleteSystemProperty(SystemProperty systemProperty) {
        em.remove(systemProperty);
    }

    @Override
    public List<SystemProperty> readAllSystemProperties() {
        Query query = em.createNamedQuery("BC_READ_ALL_SYSTEM_PROPERTIES");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public SystemProperty readSystemPropertyByName(String name) {
        Query query = em.createNamedQuery("BC_READ_SYSTEM_PROPERTIES_BY_NAME");
        query.setParameter("propertyName", name);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        List<SystemProperty> props = query.getResultList();
        if (props != null && ! props.isEmpty()) {
            return props.get(0);
        }
        return null;
    }

    @Override
    public SystemProperty createNewSystemProperty() {
        return (SystemProperty)entityConfiguration.createEntityInstance(SystemProperty.class.getName());
    }
}
