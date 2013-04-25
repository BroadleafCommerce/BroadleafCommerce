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

package org.broadleafcommerce.openadmin.server.factory;

import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.dto.OperationTypes;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.springframework.stereotype.Service;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blPersistencePackageFactory")
public class PersistencePackageFactoryImpl implements PersistencePackageFactory {
    
    @Override
    public PersistencePackage create(PersistencePackageRequest request) {
        PersistencePerspective persistencePerspective = new PersistencePerspective();

        persistencePerspective.setAdditionalForeignKeys(request.getAdditionalForeignKeys());
        persistencePerspective.setAdditionalNonPersistentProperties(new String[] {});
        
        if (request.getForeignKey() != null) {
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, 
                    request.getForeignKey());
        }

        switch (request.getType()) {
            case STANDARD:
                persistencePerspective.setOperationTypes(getDefaultOperationTypes());
                break;

            case ADORNED:
                if (request.getAdornedList() == null) {
                    throw new IllegalArgumentException("ADORNED type requires the adornedList to be set");
                }

                persistencePerspective.setOperationTypes(getOperationTypes(OperationType.ADORNEDTARGETLIST));
                persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.ADORNEDTARGETLIST,
                        request.getAdornedList());
                break;

            case MAP:
                if (request.getMapStructure() == null) {
                    throw new IllegalArgumentException("MAP type requires the mapStructure to be set");
                }

                persistencePerspective.setOperationTypes(getOperationTypes(OperationType.MAP));
                persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE,
                        request.getMapStructure());
                break;
        }

        if (request.getOperationTypesOverride() != null) {
            persistencePerspective.setOperationTypes(request.getOperationTypesOverride());
        }

        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(request.getCeilingEntityClassname());
        pp.setFetchTypeFullyQualifiedClassname(null);
        pp.setPersistencePerspective(persistencePerspective);
        pp.setCustomCriteria(request.getCustomCriteria());
        pp.setCsrfToken(null);

        if (request.getEntity() != null) {
            pp.setEntity(request.getEntity());
        }

        return pp;
    }

    protected OperationTypes getDefaultOperationTypes() {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setFetchType(OperationType.BASIC);
        operationTypes.setRemoveType(OperationType.BASIC);
        operationTypes.setAddType(OperationType.BASIC);
        operationTypes.setUpdateType(OperationType.BASIC);
        operationTypes.setInspectType(OperationType.BASIC);
        return operationTypes;
    }
    
    protected OperationTypes getOperationTypes(OperationType nonInspectOperationType) {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setFetchType(nonInspectOperationType);
        operationTypes.setRemoveType(nonInspectOperationType);
        operationTypes.setAddType(nonInspectOperationType);
        operationTypes.setUpdateType(nonInspectOperationType);
        operationTypes.setInspectType(OperationType.BASIC);
        return operationTypes;
    }

}
