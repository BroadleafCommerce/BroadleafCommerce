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

package org.broadleafcommerce.openadmin.client.datasource.user;

import org.broadleafcommerce.openadmin.client.datasource.CeilingEntities;
import org.broadleafcommerce.openadmin.client.datasource.EntityImplementations;
import org.broadleafcommerce.openadmin.client.datasource.SimpleDataSourceFactory;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.common.presentation.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.common.presentation.PersistencePerspectiveItemType;

/**
 * 
 * @author bpolster
 *
 */
public class AdminPermissionListDataSourceFactory extends SimpleDataSourceFactory {

	public static final String foreignKeyName = "allRoles";

    public AdminPermissionListDataSourceFactory() {
        // For non-BLC this would be super("AdminPermissionImpl")
        super(CeilingEntities.ADMIN_PERMISSION);
    }

    public PersistencePerspective setupPersistencePerspective(PersistencePerspective persistencePerspective) {
        persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey(foreignKeyName, EntityImplementations.ADMIN_ROLE, null));
        persistencePerspective.setOperationTypes(new OperationTypes(OperationType.ENTITY, OperationType.FOREIGNKEY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY));
        return persistencePerspective;
    }
}
