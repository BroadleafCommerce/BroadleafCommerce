package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.*;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blSandBoxItemDao")
public class SandBoxItemDaoImpl implements SandBoxItemDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public SandBoxItem retrieveById(Long id) {
       return em.find(SandBoxItemImpl.class, id);
    }

    @Override
    public SandBoxItem retrieveBySandboxAndOriginalItemId(SandBox sandBox, SandBoxItemType type, Long originalItemId) {
        Query query = em.createNamedQuery("BC_READ_SANDBOX_ITEM_BY_ORIG_ITEM_ID");
        query.setParameter("sandbox", sandBox);
        query.setParameter("itemType", type);
        query.setParameter("originalItemId", originalItemId);
        List<SandBoxItem> items = query.getResultList();
        return items == null || items.isEmpty() ? null : items.get(0);
    }

    @Override
    public SandBoxItem retrieveBySandboxAndTemporaryItemId(SandBox sandBox, SandBoxItemType type, Long tempItemId) {
        Query query = em.createNamedQuery("BC_READ_SANDBOX_ITEM_BY_TEMP_ITEM_ID");
        query.setParameter("sandbox", sandBox);
        query.setParameter("itemType", type);
        query.setParameter("temporaryItemId", tempItemId);
        List<SandBoxItem> items = query.getResultList();
        return items == null || items.isEmpty() ? null : items.get(0);
    }

    @Override
    public SandBoxItem addSandBoxItem(SandBox sandBox, SandBoxOperationType operationType, SandBoxItemType itemType, String description, Long temporaryId, Long originalId) {
        SandBoxItemImpl sandBoxItem = new SandBoxItemImpl();
        sandBoxItem.setSandBoxOperationType(operationType);
        sandBoxItem.setSandBox(sandBox);
        sandBoxItem.setArchivedFlag(false);
        //sandBoxItem.setLastUpdateDate(Calendar.getInstance().getTime());
        sandBoxItem.setDescription(description);
        sandBoxItem.setOriginalItemId(originalId);
        sandBoxItem.setTemporaryItemId(temporaryId);
        sandBoxItem.setSandBoxItemType(itemType);

        SandBoxAction action = new SandBoxActionImpl();
        //action.setActionDate(sandBoxItem.getLastUpdateDate());
        action.setActionType(SandBoxActionType.EDIT);
        //action.setUser(sandBoxItem.getCreatedBy());

        sandBoxItem.addSandBoxAction(action);
        action.addSandBoxItem(sandBoxItem);

        return em.merge(sandBoxItem);
    }

    @Override
    public SandBoxItem updateSandBoxItem(SandBoxItem sandBoxItem) {
        //sandBoxItem.setLastUpdateDate(SystemTime.asDate());
        return em.merge(sandBoxItem);
    }
}
