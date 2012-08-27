/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.datasource.user;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.datasource.CeilingEntities;
import org.broadleafcommerce.openadmin.client.datasource.SimpleDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.common.presentation.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

/**
 * 
 * @author bpolster
 *
 */
public class AdminCreatePermissionListDataSourceFactory extends SimpleDataSourceFactory {

    public AdminCreatePermissionListDataSourceFactory() {
        // For non-BLC this would be super("AdminPermissionImpl")
        super(CeilingEntities.ADMIN_PERMISSION);
    }

    public PersistencePerspective setupPersistencePerspective(PersistencePerspective persistencePerspective) {
        persistencePerspective.setOperationTypes(new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY));
        return persistencePerspective;
    }
    
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        PersistencePerspective persistencePerspective = createPersistencePerspective();
        DataSourceModule[] modules = createDataSourceModules().toArray(new DataSourceModule[0]);
        CustomCriteriaListGridDataSource dataSource = new CustomCriteriaListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules, true, true, true, true, true);
        dataSource.setCustomCriteria(new String[]{"createNewPermission"});
        dataSource.buildFields(null, false, cb);
    }
}
