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

import org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.time.SystemTime;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blModuleConfigurationDao")
public class ModuleConfigurationDaoImpl implements ModuleConfigurationDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected Long currentDateResolution = 10000L;
    protected Date cachedDate = SystemTime.asDate();

    protected Date getCurrentDateAfterFactoringInDateResolution() {
        Date returnDate = SystemTime.getCurrentDateWithinTimeResolution(cachedDate, currentDateResolution);
        if (returnDate != cachedDate) {
            if (SystemTime.shouldCacheDate()) {
                cachedDate = returnDate;
            }
        }
        return returnDate;
    }

    @Override
    public ModuleConfiguration readById(Long id) {
        return em.find(AbstractModuleConfiguration.class, id);
    }

    @Override
    public ModuleConfiguration save(ModuleConfiguration config) {
        if (config.getIsDefault()) {
            Query batchUpdate = em.createNamedQuery("BC_BATCH_UPDATE_MODULE_CONFIG_DEFAULT");
            batchUpdate.setParameter("configType", config.getModuleConfigurationType().getType());
            batchUpdate.executeUpdate();
        }
        return em.merge(config);
    }

    @Override
    public void delete(ModuleConfiguration config) {
        ((Status) config).setArchived('Y');
        em.merge(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ModuleConfiguration> readAllByType(ModuleConfigurationType type) {
        Query query = em.createNamedQuery("BC_READ_MODULE_CONFIG_BY_TYPE");
        query.setParameter("configType", type.getType());
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blConfigurationModuleElements");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ModuleConfiguration> readActiveByType(ModuleConfigurationType type) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_MODULE_CONFIG_BY_TYPE");
        query.setParameter("configType", type.getType());

        Date myDate = getCurrentDateAfterFactoringInDateResolution();

        query.setParameter("currentDate", myDate);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blConfigurationModuleElements");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ModuleConfiguration> readByType(Class<? extends ModuleConfiguration> type) {
        //TODO change this to a JPA criteria expression
        Query query = em.createQuery("SELECT config FROM " + type.getName() + " config");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "blConfigurationModuleElements");
        return query.getResultList();
    }

    @Override
    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    @Override
    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }
}
