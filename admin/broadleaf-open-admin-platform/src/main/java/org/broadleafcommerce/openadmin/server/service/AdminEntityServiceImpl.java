/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.factory.PersistencePackageFactory;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blAdminEntityService")
public class AdminEntityServiceImpl implements AdminEntityService {

    @Resource(name = "blDynamicEntityRemoteService")
    protected DynamicEntityService service;

    @Resource(name = "blPersistencePackageFactory")
    protected PersistencePackageFactory persistencePackageFactory;

    @Override
    public ClassMetadata getClassMetadata(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        ClassMetadata cmd = inspect(request).getClassMetaData();
        cmd.setCeilingType(request.getClassName());
        return cmd;
    }

    @Override
    public Entity[] getRecords(PersistencePackageRequest request) throws ServiceException, ApplicationSecurityException {
        return fetch(request).getRecords();
    }

    @Override
    public Entity getRecord(PersistencePackageRequest request, String id) throws ServiceException, ApplicationSecurityException {
        FilterAndSortCriteria fasc = new FilterAndSortCriteria("id");
        fasc.setFilterValue(id);
        request.addFilterAndSortCriteria(fasc);

        Entity[] entities = fetch(request).getRecords();

        Assert.isTrue(entities != null && entities.length == 1);

        Entity entity = entities[0];
        return entity;
    }

    @Override
    public Entity addEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria);
        return add(ppr);
    }

    @Override
    @Transactional("blTransactionManager")
    public Entity updateEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria);
        
        Entity returnEntity = update(ppr);
        
        // If the entity form has dynamic forms inside of it, we need to persist those as well.
        // They are typically done in their own custom persistence handlers, which will get triggered
        // based on the criteria specific in the PersistencePackage.
        for (Entry<String, EntityForm> entry : entityForm.getDynamicForms().entrySet()) {
            DynamicEntityFormInfo info = entityForm.getDynamicFormInfo(entry.getKey());
            
            customCriteria = new String[] { info.getCriteriaName(), entityForm.getId() };
            ppr = getRequestForEntityForm(entry.getValue(), customCriteria);
            update(ppr);
        }
        
        return returnEntity;
    }

    @Override
    public void removeEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria);
        remove(ppr);
    }

    protected PersistencePackageRequest getRequestForEntityForm(EntityForm entityForm, String[] customCriteria) {
        // Ensure the ID property is on the form
        Field idField = entityForm.findField("id");
        if (idField == null) {
            idField = new Field();
            idField.setName("id");
            idField.setValue(entityForm.getId());
            entityForm.getFields().put("id", idField);
        }
        
        // Build the property array from the field map
        Property[] properties = new Property[entityForm.getFields().size()];
        int i = 0;
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            properties[i++] = p;
        }

        Entity entity = new Entity();
        entity.setProperties(properties);
        entity.setType(new String[] { entityForm.getEntityType() });

        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withEntity(entity)
                .withCustomCriteria(customCriteria)
                .withClassName(entityForm.getEntityType());

        return ppr;
    }

    @Override
    public Entity getAdvancedCollectionRecord(ClassMetadata containingClassMetadata, String containingEntityId,
            Property collectionProperty, String collectionItemId)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(collectionProperty.getMetadata());

        FieldMetadata md = collectionProperty.getMetadata();

        Entity entity = null;

        if (md instanceof AdornedTargetCollectionMetadata) {
            FilterAndSortCriteria fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName());
            fasc.setFilterValue(containingEntityId);
            ppr.addFilterAndSortCriteria(fasc);

            fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName() + "Target");
            fasc.setFilterValue(collectionItemId);
            ppr.addFilterAndSortCriteria(fasc);

            Entity[] entities = fetch(ppr).getRecords();
            Assert.isTrue(entities != null && entities.length == 1);
            entity = entities[0];
        } else if (md instanceof MapMetadata) {
            FilterAndSortCriteria fasc = new FilterAndSortCriteria(ppr.getForeignKey().getManyToField());
            fasc.setFilterValue(containingEntityId);
            ppr.addFilterAndSortCriteria(fasc);

            Entity[] entities = fetch(ppr).getRecords();
            for (Entity e : entities) {
                Property p = e.getPMap().get("id");
                if (p.getValue().equals(collectionItemId)) {
                    entity = e;
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was not an " +
                    "advanced collection field.", collectionProperty.getName(), containingClassMetadata.getCeilingType()));
        }

        if (entity == null) {
            throw new NoResultException(String.format("Could not find record for class [%s], field [%s], main entity id " +
                    "[%s], collection entity id [%s]", containingClassMetadata.getCeilingType(),
                    collectionProperty.getName(), containingEntityId, collectionItemId));
        }

        return entity;
    }

    @Override
    public Entity[] getRecordsForCollection(ClassMetadata containingClassMetadata, String containingEntityId,
            Property collectionProperty, FilterAndSortCriteria[] criteria)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(collectionProperty.getMetadata())
                .withFilterAndSortCriteria(criteria);
        FilterAndSortCriteria fasc;

        FieldMetadata md = collectionProperty.getMetadata();

        if (md instanceof BasicCollectionMetadata) {
            fasc = new FilterAndSortCriteria(ppr.getForeignKey().getManyToField());
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName());
        } else if (md instanceof MapMetadata) {
            fasc = new FilterAndSortCriteria(ppr.getForeignKey().getManyToField());
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was not a " +
                    "collection field.", collectionProperty.getName(), containingClassMetadata.getCeilingType()));
        }

        fasc.setFilterValue(containingEntityId);
        ppr.addFilterAndSortCriteria(fasc);

        return fetch(ppr).getRecords();
    }

    @Override
    public Map<String, Entity[]> getRecordsForAllSubCollections(PersistencePackageRequest ppr, String containingEntityId)
            throws ServiceException, ApplicationSecurityException {
        Map<String, Entity[]> map = new HashMap<String, Entity[]>();

        ClassMetadata cmd = getClassMetadata(ppr);
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof CollectionMetadata) {
                Entity[] rows = getRecordsForCollection(cmd, containingEntityId, p, null);

                //TODO APA Figure out where else to do this
                //String collectionClass = ((CollectionMetadata) p.getMetadata()).getCollectionCeilingEntity();
                //ClassMetadata collectionMd = getClassMetadata(collectionClass);

                map.put(p.getName(), rows);
            }
        }

        return map;
    }

    @Override
    public Entity addSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field, String parentId)
            throws ServiceException, ApplicationSecurityException, ClassNotFoundException {
        // Assemble the properties from the entity form
        List<Property> properties = new ArrayList<Property>();
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            properties.add(p);
        }

        FieldMetadata md = field.getMetadata();

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md)
                .withEntity(new Entity());

        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;
            ppr.getEntity().setType(new String[] { fmd.getCollectionCeilingEntity() });

            Property fp = new Property();
            fp.setName(ppr.getForeignKey().getManyToField());
            fp.setValue(parentId);
            properties.add(fp);
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            ppr.getEntity().setType(new String[] { ppr.getAdornedList().getAdornedTargetEntityClassname() });
        } else if (md instanceof MapMetadata) {
            ppr.getEntity().setType(new String[] { entityForm.getEntityType() });
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was" +
                    " not a collection field.", field.getName(), mainMetadata.getCeilingType()));
        }

        ppr.setClassName(ppr.getEntity().getType()[0]);

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        return add(ppr);
    }

    @Override
    public Entity updateSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            String parentId, String collectionItemId)
            throws ServiceException, ApplicationSecurityException, ClassNotFoundException {
        // Assemble the properties from the entity form
        List<Property> properties = new ArrayList<Property>();
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            properties.add(p);
        }

        FieldMetadata md = field.getMetadata();

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md)
                .withEntity(new Entity());

        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;
            ppr.getEntity().setType(new String[] { fmd.getCollectionCeilingEntity() });

            Property fp = new Property();
            fp.setName(ppr.getForeignKey().getManyToField());
            fp.setValue(parentId);
            properties.add(fp);
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            ppr.getEntity().setType(new String[] { ppr.getAdornedList().getAdornedTargetEntityClassname() });
        } else if (md instanceof MapMetadata) {
            ppr.getEntity().setType(new String[] { entityForm.getEntityType() });
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was" +
                    " not a collection field.", field.getName(), mainMetadata.getCeilingType()));
        }

        ppr.setClassName(ppr.getEntity().getType()[0]);

        Property p = new Property();
        p.setName("id");
        p.setValue(collectionItemId);
        properties.add(p);

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        return update(ppr);
    }

    @Override
    public void removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, String parentId, String itemId,
            String priorKey)
            throws ServiceException, ApplicationSecurityException {
        List<Property> properties = new ArrayList<Property>();

        Property p;

        Entity entity = new Entity();
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(field.getMetadata())
                .withEntity(entity);

        if (field.getMetadata() instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) field.getMetadata();

            p = new Property();
            p.setName("id");
            p.setValue(itemId);
            properties.add(p);

            p = new Property();
            p.setName(ppr.getForeignKey().getManyToField());
            p.setValue(parentId);
            properties.add(p);

            entity.setType(new String[] { fmd.getCollectionCeilingEntity() });
        } else if (field.getMetadata() instanceof AdornedTargetCollectionMetadata) {
            AdornedTargetList adornedList = ppr.getAdornedList();

            p = new Property();
            p.setName(adornedList.getLinkedObjectPath() + "." + adornedList.getLinkedIdProperty());
            p.setValue(parentId);
            properties.add(p);

            p = new Property();
            p.setName(adornedList.getTargetObjectPath() + "." + adornedList.getTargetIdProperty());
            p.setValue(itemId);
            properties.add(p);

            entity.setType(new String[] { adornedList.getAdornedTargetEntityClassname() });
        } else if (field.getMetadata() instanceof MapMetadata) {
            MapMetadata fmd = (MapMetadata) field.getMetadata();

            p = new Property();
            p.setName("symbolicId");
            p.setValue(parentId);
            properties.add(p);

            p = new Property();
            p.setName("priorKey");
            p.setValue(priorKey);
            properties.add(p);

            entity.setType(new String[] { fmd.getTargetClass() });
        }

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        remove(ppr);
    }

    protected Entity add(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.add(pkg);
    }

    protected Entity update(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.update(pkg);
    }

    protected DynamicResultSet inspect(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.inspect(pkg);
    }

    protected void remove(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        service.remove(pkg);
    }

    protected DynamicResultSet fetch(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);

        CriteriaTransferObject cto = getDefaultCto();
        if (request.getFilterAndSortCriteria() != null) {
            cto.addAll(request.getFilterAndSortCriteria());
        }

        return service.fetch(pkg, cto);
    }

    protected CriteriaTransferObject getDefaultCto() {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        cto.setFirstResult(0);
        cto.setMaxResults(75);
        return cto;
    }

}