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

import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author bpolster
 *
 */
public class SimpleDataSourceFactory implements DataSourceFactory {

    private String entityClassName;
	private String configurationKey;
	public  ListGridDataSource dataSource = null;

	/**
	 * Creates the SimpleDataSourceFactory which allows a DataSource to be setup 
	 * representing an entity in the system.   Typically, callers would pass in the
	 * cieling entity (e.g. 
	 * 
	 * @param entityClassName
	 */
    public SimpleDataSourceFactory(String entityClassName) {
           this.entityClassName = entityClassName;
    }
    
	/**
	 * Allows the DataSourceFactory to be constructed using the className and 
	 * configuration context.  
	 * 
	 * The configurationContext works along with the  "blMetadataOverrides" component
	 * to allow some fields from the entity to be suppressed.  
	 * 
	 * @param entityClassName
	 * @param configurationContext
	 */
    public SimpleDataSourceFactory(String entityClassName, String configurationKey) {
        this(entityClassName);
        this.configurationKey = configurationKey;
    }

    public PersistencePerspective setupPersistencePerspective(PersistencePerspective persistencePerspective) {
          // Place holder
        return persistencePerspective;
    }
	
	public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
		if (dataSource == null) {
			PersistencePerspective persistencePerspective = createPersistencePerspective(operationTypes);			
			DataSourceModule[] modules = createDataSourceModules().toArray(new DataSourceModule[0]);
			ListGridDataSource dataSource = new ListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
			dataSource.buildFields(null, false, cb);
		} else {
			if (cb != null) {
				cb.onSuccess(dataSource);
			}
		}
	}
	
	public PersistencePerspective createPersistencePerspective() {
		return createPersistencePerspective(new OperationTypes());
	}

    public PersistencePerspective createPersistencePerspective(OperationTypes operationTypes) {
        PersistencePerspective persistencePerspective = new PersistencePerspective();
		if (operationTypes != null) {
			persistencePerspective.setOperationTypes(operationTypes);
		}			
		if (configurationKey != null) {
			persistencePerspective.setConfigurationKey(configurationKey);
		}
        return setupPersistencePerspective(persistencePerspective);
    }

    public List<DataSourceModule> createDataSourceModules() {
        List<DataSourceModule> dsModuleList = new ArrayList<DataSourceModule>();
        dsModuleList.add(new BasicClientEntityModule(entityClassName, createPersistencePerspective(null), AppServices.DYNAMIC_ENTITY));
        return dsModuleList;
    }

}
