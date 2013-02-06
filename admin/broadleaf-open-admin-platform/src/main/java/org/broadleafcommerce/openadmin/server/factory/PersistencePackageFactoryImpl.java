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

package org.broadleafcommerce.openadmin.server.factory;

import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.springframework.stereotype.Service;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blPersistencePackageFactory")
public class PersistencePackageFactoryImpl implements PersistencePackageFactory {
    
    @Override
    public PersistencePackage standard(String className, String[] customCriteria, ForeignKey[] foreignKeys) {
        PersistencePerspective persistencePerspective = new PersistencePerspective();
        persistencePerspective.setOperationTypes(getDefaultOperationTypes());
        persistencePerspective.setAdditionalForeignKeys(new ForeignKey[] {});
        persistencePerspective.setAdditionalNonPersistentProperties(new String[] {});
        if (foreignKeys != null) {
            for (ForeignKey fk : foreignKeys) {
                persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, fk);
            }
        }

        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(className);
        pp.setFetchTypeFullyQualifiedClassname(null);
        pp.setPersistencePerspective(persistencePerspective);
        pp.setCustomCriteria(customCriteria);
        pp.setEntity(null);
        pp.setCsrfToken(null);
        return pp;
    }

    @Override
    public PersistencePackage adornedTarget(String className, String[] customCriteria, AdornedTargetList adornedList) {
        PersistencePerspective persistencePerspective = new PersistencePerspective();
        persistencePerspective.setOperationTypes(getOperationTypes(OperationType.ADORNEDTARGETLIST));
        persistencePerspective.setAdditionalForeignKeys(new ForeignKey[] {});
        persistencePerspective.setAdditionalNonPersistentProperties(new String[] {});
        persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.ADORNEDTARGETLIST, adornedList);

        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(className);
        pp.setFetchTypeFullyQualifiedClassname(null);
        pp.setPersistencePerspective(persistencePerspective);
        pp.setCustomCriteria(customCriteria);
        pp.setEntity(null);
        pp.setCsrfToken(null);
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
