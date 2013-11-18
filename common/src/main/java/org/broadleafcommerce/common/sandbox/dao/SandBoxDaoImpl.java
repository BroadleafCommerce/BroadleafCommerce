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
package org.broadleafcommerce.common.sandbox.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

@Repository("blSandBoxDao")
public class SandBoxDaoImpl implements SandBoxDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager sandBoxEntityManager;
    
    @Resource(name = "blTransactionManager")
    protected JpaTransactionManager transactionManager;

    @Override
    public SandBox retrieve(Long id) {
        return sandBoxEntityManager.find(SandBoxImpl.class, id);
    }
    
    @Override
    public List<SandBox> retrieveAllSandBoxes() {
        TypedQuery<SandBox> query = new TypedQueryBuilder<SandBox>(SandBox.class, "sandbox")
                .toQuery(sandBoxEntityManager);
        return query.getResultList();
    }

    @Override
    public List<SandBox> retrieveSandBoxesByType(SandBoxType sandboxType) {
        TypedQuery<SandBox> query = new TypedQueryBuilder<SandBox>(SandBox.class, "sandbox")
            .addRestriction("sandbox.sandboxType", "=", sandboxType.getType())
            .toQuery(sandBoxEntityManager);
        return query.getResultList();
    }

    @Override
    public SandBox retrieveUserSandBoxForParent(Long authorId, Long parentSandBoxId) {
        TypedQuery<SandBox> query = new TypedQueryBuilder<SandBox>(SandBox.class, "sandbox")
            .addRestriction("sandbox.sandboxType", "=", SandBoxType.USER.getType())
            .addRestriction("sandbox.author", "=", authorId)
            .addRestriction("sandbox.parentSandBox.id", "=", parentSandBoxId)
            .toQuery(sandBoxEntityManager);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    public SandBox retrieveSandBoxByType(SandBoxType sandboxType) {
        TypedQuery<SandBox> query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE", SandBox.class);
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
    public SandBox retrieveNamedSandBox(SandBoxType sandboxType, String sandboxName) {
        Query query = sandBoxEntityManager.createNamedQuery("BC_READ_SANDBOX_BY_TYPE_AND_NAME");
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
    @SuppressWarnings("unchecked")
    public Map<Long, String> retrieveAuthorNamesForSandBoxes(Set<Long> sandBoxIds) {
        Query query = sandBoxEntityManager.createQuery(
                "SELECT sb.id, au.name " +
                "FROM org.broadleafcommerce.common.sandbox.domain.SandBoxImpl sb, " +
                    "org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl au " +
                "WHERE sb.author = au.id " +
                "AND sb.id IN :sandBoxIds");
        query.setParameter("sandBoxIds", sandBoxIds);
        List<Object[]> results = query.getResultList();

        Map<Long, String> map = new HashMap<Long, String>();
        for (Object[] result : results) {
            map.put((Long) result[0], (String) result[1]);
        }

        return map;
    }

    @Override
    public List<SandBox> retrieveSandBoxesForAuthor(Long authorId) {
        TypedQuery<SandBox> query = new TypedQueryBuilder<SandBox>(SandBox.class, "sb")
            .addRestriction("sb.author", "=", authorId)
            .toQuery(sandBoxEntityManager);
        return query.getResultList();
    }

    @Override
    public SandBox persist(SandBox entity) {
        sandBoxEntityManager.persist(entity);
        sandBoxEntityManager.flush();
        return entity;
    }

    @Override
    public SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType) {
        TransactionStatus status = TransactionUtils.createTransaction("createSandBox",
                        TransactionDefinition.PROPAGATION_REQUIRES_NEW, transactionManager);
        try {
            SandBox approvalSandbox = retrieveNamedSandBox(sandBoxType, sandBoxName);
            if (approvalSandbox == null) {
                approvalSandbox = new SandBoxImpl();
                approvalSandbox.setName(sandBoxName);
                approvalSandbox.setSandBoxType(sandBoxType);
                approvalSandbox = persist(approvalSandbox);
            }
            TransactionUtils.finalizeTransaction(status, transactionManager, false);
            return approvalSandbox;
        } catch (Exception ex) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public SandBox createUserSandBox(Long authorId, SandBox approvalSandBox) {
        TransactionStatus status = TransactionUtils.createTransaction("createSandBox",
                        TransactionDefinition.PROPAGATION_REQUIRES_NEW, transactionManager);
        try {
            SandBox userSandBox = new SandBoxImpl();
            userSandBox.setName(approvalSandBox.getName());
            userSandBox.setAuthor(authorId);
            userSandBox.setParentSandBox(approvalSandBox);
            userSandBox.setSandBoxType(SandBoxType.USER);
            userSandBox = persist(userSandBox);

            TransactionUtils.finalizeTransaction(status, transactionManager, false);
            return userSandBox;
        } catch (Exception ex) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public SandBox createDefaultSandBox() {
        TransactionStatus status = TransactionUtils.createTransaction("createSandBox",
                        TransactionDefinition.PROPAGATION_REQUIRES_NEW, transactionManager);
        try {
            SandBox defaultSB = new SandBoxImpl();
            defaultSB.setName("Default");
            defaultSB.setSandBoxType(SandBoxType.DEFAULT);
            defaultSB.setColor("#0B9098");
            defaultSB = persist(defaultSB);

            TransactionUtils.finalizeTransaction(status, transactionManager, false);
            return defaultSB;
        } catch (Exception ex) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new RuntimeException(ex);
        }
    }

}
