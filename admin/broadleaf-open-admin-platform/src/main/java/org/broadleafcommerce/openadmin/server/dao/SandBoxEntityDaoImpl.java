/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//@Repository("blSandBoxEntityDao")
public class SandBoxEntityDaoImpl implements SandBoxEntityDao {

    //@PersistenceContext(unitName = "blSandboxPU")
    protected EntityManager sandBoxEntityManager;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao#persist(org.broadleafcommerce.openadmin.server.domain.SandBox)
     */
    @Override
    public SandBox persist(SandBox entity) {
        sandBoxEntityManager.persist(entity);
        sandBoxEntityManager.flush();
        return entity;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao#merge(org.broadleafcommerce.openadmin.server.domain.SandBox)
     */
    @Override
    public SandBox merge(SandBox entity) {
        SandBox response = sandBoxEntityManager.merge(entity);
        sandBoxEntityManager.flush();
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao#retrieve(java.lang.Class, java.lang.Object)
     */
    @Override
    public SandBox retrieve(Long id) {
        return sandBoxEntityManager.find(SandBox.class, id);
    }
    
    public EntityManager getSandBoxEntityManager() {
        return sandBoxEntityManager;
    }

    public void setSandBoxEntityManager(EntityManager sandBoxEntityManager) {
        this.sandBoxEntityManager = sandBoxEntityManager;
    }

    @Override
    public EntitySandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_ITEM_BY_TEMPORARY_ID");
        query.setParameter("temporaryId", temporaryId);
        EntitySandBoxItem response = null;
        try {
            response = (EntitySandBoxItem) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }

    public void deleteEntitySandBoxItem(EntitySandBoxItem sandBoxItem) {
        if (!sandBoxEntityManager.contains(sandBoxItem)) {
            sandBoxItem = retrieveSandBoxItemByTemporaryId(sandBoxItem.getTemporaryId());
        }
        sandBoxEntityManager.remove(sandBoxItem);
    }


    public SandBox retrieveSandBoxByType(Site site, SandBoxType sandboxType) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE");
        query.setParameter("site", site);
        query.setParameter("sandboxType", sandboxType.getType());
        SandBox response = null;
        try {
            response = (SandBox) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }

    public SandBox retrieveNamedSandBox(Site site, SandBoxType sandBoxType, String sandboxName) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE_AND_NAME");
        query.setParameter("site", site);
        query.setParameter("sandboxType", sandBoxType.getType());
        query.setParameter("sandboxName", sandboxName);
        SandBox response = null;
        try {
            response = (SandBox) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }
}
