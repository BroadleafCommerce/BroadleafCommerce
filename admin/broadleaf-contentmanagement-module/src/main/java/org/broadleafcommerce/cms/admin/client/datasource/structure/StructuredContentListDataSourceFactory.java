
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

package org.broadleafcommerce.cms.admin.client.datasource.structure;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.cms.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.cms.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.cms.admin.client.datasource.structure.module.StructuredContentListClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.DataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.common.presentation.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

/**
 * 
 * @author jfischer
 *
 */
public class StructuredContentListDataSourceFactory implements DataSourceFactory {

    public static final String structuredContentTypeForeignKey = "structuredContentType";
    public static final String localeForeignKey = "locale";

	public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        operationTypes = new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY);
        PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{new ForeignKey(structuredContentTypeForeignKey, EntityImplementations.STRUCTUREDCONTENTTYPEIMPL, null, ForeignKeyRestrictionType.ID_EQ, "name"), new ForeignKey(localeForeignKey, EntityImplementations.LOCALEIMPL, null, ForeignKeyRestrictionType.ID_EQ, "friendlyName")});
        DataSourceModule[] modules = new DataSourceModule[]{
            new StructuredContentListClientEntityModule(CeilingEntities.STRUCTUREDCONTENT, persistencePerspective, AppServices.DYNAMIC_ENTITY)
        };
        StructuredContentListDataSource dataSource = new StructuredContentListDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
        dataSource.buildFields(null, false, cb);
	}

}
