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

package org.broadleafcommerce.cms.admin.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.cms.page.domain.PageItemCriteria;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Jeff Fischer
 */
@Component("blPageItemCriteriaCustomPersistenceHandler")
public class PageItemCriteriaCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private final Log LOG = LogFactory.getLog(PageItemCriteriaCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return PageItemCriteria.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleAdd(persistencePackage);
    }

    protected void removeHtmlEncoding(Entity entity) {
        Property prop = entity.findProperty("orderItemMatchRule");
        if (prop != null && prop.getValue() != null) {
            //antisamy XSS protection encodes the values in the MVEL
            //reverse this behavior
            prop.setValue(prop.getRawValue());
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
        removeHtmlEncoding(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            PageItemCriteria adminInstance = (PageItemCriteria) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(PageItemCriteria.class.getName(), persistencePerspective);
            adminInstance = (PageItemCriteria) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            if (adminInstance.getPage().getLockedFlag()) {
                throw new IllegalArgumentException("Unable to update a locked record");
            }
            adminInstance = (PageItemCriteria) dynamicEntityDao.merge(adminInstance);
            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            return adminEntity;
        } catch (Exception e) {
            throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        removeHtmlEncoding(entity);
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(PageItemCriteria.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            PageItemCriteria adminInstance = (PageItemCriteria) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            if (adminInstance.getPage().getLockedFlag()) {
                /*
                This may be an attempt to delete a target item criteria off an otherwise un-edited, production StructuredContent instance
                 */
                CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
                CriteriaQuery<Page> query = criteriaBuilder.createQuery(Page.class);
                Root<PageImpl> root = query.from(PageImpl.class);
                query.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("archivedFlag"), Boolean.FALSE), criteriaBuilder.equal(root.get("originalPageId"), adminInstance.getPage().getId())));
                query.select(root);
                TypedQuery<Page> scQuery = dynamicEntityDao.getStandardEntityManager().createQuery(query);
                try {
                    checkCriteria: {
                        Page myContent = scQuery.getSingleResult();
                        for (PageItemCriteria itemCriteria : myContent.getQualifyingItemCriteria()) {
                            if (itemCriteria.getMatchRule().equals(adminInstance.getMatchRule()) && itemCriteria.getQuantity().equals(adminInstance.getQuantity())) {
                                //manually set the values - otherwise unwanted properties will be set
                                itemCriteria.setMatchRule(entity.findProperty("orderItemMatchRule").getValue());
                                itemCriteria.setQuantity(Integer.parseInt(entity.findProperty("quantity").getValue()));
                                adminInstance = itemCriteria;
                                break checkCriteria;
                            }
                        }
                        throw new RuntimeException("Unable to find an item criteria to update");
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to update a locked record");
                }
            } else {
                adminInstance = (PageItemCriteria) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            }
            adminInstance = (PageItemCriteria) dynamicEntityDao.merge(adminInstance);
            Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

            return adminEntity;
        } catch (Exception e) {
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(PageItemCriteria.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            PageItemCriteria adminInstance = (PageItemCriteria) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            if (adminInstance.getPage().getLockedFlag()) {
                /*
                This may be an attempt to delete a target item criteria off an otherwise un-edited, production StructuredContent instance
                 */
                CriteriaBuilder criteriaBuilder = dynamicEntityDao.getStandardEntityManager().getCriteriaBuilder();
                CriteriaQuery<Page> query = criteriaBuilder.createQuery(Page.class);
                Root<PageImpl> root = query.from(PageImpl.class);
                query.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("archivedFlag"), Boolean.FALSE), criteriaBuilder.equal(root.get("originalPageId"), adminInstance.getPage().getId())));
                query.select(root);
                TypedQuery<Page> scQuery = dynamicEntityDao.getStandardEntityManager().createQuery(query);
                try {
                    Page myContent = scQuery.getSingleResult();
                    for (PageItemCriteria itemCriteria : myContent.getQualifyingItemCriteria()) {
                        if (itemCriteria.getMatchRule().equals(adminInstance.getMatchRule()) && itemCriteria.getQuantity().equals(adminInstance.getQuantity())) {
                            myContent.getQualifyingItemCriteria().remove(itemCriteria);
                            return;
                        }
                    }
                    throw new RuntimeException("Unable to find an item criteria to delete");
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to update a locked record");
                }
            }

            dynamicEntityDao.remove(adminInstance);
        } catch (Exception e) {
            throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
        }
    }
}
