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

package org.broadleafcommerce.admin.client.datasource.catalog.category;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.admin.client.presenter.catalog.category.CategoryPresenter;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.datasource.DataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ComplexValueMapStructureDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.MapStructureClientModule;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import java.util.LinkedHashMap;

/**
 * 
 * @author jfischer
 *
 */
public class MediaMapDataSourceFactory implements DataSourceFactory {
    
    public static final MapStructure MAPSTRUCTURE = new MapStructure(String.class.getName(), "key", "Key", EntityImplementations.MEDIA, "categoryMedia", true);
    public static ComplexValueMapStructureDataSource dataSource = null;
    
    protected CategoryPresenter presenter;
    
    public MediaMapDataSourceFactory(CategoryPresenter presenter) {
        this.presenter = presenter;
    }
    
    @SuppressWarnings("unchecked")
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        if (dataSource == null) {
            operationTypes = new OperationTypes(OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP);
            PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey("id", EntityImplementations.CATEGORY, null));
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE, MAPSTRUCTURE);
            DataSourceModule[] modules = new DataSourceModule[]{
                new MapStructureClientModule(CeilingEntities.CATEGORY, persistencePerspective, AppServices.DYNAMIC_ENTITY)
            };
            dataSource = new ComplexValueMapStructureDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules, (LinkedHashMap<String, String>) additionalItems[0]);
            dataSource.buildFields(null, false, cb);
        } else {
            if (cb != null) {
                cb.onSuccess(dataSource);
            }
        }
    }

}
