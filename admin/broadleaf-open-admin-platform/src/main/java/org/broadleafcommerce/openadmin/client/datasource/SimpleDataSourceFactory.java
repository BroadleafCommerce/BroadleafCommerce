/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.client.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

/**
 * 
 * @author bpolster
 *
 */
public class SimpleDataSourceFactory implements DataSourceFactory {

    private String entityClassName;

    public SimpleDataSourceFactory(String entityClassName) {
           this.entityClassName = entityClassName;
    }

    public PersistencePerspective setupPersistencePerspective(PersistencePerspective persistencePerspective) {
          // Place holder
        return persistencePerspective;
    }
    
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        PersistencePerspective persistencePerspective = createPersistencePerspective();
        DataSourceModule[] modules = createDataSourceModules().toArray(new DataSourceModule[0]);
        ListGridDataSource dataSource = new ListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
        dataSource.buildFields(null, false, cb);
    }

    public PersistencePerspective createPersistencePerspective() {
        PersistencePerspective persistencePerspective = new PersistencePerspective();
        return setupPersistencePerspective(persistencePerspective);
    }

    public List<DataSourceModule> createDataSourceModules() {
        List<DataSourceModule> dsModuleList = new ArrayList<DataSourceModule>();
        dsModuleList.add(new BasicClientEntityModule(entityClassName, createPersistencePerspective(), AppServices.DYNAMIC_ENTITY));
        return dsModuleList;
    }

}
