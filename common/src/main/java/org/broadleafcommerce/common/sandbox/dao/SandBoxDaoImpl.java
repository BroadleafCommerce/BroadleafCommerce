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

package org.broadleafcommerce.common.sandbox.dao;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Repository("blSandBoxDao")
public class SandBoxDaoImpl implements SandBoxDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager sandBoxEntityManager;
    
    @Resource(name = "blTransactionManager")
    JpaTransactionManager transactionManager;

    @Override
    public SandBox retrieve(Long id) {
        return  sandBoxEntityManager.find(SandBoxImpl.class, id);
    }

    @Override
    public SandBox retrieveSandBoxByType(Site site, SandBoxType sandboxType) {
        TypedQuery<SandBox> query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE", SandBox.class);
        //query.setParameter("site", site);
        query.setParameter("sandboxType", sandboxType.getType());
        SandBox response = null;
        try {
            response = query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }

    @Override
    public SandBox retrieveNamedSandBox(Site site, SandBoxType sandboxType, String sandboxName) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE_AND_NAME");
        //query.setParameter("site", site);
        query.setParameter("sandboxType", sandboxType.getType());
        query.setParameter("sandboxName", sandboxName);
        SandBox response = null;
        try {
            response = (SandBox) query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing - there is no sandbox
        }
        return response;
    }

    @Override
    public SandBox persist(SandBox entity) {
        sandBoxEntityManager.persist(entity);
        sandBoxEntityManager.flush();
        return entity;
    }

    public SandBox createSandBox(Site site, String sandBoxName, SandBoxType sandBoxType) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("createSandBox");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            SandBox approvalSandbox = retrieveNamedSandBox(site, sandBoxType, sandBoxName);
            if (approvalSandbox == null) {
                approvalSandbox = new SandBoxImpl();
                approvalSandbox.setSite(site);
                approvalSandbox.setName(sandBoxName);
                approvalSandbox.setSandBoxType(sandBoxType);
                approvalSandbox = persist(approvalSandbox);
            }
            transactionManager.commit(status);
            return approvalSandbox;
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw new RuntimeException(ex);
        }
    }
   
}
