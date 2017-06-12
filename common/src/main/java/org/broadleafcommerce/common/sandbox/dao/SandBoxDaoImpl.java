/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.sandbox.dao;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxManagement;
import org.broadleafcommerce.common.sandbox.domain.SandBoxManagementImpl;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("blSandBoxDao")
public class SandBoxDaoImpl implements SandBoxDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager sandBoxEntityManager;

    @Resource(name = "blTransactionManager")
    protected JpaTransactionManager transactionManager;

    @Override
    public SandBox retrieve(Long id) {
        //Need to not create a query through SandBoxManagement here. Otherwise, a Hibernate exception can occur
        //(i.e. org.hibernate.HibernateException: Found two representations of same collection: org.broadleafcommerce.core.catalog.domain.ProductImpl.additionalSkus
        //when saving a change to product).
        return sandBoxEntityManager.find(SandBoxImpl.class, id);
    }
    
    @Override
    public List<SandBox> retrieveAllSandBoxes() {
        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        criteria.where(
                builder.and(builder.or(
                        builder.isNotNull(sandbox.get("sandBox").get("name")),
                        builder.notEqual(sandbox.get("sandBox").get("name").as(String.class), "")),
                builder.or(
                        builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                        builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y')))
        );
        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public List<SandBox> retrieveSandBoxesByType(SandBoxType sandboxType) {
        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        criteria.where(
                builder.and(builder.equal(sandbox.get("sandBox").get("sandboxType"), sandboxType.getType()),
                        builder.or(
                                builder.isNotNull(sandbox.get("sandBox").get("name")),
                                builder.notEqual(sandbox.get("sandBox").get("name").as(String.class), "")),
                        builder.or(
                                builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                                builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y')))
        );
        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    @Deprecated
    public List<SandBox> retrieveAllUserSandBoxes(Long authorId) {
        TypedQuery<SandBox> query = new TypedQueryBuilder<SandBox>(SandBox.class, "sb")
            .addRestriction("sb.author", "=", authorId)
            .addRestriction("sb.sandboxType", "=", SandBoxType.USER.getType())
            .addRestriction("sb.archiveStatus.archived", "==", null)
            .addRestriction("sb.archiveStatus.archived", "!=", "Y")
            .addRestriction("sb.name", "!=", "")
            .toQuery(sandBoxEntityManager);
        return query.getResultList();
    }

    @Override
    public SandBox merge(SandBox userSandBox) {
        SandBox response = sandBoxEntityManager.merge(userSandBox);
        sandBoxEntityManager.flush();
        return response;
    }

    @Override
    public List<SandBox> retrieveChildSandBoxesByParentId(Long parentSandBoxId) {
        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        criteria.where(
                builder.and(sandbox.get("sandBox").get("parentSandBox").in(parentSandBoxId),
                        builder.or(builder.isNotNull(sandbox.get("sandBox").get("name")),
                                builder.notEqual(sandbox.get("sandBox").get("name").as(String.class), "")),
                        builder.or(builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                                builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y')))
        );

        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);

        return query.getResultList();
    }

    @Override
    public SandBox retrieveUserSandBoxForParent(Long authorId, Long parentSandBoxId) {
        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(sandbox.get("sandBox").get("sandboxType"), SandBoxType.USER.getType()));
        restrictions.add(builder.or(builder.equal(sandbox.get("sandBox").get("author"), authorId), builder.isNull(sandbox.get("sandBox").get("author"))));
        restrictions.add(builder.equal(sandbox.get("sandBox").get("parentSandBox").get("id"), parentSandBoxId));
        restrictions.add(
                builder.or(
                        builder.isNotNull(sandbox.get("sandBox").get("name")),
                        builder.notEqual(sandbox.get("sandBox").get("name").as(String.class), ""))
        );
        restrictions.add(
                builder.or(
                        builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                        builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y'))
        );
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);

        List<SandBox> results = query.getResultList();

        SandBox response;
        if (results == null || results.size() == 0) {
            response = null;
        } else {
            response = results.get(0);
        }
        return response;
    }
    
    public SandBox retrieveSandBoxManagementById(Long sandBoxId) {
        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        criteria.where(
                builder.and(builder.equal(sandbox.get("sandBox").get("id"), sandBoxId),
                        builder.or(builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                                builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y')))
        );
        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);

        List<SandBox> results = query.getResultList();

        if (results == null || results.size() == 0) {
            return null;
        }
        return results.get(0);

    }

    @Override
    public SandBox retrieveNamedSandBox(SandBoxType sandboxType, String sandboxName) {
        return retrieveNamedSandBox(sandboxType, sandboxName, null);
    }

    @Override
    public SandBox retrieveNamedSandBox(SandBoxType sandBoxType, String sandboxName, Long authorId) {
        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(sandbox.get("sandBox").get("sandboxType"), sandBoxType.getType()));
        restrictions.add(builder.equal(sandbox.get("sandBox").get("name"), sandboxName));
        if (authorId != null) {
            restrictions.add(builder.equal(sandbox.get("sandBox").get("author"), authorId));
        }
        restrictions.add(
                builder.or(
                        builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                        builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y'))
        );
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);
        if (query.getResultList() != null && query.getResultList().size() == 1) {
            return query.getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public Map<Long, String> retrieveAuthorNamesForSandBoxes(Set<Long> sandBoxIds) {
        Query query = sandBoxEntityManager.createQuery(
                "SELECT sb.sandBox.id, au.name " +
                "FROM org.broadleafcommerce.common.sandbox.domain.SandBoxManagementImpl sb, " +
                    "org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl au " +
                "WHERE sb.sandBox.author = au.id " +
                "AND sb.sandBox.id IN :sandBoxIds " +
                "AND (sb.sandBox.archiveStatus.archived IS NULL OR sb.sandBox.archiveStatus.archived = 'N')");
        query.setParameter("sandBoxIds", sandBoxIds);
        List<Object[]> results = query.getResultList();

        Map<Long, String> map = new HashMap<Long, String>();
        for (Object[] result : results) {
            map.put((Long) result[0], (String) result[1]);
        }

        return map;
    }

    @Override
    public Map<Long, String> retrieveSandboxNamesForSandBoxes(Set<Long> sandBoxIds) {

        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        criteria.where(
                builder.and(builder.in(sandbox.get("sandBox").get("id")).value(sandBoxIds),
                builder.or(builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                        builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y')))
        );
        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);
        List<SandBox> results = query.getResultList();

        Map<Long, String> map = new HashMap<Long, String>();
        for (SandBox result : results) {
            map.put(result.getId(), result.getName());
        }

        return map;
    }

    @Override
    public List<SandBox> retrieveSandBoxesForAuthor(Long authorId) {
        return retrieveSandBoxesForAuthor(authorId, null);
    }

    @Override
    public List<SandBox> retrieveSandBoxesForAuthor(Long authorId, SandBoxType sandBoxType) {
        CriteriaBuilder builder = sandBoxEntityManager.getCriteriaBuilder();
        CriteriaQuery<SandBox> criteria = builder.createQuery(SandBox.class);
        Root<SandBoxManagementImpl> sandbox = criteria.from(SandBoxManagementImpl.class);
        criteria.select(sandbox.get("sandBox").as(SandBox.class));
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(sandbox.get("sandBox").get("author"), authorId));
        restrictions.add(
                builder.or(
                        builder.isNotNull(sandbox.get("sandBox").get("name")),
                        builder.notEqual(sandbox.get("sandBox").get("name").as(String.class), ""))
        );
        if (sandBoxType != null) {
            restrictions.add(builder.equal(sandbox.get("sandBox").get("sandboxType"), sandBoxType.getType()));
        }
        restrictions.add(
                builder.or(builder.isNull(sandbox.get("sandBox").get("archiveStatus").get("archived").as(String.class)),
                        builder.notEqual(sandbox.get("sandBox").get("archiveStatus").get("archived").as(Character.class), 'Y'))
        );
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        TypedQuery<SandBox> query = sandBoxEntityManager.createQuery(criteria);
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

                SandBoxManagement mgmt = new SandBoxManagementImpl();
                mgmt.setSandBox(approvalSandbox);
                sandBoxEntityManager.persist(mgmt);
                sandBoxEntityManager.flush();
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

            SandBoxManagement mgmt = new SandBoxManagementImpl();
            mgmt.setSandBox(userSandBox);
            sandBoxEntityManager.persist(mgmt);
            sandBoxEntityManager.flush();

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
            defaultSB.setColor("#20C0F0");
            defaultSB = persist(defaultSB);

            SandBoxManagement mgmt = new SandBoxManagementImpl();
            mgmt.setSandBox(defaultSB);
            sandBoxEntityManager.persist(mgmt);
            sandBoxEntityManager.flush();

            TransactionUtils.finalizeTransaction(status, transactionManager, false);
            return defaultSB;
        } catch (Exception ex) {
            TransactionUtils.finalizeTransaction(status, transactionManager, true);
            throw new RuntimeException(ex);
        }
    }

}
