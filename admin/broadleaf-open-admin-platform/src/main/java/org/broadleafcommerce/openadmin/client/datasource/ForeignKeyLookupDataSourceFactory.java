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

package org.broadleafcommerce.openadmin.client.datasource;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import java.util.ArrayList;
import java.util.List;

/**
 * This factory is responsible for generating a datasource from a ForeignKey instance. It is primarily
 * used to generate the datasource that drives the search grid for ManyToOne and OneToOne lookups.
 *
 * @author Jeff Fischer
 */
public class ForeignKeyLookupDataSourceFactory implements DataSourceFactory {

    private ForeignKey foreignKey;
    private String[] customCriteria;
    private Boolean useServerSideInspectionCache;

    public ForeignKeyLookupDataSourceFactory(ForeignKey foreignKey, String[] customCriteria, Boolean useServerSideInspectionCache) {
        this.foreignKey = foreignKey;
        this.customCriteria = customCriteria;
        this.useServerSideInspectionCache = useServerSideInspectionCache;
    }

    @Override
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        final PersistencePerspective persistencePerspective = new PersistencePerspective();
        if (useServerSideInspectionCache != null) {
            persistencePerspective.setUseServerSideInspectionCache(useServerSideInspectionCache);
        }
        final List<DataSourceModule> dataSourceModuleList = new ArrayList<DataSourceModule>();
        dataSourceModuleList.add(new BasicClientEntityModule(foreignKey.getForeignKeyClass(), persistencePerspective, AppServices.DYNAMIC_ENTITY));

        DataSourceModule[] modules = new DataSourceModule[dataSourceModuleList.size()];
        modules = dataSourceModuleList.toArray(modules);

        CustomCriteriaListGridDataSource dataSource = new CustomCriteriaListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
        if (customCriteria != null && customCriteria.length > 0) {
            dataSource.setUseForAdd(true);
            dataSource.setUseForFetch(true);
            dataSource.setUseForInspect(true);
            dataSource.setUseForRemove(true);
            dataSource.setUseForUpdate(true);
            dataSource.setCustomCriteria(customCriteria);
        }
        dataSource.buildFields(null, false, cb);
    }
}
