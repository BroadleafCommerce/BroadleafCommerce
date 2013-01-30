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
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.HashMap;
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
    
    @Override
    public ClassMetadata getClassMetadata(Class<?> clazz) throws ServiceException, ApplicationSecurityException {
        return inspect(clazz).getClassMetaData();
    }
    
    @Override
    public Entity[] getRecords(Class<?> clazz, FilterAndSortCriteria... fascs) throws ServiceException, ApplicationSecurityException {
        return fetch(clazz, null, fascs).getRecords();
    }

    @Override
    public Entity getRecord(Class<?> clazz, String id) throws ServiceException, ApplicationSecurityException {
        FilterAndSortCriteria fasc = new FilterAndSortCriteria("id");
        fasc.setFilterValue(id);
        
        Entity[] entities = fetch(clazz, null, fasc).getRecords();
        
        if (entities == null || entities.length > 1) {
            throw new RuntimeException("More than one entity found with the same id");
        }

        Entity entity = entities[0];
        return entity;
    }

    @Override
    public Entity[] getRecordsForCollection(Class<?> containingClass, String containingEntityId, String collectionField)
            throws ServiceException, ApplicationSecurityException {
        // Get the collection property and assert it's a valid collection
        ClassMetadata cmd = getClassMetadata(containingClass);
        Property prop = cmd.getPMap().get(collectionField);
        if (!(prop.getMetadata() instanceof BasicCollectionMetadata)) {
            throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was not a " +
                    "collection field.", collectionField, containingClass.getName()));
        }
        BasicCollectionMetadata fmd = (BasicCollectionMetadata) prop.getMetadata();

        try {
            // Establish the filter criteria for the subcollection -- we want to get all results for the subcollection
            // for the current containing entity id
            Class<?> collectionClass = Class.forName(fmd.getCollectionCeilingEntity());
            ForeignKey foreignField = (ForeignKey) fmd.getPersistencePerspective().getPersistencePerspectiveItems()
                    .get(PersistencePerspectiveItemType.FOREIGNKEY);

            FilterAndSortCriteria fasc = new FilterAndSortCriteria(foreignField.getManyToField());
            fasc.setFilterValue(containingEntityId);

            Entity[] subRecords = fetch(collectionClass, new ForeignKey[] { foreignField }, fasc).getRecords();
            return subRecords;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Map<String, Entity[]> getRecordsForAllSubCollections(Class<?> containingClass, String containingEntityId)
            throws ServiceException, ApplicationSecurityException {
        Map<String, Entity[]> map = new HashMap<String, Entity[]>();

        ClassMetadata cmd = getClassMetadata(containingClass);
        for (Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof BasicCollectionMetadata) {
                Entity[] rows = getRecordsForCollection(containingClass, containingEntityId, p.getName());
                map.put(p.getName(), rows);
            }
        }

        return map;
    }

    @Override
    public Entity updateEntity(EntityForm entityForm, Class<?> clazz) throws ServiceException, ApplicationSecurityException {
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

        return update(entity, clazz);
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
    protected Entity update(Entity entity, Class<?> clazz) throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = getPersistencePackage(clazz.getName(), null, null);
        pkg.setEntity(entity);
        return service.update(pkg);
    }

    /**
     * Executes a database inspect for the given class
     * 
     * @param clazz
     * @return the DynamicResultSet (note that this will not have any entities, only metadata)
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    protected DynamicResultSet inspect(Class<?> clazz) throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = getPersistencePackage(clazz.getName(), null, null);
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
    protected DynamicResultSet fetch(Class<?> clazz, ForeignKey[] foreignKeys, FilterAndSortCriteria... fascs)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = getPersistencePackage(clazz.getName(), null, foreignKeys);
        CriteriaTransferObject cto = getDefaultCto();
        
        if (fascs != null) {
            for (FilterAndSortCriteria fasc : fascs) {
                cto.add(fasc);
            }
        }

        return service.fetch(pkg, cto);
    }
    
    /**
     * @return an OperationTypes object with OperationType.BASIC set for all operations
     */
    protected OperationTypes getDefaultOperationTypes() {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setFetchType(OperationType.BASIC);
        operationTypes.setRemoveType(OperationType.BASIC);
        operationTypes.setAddType(OperationType.BASIC);
        operationTypes.setUpdateType(OperationType.BASIC);
        operationTypes.setInspectType(OperationType.BASIC);
        return operationTypes;
    }
    
    /**
     * @param foreignKeys keys to add to the PersistencePerspective PersistencePerspectiveItems
     * @return a PersistencePerspective configured with the default operation types and specified foreign keys
     */
    protected PersistencePerspective getPersistencePerspective(ForeignKey[] foreignKeys) {
        PersistencePerspective persistencePerspective = new PersistencePerspective();
        persistencePerspective.setOperationTypes(getDefaultOperationTypes());
        persistencePerspective.setAdditionalForeignKeys(new ForeignKey[] {});
        persistencePerspective.setAdditionalNonPersistentProperties(new String[] {});
        if (foreignKeys != null) {
            for (ForeignKey fk : foreignKeys) {
                persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, fk);
            }
        }
        
        return persistencePerspective;
    }
    
    /**
     * Assembles a persistence package for the specified attributes
     * 
     * @param className the fully qualified name of the class to use
     * @param customCriteria any customCriteria to pass to the persistence handlers
     * @param keys any foreign keys to consider for the persistence perspective
     * @return the assembled persistence package
     */
    protected PersistencePackage getPersistencePackage(String className, String[] customCriteria, ForeignKey[] keys) {
        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(className);
        pp.setFetchTypeFullyQualifiedClassname(null);
        pp.setPersistencePerspective(getPersistencePerspective(keys));
        pp.setCustomCriteria(customCriteria);
        pp.setEntity(null);
        pp.setCsrfToken(null);
        return pp;
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
