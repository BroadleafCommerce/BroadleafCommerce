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
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.server.factory.PersistencePackageFactory;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

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
    public ClassMetadata getClassMetadata(String className) throws ServiceException, ApplicationSecurityException {
        ClassMetadata cmd = inspect(className, null, null).getClassMetaData();
        cmd.setCeilingType(className);
        return cmd;
    }
    
    @Override
    public ClassMetadata getClassMetadata(String className, AdornedTargetList adornedList) throws ServiceException, ApplicationSecurityException {
        ClassMetadata cmd = inspect(className, adornedList).getClassMetaData();
        cmd.setCeilingType(className);
        return cmd;
    }

    @Override
    public ClassMetadata getClassMetadata(String className, ForeignKey[] foreignKeys, String configKey) throws ServiceException, ApplicationSecurityException {
        ClassMetadata cmd = inspect(className, foreignKeys, configKey).getClassMetaData();
        cmd.setCeilingType(className);
        return cmd;
    }

    @Override
    public Entity[] getRecords(String className, ForeignKey[] foreignKeys, FilterAndSortCriteria... fascs)
            throws ServiceException, ApplicationSecurityException {
        return fetch(className, foreignKeys, null, fascs).getRecords();
    }

    @Override
    public Entity getRecord(String className, String id) throws ServiceException, ApplicationSecurityException {
        FilterAndSortCriteria fasc = new FilterAndSortCriteria("id");
        fasc.setFilterValue(id);
        
        Entity[] entities = fetch(className, new ForeignKey[] {}, null, fasc).getRecords();
        
        if (entities == null || entities.length > 1) {
            throw new RuntimeException("More than one entity found with the same id");
        }

        Entity entity = entities[0];
        return entity;
    }

    @Override
    public Entity[] getRecordsForCollection(final ClassMetadata containingClassMetadata, final String containingEntityId,
            final Property collectionProperty)
            throws ServiceException, ApplicationSecurityException {
        final Entity[][] recordContainer = new Entity[1][];

        collectionProperty.getMetadata().accept(new MetadataVisitor() {

            @Override
            public void visit(MapMetadata fmd) {
                // TODO Auto-generated method stub

            }

            @Override
            public void visit(AdornedTargetCollectionMetadata fmd) {
                try {
                    AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                            .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);

                    String manyToField = adornedList.getCollectionFieldName();
                    FilterAndSortCriteria fasc = new FilterAndSortCriteria(manyToField);
                    fasc.setFilterValue(containingEntityId);

                    recordContainer[0] = fetch(fmd.getCollectionCeilingEntity(), adornedList, fasc).getRecords();
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public void visit(BasicCollectionMetadata fmd) {
                try {
                    // Establish the filter criteria for the subcollection -- we want to get all results for the 
                    // subcollection for the current containing entity id
                    ForeignKey foreignField = (ForeignKey) fmd.getPersistencePerspective().getPersistencePerspectiveItems()
                            .get(PersistencePerspectiveItemType.FOREIGNKEY);

                    FilterAndSortCriteria fasc = new FilterAndSortCriteria(foreignField.getManyToField());
                    fasc.setFilterValue(containingEntityId);

                    recordContainer[0] = fetch(fmd.getCollectionCeilingEntity(),
                            new ForeignKey[] { foreignField }, null, fasc).getRecords();
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public void visit(BasicFieldMetadata fmd) {
                throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was not a " +
                        "collection field.", collectionProperty.getName(), containingClassMetadata.getCeilingType()));
            }
        });

        return recordContainer[0];
    }

    @Override
    public Map<String, Entity[]> getRecordsForAllSubCollections(final String containingClassName,
            final String containingEntityId)
            throws ServiceException, ApplicationSecurityException {
        final Map<String, Entity[]> map = new HashMap<String, Entity[]>();

        final ClassMetadata cmd = getClassMetadata(containingClassName);
        for (final Property p : cmd.getProperties()) {
            p.getMetadata().accept(new MetadataVisitor() {

                @Override
                public void visit(BasicCollectionMetadata fmd) {
                    try {
                        Entity[] rows = getRecordsForCollection(cmd, containingEntityId, p);
                        map.put(p.getName(), rows);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void visit(AdornedTargetCollectionMetadata fmd) {
                    try {
                        Entity[] rows = getRecordsForCollection(cmd, containingEntityId, p);
                        map.put(p.getName(), rows);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void visit(MapMetadata fmd) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void visit(BasicFieldMetadata fmd) {
                    // TODO Auto-generated method stub
                }
            });

        }

        return map;
    }

    @Override
    public Entity addSubCollectionEntity(EntityForm entityForm, String className, String fieldName, final String parentId)
            throws ServiceException, ApplicationSecurityException, ClassNotFoundException {
        // Find the FieldMetadata for this collection field
        ClassMetadata cmd = getClassMetadata(className);

        final List<Property> properties = new ArrayList<Property>();
        // Build the property array from the field map. Note that we leave 1 extra slot for the foreign key reference
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            properties.add(p);
        }

        final Entity entity = new Entity();

        final List<ForeignKey> additionalForeignKeys = new ArrayList<ForeignKey>();

        final AdornedTargetList[] adornedListContainer = new AdornedTargetList[1];

        for (Property p : cmd.getProperties()) {
            if (p.getName().equals(fieldName)) {
                p.getMetadata().accept(new MetadataVisitor() {

                    @Override
                    public void visit(MapMetadata fmd) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void visit(AdornedTargetCollectionMetadata fmd) {
                        AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                                .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
                        entity.setType(new String[] { adornedList.getAdornedTargetEntityClassname() });
                        adornedListContainer[0] = adornedList;
                    }

                    @Override
                    public void visit(BasicCollectionMetadata fmd) {
                        ForeignKey foreignField = null;
                        if (fmd != null) {
                            foreignField = (ForeignKey) fmd.getPersistencePerspective().getPersistencePerspectiveItems()
                                    .get(PersistencePerspectiveItemType.FOREIGNKEY);
                            Property p = new Property();
                            p.setName(foreignField.getManyToField());
                            p.setValue(parentId);
                            properties.add(p);
                        }

                        additionalForeignKeys.add(foreignField);

                        entity.setType(new String[] { fmd.getCollectionCeilingEntity() });
                    }

                    @Override
                    public void visit(BasicFieldMetadata fmd) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        entity.setProperties(propArr);

        ForeignKey[] additionalKeys = new ForeignKey[additionalForeignKeys.size()];
        additionalKeys = additionalForeignKeys.toArray(additionalKeys);

        if (adornedListContainer[0] != null) {
            return add(entity, entity.getType()[0], null, adornedListContainer[0]);
        } else {
            return add(entity, entity.getType()[0], additionalKeys, null);
        }
    }

    @Override
    public Entity updateEntity(EntityForm entityForm, String className)
            throws ServiceException, ApplicationSecurityException {
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

        return update(entity, className, null, null);
    }

    /**
     * Executes a database add for the given entity and class
     * 
     * @param entity
     * @param clazz
     * @return the updated entity
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    protected Entity add(Entity entity, String className, ForeignKey[] foreignKeys, String configKey)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.standard(className, null, foreignKeys, configKey);
        pkg.setEntity(entity);
        return service.add(pkg);
    }

    protected Entity add(Entity entity, String className, String[] customCriteria, AdornedTargetList adornedList)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.adornedTarget(className, customCriteria, adornedList);
        pkg.setEntity(entity);
        return service.add(pkg);
    }

    /**
     * Executes a database update for the given entity and class
     * 
     * @param entity
     * @param clazz
     * @return the updated entity
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    protected Entity update(Entity entity, String className, ForeignKey[] foreignKeys, String configKey)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.standard(className, null, foreignKeys, configKey);
        pkg.setEntity(entity);
        return service.update(pkg);
    }

    /**
     * Executes a database inspect for the given class
     * 
     * @param className the fully qualified name of the class to use
     * @param foreignKeys any foreign keys applicable to this perspective
     * @param configKey any configuration key to consider with this persistence perspective
     * @return the DynamicResultSet (note that this will not have any entities, only metadata)
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    protected DynamicResultSet inspect(String className, ForeignKey[] foreignKeys, String configKey)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.standard(className, null, foreignKeys, configKey);
        return service.inspect(pkg);
    }

    protected DynamicResultSet inspect(String className, AdornedTargetList adornedList)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.adornedTarget(className, null, adornedList);
        return service.inspect(pkg);
    }

    /**
     * Executes a database fetch for the given class, foreign keys, and any applicable filter and sort criteria
     * 
     * @param clazz
     * @param foreignKeys
     * @param fascs
     * @return the DynamicResultSet (note that this will not have any metadata, only entities)
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    protected DynamicResultSet fetch(String className, ForeignKey[] foreignKeys, String configKey,
            FilterAndSortCriteria... fascs)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.standard(className, null, foreignKeys, configKey);
        CriteriaTransferObject cto = getDefaultCto();
        
        if (fascs != null) {
            for (FilterAndSortCriteria fasc : fascs) {
                cto.add(fasc);
            }
        }

        return service.fetch(pkg, cto);
    }
    
    protected DynamicResultSet fetch(String className, AdornedTargetList adornedList, FilterAndSortCriteria... fascs)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.adornedTarget(className, null, adornedList);
        CriteriaTransferObject cto = getDefaultCto();

        if (fascs != null) {
            for (FilterAndSortCriteria fasc : fascs) {
                cto.add(fasc);
            }
        }

        return service.fetch(pkg, cto);
    }

    /**
     * @return a default CriteriaTransferObject set up to fetch a maximum of 75 results
     */
    protected CriteriaTransferObject getDefaultCto() {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        cto.setFirstResult(0);
        cto.setMaxResults(75);
        return cto;
    }

}


