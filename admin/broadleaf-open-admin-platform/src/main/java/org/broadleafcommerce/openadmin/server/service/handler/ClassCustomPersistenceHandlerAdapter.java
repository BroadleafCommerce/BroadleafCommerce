/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.handler;

import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience passthrough for {@link CustomPersistenceHandlerAdapter} that provides a method for class detection
 * based on the provided constructor.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class ClassCustomPersistenceHandlerAdapter extends CustomPersistenceHandlerAdapter {
    
    List<Class<?>> handledClasses = new ArrayList<Class<?>>();
    
    public ClassCustomPersistenceHandlerAdapter(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            handledClasses.add(clazz);
        }
    }
    
    protected boolean classMatches(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        for (Class<?> clazz : handledClasses) {
            if (clazz.getName().equals(ceilingEntityFullyQualifiedClassname)) {
                return true;
            }
        }

        return false;
    }
    
    protected boolean isMapOperation(PersistencePackage persistencePackage) {
        return persistencePackage.getPersistencePerspective().getOperationTypes().getAddType().equals(OperationType.MAP);
    }

}
