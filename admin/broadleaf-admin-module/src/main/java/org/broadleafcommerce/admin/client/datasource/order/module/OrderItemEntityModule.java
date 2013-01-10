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

package org.broadleafcommerce.admin.client.datasource.order.module;

import java.util.Arrays;

import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * 
 * @author jfischer
 *
 */
public class OrderItemEntityModule extends BasicClientEntityModule {

    /**
     * @param ceilingEntityFullyQualifiedClassname
     * @param persistencePerspective
     * @param service
     */
    public OrderItemEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
    }

    @Override
    public Record updateRecord(Entity entity, Record record, Boolean updateId) {
        Record temp = super.updateRecord(entity, record, updateId);
        if (Arrays.binarySearch(entity.getType(), EntityImplementations.BUNDLE_ORDER_ITEM) >= 0) {
            ((ListGridRecord) temp).setCanExpand(true);
        } else {
            ((ListGridRecord) temp).setCanExpand(false);
        }
        return temp;
    }

}
