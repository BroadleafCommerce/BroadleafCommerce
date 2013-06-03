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

package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.factory.PersistencePackageFactory;
import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
            throws ServiceException {
        ClassMetadata cmd = inspect(request).getClassMetaData();
        cmd.setCeilingType(request.getCeilingEntityClassname());
        return cmd;
    }

    @Override
    public DynamicResultSet getRecords(PersistencePackageRequest request) throws ServiceException {
        return fetch(request);
    }

    @Override
    public Entity getRecord(PersistencePackageRequest request, String id, ClassMetadata cmd)
            throws ServiceException {
        String idProperty = getIdProperty(cmd);
        
        FilterAndSortCriteria fasc = new FilterAndSortCriteria(idProperty);
        fasc.setFilterValue(id);
        request.addFilterAndSortCriteria(fasc);

        Entity[] entities = fetch(request).getRecords();

        Assert.isTrue(entities != null && entities.length == 1, "Entity not found");

        Entity entity = entities[0];
        return entity;
    }

    @Override
    public Entity addEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria);
        return add(ppr);
    }

    @Override
    @Transactional("blTransactionManager")
    public Entity updateEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException {
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
            throws ServiceException {
        PersistencePackageRequest ppr = getRequestForEntityForm(entityForm, customCriteria);
        remove(ppr);
    }
    
    protected List<Property> getPropertiesFromEntityForm(EntityForm entityForm) {
        List<Property> properties = new ArrayList<Property>(entityForm.getFields().size());
        
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(JSCompatibilityHelper.unEncodeFieldname(entry.getKey()));
            p.setValue(entry.getValue().getValue());
            properties.add(p);
        }
        
        return properties;
    }

    protected PersistencePackageRequest getRequestForEntityForm(EntityForm entityForm, String[] customCriteria) {
        // Ensure the ID property is on the form
        Field idField = entityForm.findField(entityForm.getIdProperty());
        if (idField == null) {
            idField = new Field();
            idField.setName(entityForm.getIdProperty());
            idField.setValue(entityForm.getId());
            entityForm.getFields().put(entityForm.getIdProperty(), idField);
        }
        
        List<Property> propList = getPropertiesFromEntityForm(entityForm);
        Property[] properties = new Property[propList.size()];
        properties = propList.toArray(properties);

        Entity entity = new Entity();
        entity.setProperties(properties);
        entity.setType(new String[] { entityForm.getEntityType() });

        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withEntity(entity)
                .withCustomCriteria(customCriteria)
                .withCeilingEntityClassname(entityForm.getCeilingEntityClassname());

        return ppr;
    }

    @Override
    public Entity getAdvancedCollectionRecord(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, String collectionItemId)
            throws ServiceException {
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(collectionProperty.getMetadata());

        FieldMetadata md = collectionProperty.getMetadata();
        String containingEntityId = getContextSpecificRelationshipId(containingClassMetadata, containingEntity, 
                collectionProperty.getName());

        Entity entity = null;

        if (md instanceof AdornedTargetCollectionMetadata) {
            FilterAndSortCriteria fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName());
            fasc.setFilterValue(containingEntityId);
            ppr.addFilterAndSortCriteria(fasc);

            fasc = new FilterAndSortCriteria(ppr.getAdornedList().getCollectionFieldName() + "Target");
            fasc.setFilterValue(collectionItemId);
            ppr.addFilterAndSortCriteria(fasc);

            Entity[] entities = fetch(ppr).getRecords();
            Assert.isTrue(entities != null && entities.length == 1, "Entity not found");
            entity = entities[0];
        } else if (md instanceof MapMetadata) {
            MapMetadata mmd = (MapMetadata) md;
            FilterAndSortCriteria fasc = new FilterAndSortCriteria(ppr.getForeignKey().getManyToField());
            fasc.setFilterValue(containingEntityId);
            ppr.addFilterAndSortCriteria(fasc);

            Entity[] entities = fetch(ppr).getRecords();
            for (Entity e : entities) {
                String idProperty = getIdProperty(containingClassMetadata);
                if (mmd.isSimpleValue()) {
                    idProperty = "key";
                }
                Property p = e.getPMap().get(idProperty);
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
    public DynamicResultSet getRecordsForCollection(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, FilterAndSortCriteria[] fascs, Integer startIndex, Integer maxIndex)
            throws ServiceException {
        
        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(collectionProperty.getMetadata())
                .withFilterAndSortCriteria(fascs)
                .withStartIndex(startIndex)
                .withMaxIndex(maxIndex);
        
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

        String id = getContextSpecificRelationshipId(containingClassMetadata, containingEntity, collectionProperty.getName());
        fasc.setFilterValue(id);
        ppr.addFilterAndSortCriteria(fasc);

        return fetch(ppr);
    }

    @Override
    public Map<String, DynamicResultSet> getRecordsForAllSubCollections(PersistencePackageRequest ppr, Entity containingEntity) 
            throws ServiceException {
        Map<String, DynamicResultSet> map = new HashMap<String, DynamicResultSet>();

        ClassMetadata cmd = getClassMetadata(ppr);
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof CollectionMetadata) {
                DynamicResultSet drs = getRecordsForCollection(cmd, containingEntity, p, null, null, null);
                map.put(p.getName(), drs);
            }
        }

        return map;
    }

    @Override
    public Entity addSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field, 
            Entity parentEntity)
            throws ServiceException, ClassNotFoundException {
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
            fp.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(fp);
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            ppr.getEntity().setType(new String[] { ppr.getAdornedList().getAdornedTargetEntityClassname() });
        } else if (md instanceof MapMetadata) {
            ppr.getEntity().setType(new String[] { entityForm.getEntityType() });
            
            Property p = new Property();
            p.setName("symbolicId");
            p.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(p);
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was" +
                    " not a collection field.", field.getName(), mainMetadata.getCeilingType()));
        }

        ppr.setCeilingEntityClassname(ppr.getEntity().getType()[0]);

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        return add(ppr);
    }

    @Override
    public Entity updateSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            Entity parentEntity, String collectionItemId)
            throws ServiceException, ClassNotFoundException {
        List<Property> properties = getPropertiesFromEntityForm(entityForm);

        FieldMetadata md = field.getMetadata();

        PersistencePackageRequest ppr = PersistencePackageRequest.fromMetadata(md)
                .withEntity(new Entity());

        if (md instanceof BasicCollectionMetadata) {
            BasicCollectionMetadata fmd = (BasicCollectionMetadata) md;
            ppr.getEntity().setType(new String[] { fmd.getCollectionCeilingEntity() });

            Property fp = new Property();
            fp.setName(ppr.getForeignKey().getManyToField());
            fp.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(fp);
        } else if (md instanceof AdornedTargetCollectionMetadata) {
            ppr.getEntity().setType(new String[] { ppr.getAdornedList().getAdornedTargetEntityClassname() });
        } else if (md instanceof MapMetadata) {
            ppr.getEntity().setType(new String[] { entityForm.getEntityType() });
            
            Property p = new Property();
            p.setName("symbolicId");
            p.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(p);
        } else {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was" +
                    " not a collection field.", field.getName(), mainMetadata.getCeilingType()));
        }

        ppr.setCeilingEntityClassname(ppr.getEntity().getType()[0]);

        Property p = new Property();
        p.setName(entityForm.getIdProperty());
        p.setValue(collectionItemId);
        properties.add(p);

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        return update(ppr);
    }

    @Override
    public void removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, Entity parentEntity, String itemId,
            String priorKey)
            throws ServiceException {
        List<Property> properties = new ArrayList<Property>();

        Property p;
        String parentId = getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName());

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
            p.setValue(getContextSpecificRelationshipId(mainMetadata, parentEntity, field.getName()));
            properties.add(p);

            p = new Property();
            p.setName("priorKey");
            p.setValue(priorKey);
            properties.add(p);
            
            MapStructure mapStructure = ppr.getMapStructure();
            
            p = new Property();
            p.setName(mapStructure.getKeyPropertyName());
            p.setValue(itemId);
            properties.add(p);

            entity.setType(new String[] { fmd.getTargetClass() });
        }

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        ppr.getEntity().setProperties(propArr);

        remove(ppr);
    }

    @Override
    public String getContextSpecificRelationshipId(ClassMetadata cmd, Entity entity, String propertyName) {
        String prefix;
        if (propertyName.contains(".")) {
            prefix = propertyName.substring(0, propertyName.lastIndexOf("."));
        } else {
            prefix = "";
        }
                
        if (prefix.equals("")) {
            return entity.findProperty("id").getValue();
        } else {
            //we need to check all the parts of the prefix. For example, the prefix could include an @Embedded class like
            //defaultSku.dimension. In this case, we want the id from the defaultSku property, since the @Embedded does
            //not have an id property - nor should it.
            String[] prefixParts = prefix.split("\\.");
            for (int j = 0; j < prefixParts.length; j++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < prefixParts.length - j; x++) {
                    sb.append(prefixParts[x]);
                    if (x < prefixParts.length - j - 1) {
                        sb.append(".");
                    }
                }
                String tempPrefix = sb.toString();
                
                for (Property property : entity.getProperties()) {
                    if (property.getName().startsWith(tempPrefix)) {
                        BasicFieldMetadata md = (BasicFieldMetadata) cmd.getPMap().get(property.getName()).getMetadata();
                        if (md.getFieldType().equals(SupportedFieldType.ID)) {
                            return property.getValue();
                        }
                    }
                }
            }
        }
        if (!prefix.contains(".")) {
            //this may be an embedded class directly on the root entity (e.g. embeddablePriceList.restrictedPriceLists on OfferImpl)
            return entity.findProperty("id").getValue();
        }
        throw new RuntimeException("Unable to establish a relationship id");
    }
    
    @Override
    public String getIdProperty(ClassMetadata cmd) throws ServiceException {
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fmd = (BasicFieldMetadata) p.getMetadata();
                //check for ID type and also make sure the field we're looking at is not a "ToOne" association
                if (SupportedFieldType.ID.equals(fmd.getFieldType()) && !p.getName().contains(".")) {
                    return p.getName();
                }
            }
        }
        
        throw new ServiceException("Could not determine ID field for " + cmd.getCeilingType());
    }

    protected Entity add(PersistencePackageRequest request)
            throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.add(pkg);
    }

    protected Entity update(PersistencePackageRequest request)
            throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.update(pkg);
    }

    protected DynamicResultSet inspect(PersistencePackageRequest request)
            throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.inspect(pkg);
    }

    protected void remove(PersistencePackageRequest request)
            throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        service.remove(pkg);
    }

    protected DynamicResultSet fetch(PersistencePackageRequest request)
            throws ServiceException {
        PersistencePackage pkg = persistencePackageFactory.create(request);

        CriteriaTransferObject cto = getDefaultCto();
        if (request.getFilterAndSortCriteria() != null) {
            cto.addAll(Arrays.asList(request.getFilterAndSortCriteria()));
        }
        
        if (request.getStartIndex() == null) {
            cto.setFirstResult(0);
        } else {
            cto.setFirstResult(request.getStartIndex());
        }
        
        if (request.getMaxIndex() != null) {
            int requestedMaxResults = request.getMaxIndex() - request.getStartIndex() + 1;
            if (requestedMaxResults >= 0 && requestedMaxResults < cto.getMaxResults()) {
                cto.setMaxResults(requestedMaxResults);
            }
        }
        
        return service.fetch(pkg, cto);
    }
    
    protected CriteriaTransferObject getDefaultCto() {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        cto.setMaxResults(50);
        return cto;
    }

}