/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.openadmin.client.datasource.dynamic.module;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.Record;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.JoinStructure;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * 
 * @author jfischer
 *
 */
public class JoinStructureClientModule extends BasicClientEntityModule {
    
    /**
     * @param ceilingEntityFullyQualifiedClassname
     * @param persistencePerspective
     * @param service
     */
    public JoinStructureClientModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
    }

    @Override
    public CriteriaTransferObject getCto(DSRequest request) {
        JoinStructure joinTable = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
        CriteriaTransferObject cto = super.getCto(request);
        if (joinTable.getSortField() != null) {
            FilterAndSortCriteria sortCriteria = cto.get(joinTable.getSortField());
            sortCriteria.setSortAscending(joinTable.getSortAscending()!=null?joinTable.getSortAscending():true);
        }
        return cto;
    }
    
    @Override
    public boolean isCompatible(OperationType operationType) {
        return OperationType.JOINSTRUCTURE.equals(operationType);
    }
    
    @Override
    public Entity buildEntity(Record record, DSRequest request) {
        JoinStructure joinTable = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
        Entity entity = super.buildEntity(record, request);
        //JoinStructure joinTable = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
        entity.setType(new String[]{joinTable.getJoinStructureEntityClassname()});
        List<Property> properties = new ArrayList<Property>();
        {
            Property property = new Property();
            property.setName(joinTable.getLinkedObjectPath() + "." + joinTable.getLinkedIdProperty());
            property.setValue(dataSource.stripDuplicateAllowSpecialCharacters(getLinkedValue()));
            properties.add(property);
        }
        {
            Property property = new Property();
            property.setName(joinTable.getTargetObjectPath() + "." + joinTable.getTargetIdProperty());
            String id = dataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(record));
            if (id == null || id.equals("")) {
                id = dataSource.stripDuplicateAllowSpecialCharacters(record.getAttribute("backup_id"));
            }
            property.setValue(id);
            properties.add(property);
        }
        if (joinTable.getSortField() != null) {
            Property property = new Property();
            property.setName(joinTable.getSortField());
            property.setValue(record.getAttribute(joinTable.getSortField()));
            properties.add(property);
        }
        
        Property[] props = new Property[properties.size() + entity.getProperties().length];
        for (int j=0;j<properties.size();j++){
            props[j] = properties.get(j);
        }
        int count = properties.size();
        for (int j = 0; j<entity.getProperties().length; j++){
            props[count] = entity.getProperties()[j];
            count++;
        }
        entity.setProperties(props);
        
        return entity;
    }
}
