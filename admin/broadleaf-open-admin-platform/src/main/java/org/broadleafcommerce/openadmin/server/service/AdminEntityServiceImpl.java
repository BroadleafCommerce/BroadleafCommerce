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

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
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
    public Map<String, Entity[]> getSubRecords(Class<?> clazz, String containingEntityId, ClassMetadata metadata) throws ServiceException, ApplicationSecurityException {
        Map<String, Entity[]> map = new HashMap<String, Entity[]>();

        for (Property p : metadata.getProperties()) {
            if (p.getMetadata() instanceof BasicCollectionMetadata) {
                BasicCollectionMetadata md = (BasicCollectionMetadata) p.getMetadata();
                try {
                    Class<?> collectionClass = Class.forName(md.getCollectionCeilingEntity());
                    ForeignKey foreignField = (ForeignKey) md.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);

                    FilterAndSortCriteria subFasc = new FilterAndSortCriteria(foreignField.getManyToField());
                    subFasc.setFilterValue(containingEntityId);

                    //Entity[] subRecords = getRecords(collectionClass, new ForeignKey[] { foreignField }, subFasc);
                    Entity[] subRecords = fetch(collectionClass, new ForeignKey[] { foreignField }, subFasc).getRecords();
                    
                    if (subRecords != null && subRecords.length > 0) {
                        map.put(p.getName(), subRecords);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return map;
    }

    @Override
    public Property[] getDisplayProperties(ClassMetadata metadata, Entity entity) {
        List<Property> returnList = new ArrayList<Property>();

        for (Property p : metadata.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata md = (BasicFieldMetadata) p.getMetadata();

                Property entityProp = entity.getPMap().get(p.getName());
                entityProp.setMetadata(md);

                returnList.add(entityProp);
            }
        }

        Property[] returnArray = new Property[returnList.size()];
        return returnList.toArray(returnArray);
    }

    @Override
    public Entity updateEntity(Entity entity, Class<?> clazz) throws ServiceException, ApplicationSecurityException {
        OperationTypes opTypes = getDefaultOperationTypes();
        PersistencePerspective perspective = getPersistencePerspective(opTypes, null);
        PersistencePackage pkg = getPersistencePackage(clazz.getName(), null, perspective);

        for (Entry<String, Property> entry : entity.getPMap().entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        Property[] pArray = new Property[entity.getPMap().values().size()];
        entity.setProperties(entity.getPMap().values().toArray(pArray));

        pkg.setEntity(entity);

        return service.update(pkg);
    }

    protected DynamicResultSet inspect(Class<?> clazz) throws ServiceException, ApplicationSecurityException {
        OperationTypes opTypes = getDefaultOperationTypes();
        PersistencePerspective perspective = getPersistencePerspective(opTypes, null);
        PersistencePackage pkg = getPersistencePackage(clazz.getName(), null, perspective);

        return service.inspect(pkg);
    }

    protected DynamicResultSet fetch(Class<?> clazz, ForeignKey[] foreignKeys, FilterAndSortCriteria... fascs) throws ServiceException, ApplicationSecurityException {
        ForeignKey[] combined = (ForeignKey[]) ArrayUtils.addAll(foreignKeys, null);

        OperationTypes opTypes = getDefaultOperationTypes();
        PersistencePerspective perspective = getPersistencePerspective(opTypes, combined);
        PersistencePackage pkg = getPersistencePackage(clazz.getName(), null, perspective);
        CriteriaTransferObject cto = getDefaultCto();
        
        if (fascs != null) {
            for (FilterAndSortCriteria fasc : fascs) {
                cto.add(fasc);
            }
        }

        return service.fetch(pkg, cto);
    }
    
    protected OperationTypes getDefaultOperationTypes() {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setFetchType(OperationType.BASIC);
        operationTypes.setRemoveType(OperationType.NONDESTRUCTIVEREMOVE);
        operationTypes.setAddType(OperationType.NONDESTRUCTIVEREMOVE);
        operationTypes.setUpdateType(OperationType.BASIC);
        operationTypes.setInspectType(OperationType.BASIC);
        return operationTypes;
    }
    
    protected PersistencePerspective getPersistencePerspective(OperationTypes types, ForeignKey[] foreignKeys) {
        PersistencePerspective persistencePerspective = new PersistencePerspective();
        persistencePerspective.setOperationTypes(types);
        persistencePerspective.setAdditionalForeignKeys(new ForeignKey[] {});
        persistencePerspective.setAdditionalNonPersistentProperties(new String[] {});
        if (foreignKeys != null) {
            for (ForeignKey fk : foreignKeys) {
                persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, fk);
            }
        }
        
        return persistencePerspective;
    }
    
    protected PersistencePackage getPersistencePackage(String className, String[] customCriteria, PersistencePerspective perspective) {
        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(className);
        pp.setFetchTypeFullyQualifiedClassname(null);
        pp.setPersistencePerspective(perspective);
        pp.setCustomCriteria(customCriteria);
        pp.setEntity(null);
        pp.setCsrfToken(null);
        return pp;
    }
    
    protected CriteriaTransferObject getDefaultCto() {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        cto.setFirstResult(0);
        cto.setMaxResults(75);
        return cto;
    }
    

}
