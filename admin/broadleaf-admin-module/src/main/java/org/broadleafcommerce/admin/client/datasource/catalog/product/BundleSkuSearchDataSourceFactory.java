/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.admin.client.datasource.catalog.product;

import org.broadleafcommerce.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.admin.client.datasource.catalog.product.module.SkuBasicClientEntityModule;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.datasource.DataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

/**
 * 
 * @author Phillip Verheyden
 *
 */
public class BundleSkuSearchDataSourceFactory implements DataSourceFactory {

    protected CustomCriteriaListGridDataSource dataSource = null;
    
    @Override
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        operationTypes = new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC);
        PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
        persistencePerspective.setUseServerSideInspectionCache(false);
        persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey("product", EntityImplementations.PRODUCT, null));
        DataSourceModule[] modules = new DataSourceModule[]{
            new SkuBasicClientEntityModule(CeilingEntities.SKU, persistencePerspective, AppServices.DYNAMIC_ENTITY),
        };
        
        /**
         * Not declared as static because this needs to be recreated every time the page is loaded, since
         * the actual fields for the DataSource can change based on adding/removing Product Options
         */
        ListGridDataSource dataSource = new ListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
        dataSource.buildFields(null, false, cb);
    }

}