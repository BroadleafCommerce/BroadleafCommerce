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

package org.broadleafcommerce.admin.client.datasource.catalog.category.module;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.AdornedTargetListClientModule;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Record;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryTreeAdornedTargetListModule extends AdornedTargetListClientModule {

    /**
     * @param ceilingEntityFullyQualifiedClassname
     * @param persistencePerspective
     * @param service
     */
    public CategoryTreeAdornedTargetListModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
    }

    @Override
    public Record buildRecord(Entity entity, Boolean updateId) {
        return super.buildRecord(entity, true);
    }

}
