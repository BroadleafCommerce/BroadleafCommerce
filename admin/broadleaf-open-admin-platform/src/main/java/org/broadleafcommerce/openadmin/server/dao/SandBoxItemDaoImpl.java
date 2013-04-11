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

package org.broadleafcommerce.openadmin.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.openadmin.server.domain.SandBoxAction;
import org.broadleafcommerce.openadmin.server.domain.SandBoxActionImpl;
import org.broadleafcommerce.openadmin.server.domain.SandBoxActionType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemImpl;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxOperationType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blSandBoxItemDao")
public class SandBoxItemDaoImpl implements SandBoxItemDao {
    private static final Log LOG = LogFactory.getLog(SandBoxItemDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blTransactionManager")
    protected JpaTransactionManager manager;

    @Override
    public SandBoxItem retrieveById(Long id) {
       return em.find(SandBoxItemImpl.class, id);
    }

    @Override
    public SandBoxItem retrieveBySandboxAndTemporaryItemId(Long sandBoxId, SandBoxItemType type, Long tempItemId) {
        if (sandBoxId != null) {
            Query query = em.createNamedQuery("BC_READ_SANDBOX_ITEM_BY_TEMP_ITEM_ID");
            query.setParameter("sandboxId", sandBoxId);
            query.setParameter("itemType", type.getType());
            query.setParameter("temporaryItemId", tempItemId);
            List<SandBoxItem> items = query.getResultList();
            return items == null || items.isEmpty() ? null : items.get(0);
        } else {
            Query query = em.createNamedQuery("BC_READ_SANDBOX_ITEM_BY_TEMP_ITEM_ID_AND_PROD_SANDBOX");
            query.setParameter("itemType", type.getType());
            query.setParameter("temporaryItemId", tempItemId);
            List<SandBoxItem> items = query.getResultList();
            return items == null || items.isEmpty() ? null : items.get(0);
        }
    }

    @Override
    public List<SandBoxItem> retrieveByGroupName(Long sandBoxId, String groupName) {
        Query query = em.createNamedQuery("BC_READ_SANDBOX_ITEM_BY_GROUP_NAME");
        query.setParameter("sandboxId", sandBoxId);
        query.setParameter("groupName", groupName);

        return query.getResultList();
    }

    @Override
    public SandBoxItem addSandBoxItem(Long sbox, SandBoxOperationType operationType, SandBoxItemType itemType, String description, Long temporaryId, Long originalId) {
        return addSandBoxItem(sbox, operationType, itemType, description, itemType.getFriendlyType(), temporaryId, originalId);
    }

    @Override
    public SandBoxItem addSandBoxItem(Long sbox, SandBoxOperationType operationType, SandBoxItemType itemType, String description, String groupDescription, Long temporaryId, Long originalId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding sandbox item.  " + originalId);
        }
        SandBoxItemImpl sandBoxItem = new SandBoxItemImpl();
        sandBoxItem.setSandBoxOperationType(operationType);
        sandBoxItem.setSandBoxId(sbox);
        sandBoxItem.setArchivedFlag(false);
        sandBoxItem.setDescription(description);
        sandBoxItem.setOriginalItemId(originalId);
        sandBoxItem.setTemporaryItemId(temporaryId);
        sandBoxItem.setSandBoxItemType(itemType);
        sandBoxItem.setGroupDescription(groupDescription);

        SandBoxAction action = new SandBoxActionImpl();
        action.setActionType(SandBoxActionType.EDIT);

        sandBoxItem.addSandBoxAction(action);
        action.addSandBoxItem(sandBoxItem);

        return em.merge(sandBoxItem);
    }

    @Override
    public SandBoxItem updateSandBoxItem(SandBoxItem sandBoxItem) {
        //sandBoxItem.setLastUpdateDate(SystemTime.asDate());
        return em.merge(sandBoxItem);
    }

    @Override
    public List<SandBoxItem> retrieveSandBoxItemsForSandbox(Long sandBox) {
        Query query = em.createNamedQuery("BC_READ_ALL_SANDBOX_ITEMS");
        query.setParameter("sandboxId", sandBox);
        return query.getResultList();
    }

    @Override
    public List<SandBoxItem> retrieveSandBoxItemsByTypeForSandbox(Long sandBox, SandBoxItemType itemType) {
        Query query = em.createNamedQuery("BC_READ_ALL_SANDBOX_ITEMS_BY_TYPE");
        query.setParameter("sandboxId", sandBox);
        query.setParameter("sandBoxItemType", itemType.getType());
        return query.getResultList();
    }

    @Override
    public List<SandBoxItem> retrieveSandBoxItemsByTypesForSandbox(Long sandBox, List<SandBoxItemType> sandBoxItemTypes) {
        Query query = em.createNamedQuery("BC_READ_ALL_SANDBOX_ITEMS_BY_TYPES");
        query.setParameter("sandboxId", sandBox);
        query.setParameter("sandBoxItemTypes", sandBoxItemTypes);
        return query.getResultList();
    }

    @Override
    public void delete(SandBoxItem sandBoxItem) {
        if (!em.contains(sandBoxItem)) {
            sandBoxItem = retrieveById(sandBoxItem.getId());
        }
        em.remove(sandBoxItem);
    }
}
