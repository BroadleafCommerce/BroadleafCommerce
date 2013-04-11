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

package org.broadleafcommerce.admin.client.datasource.order;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.admin.client.presenter.order.OrderPresenter;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.datasource.DataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.MapStructureClientModule;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.client.service.AppServices;

/**
 * 
 * @author jfischer
 *
 */
public class PaymentAdditionalAttributesDataSourceFactory implements DataSourceFactory {
    
    public static final SimpleValueMapStructure MAPSTRUCTURE = new SimpleValueMapStructure(String.class.getName(), "key", "Key", String.class.getName(), "value", "Value", "additionalFields");
    public static ListGridDataSource dataSource = null;
    
    protected OrderPresenter presenter;
    
    public PaymentAdditionalAttributesDataSourceFactory(OrderPresenter presenter) {
        this.presenter = presenter;
    }
    
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        if (dataSource == null) {
            operationTypes = new OperationTypes(OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP);
            PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey("id", EntityImplementations.PAYMENT_INFO, null));
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE, MAPSTRUCTURE);
            DataSourceModule[] modules = new DataSourceModule[]{
                new MapStructureClientModule(CeilingEntities.PAYMENT_INFO, persistencePerspective, AppServices.DYNAMIC_ENTITY)
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
