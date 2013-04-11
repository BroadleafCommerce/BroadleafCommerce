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

package org.broadleafcommerce.admin.client.datasource.catalog.product;

import org.broadleafcommerce.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.openadmin.client.datasource.DataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.AdornedTargetListClientModule;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryUpSaleProductListDataSourceFactory implements DataSourceFactory {

    public static final String symbolName = "upSaleProducts";
    public static final String linkedObjectPath = "category";
    public static final String linkedIdProperty = "id";
    public static final String targetObjectPath = "relatedSaleProduct";
    public static final String targetIdProperty = "id";
    public static final String sortField = "sequence";
    public static ListGridDataSource dataSource = null;
    
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        if (dataSource == null) {
            operationTypes = new OperationTypes(OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.BASIC);
            PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.ADORNEDTARGETLIST, new AdornedTargetList(symbolName, linkedObjectPath, linkedIdProperty, targetObjectPath, targetIdProperty, EntityImplementations.UPSALEPRODUCT, sortField, true));
            DataSourceModule[] modules = new DataSourceModule[]{
                new BasicClientEntityModule(CeilingEntities.PRODUCT, persistencePerspective, AppServices.DYNAMIC_ENTITY),
                new AdornedTargetListClientModule(CeilingEntities.PRODUCT, persistencePerspective, AppServices.DYNAMIC_ENTITY)
            };
            dataSource = new ListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
            dataSource.buildFields(null, false, cb);
        } else {
            if (cb != null) {
                cb.onSuccess(dataSource);
            }
        }
    }

}
