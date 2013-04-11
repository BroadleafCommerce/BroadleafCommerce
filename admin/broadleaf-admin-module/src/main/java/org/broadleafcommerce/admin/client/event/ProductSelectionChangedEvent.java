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

package org.broadleafcommerce.admin.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * 
 * @author jfischer
 *
 */
public class ProductSelectionChangedEvent extends GwtEvent<ProductSelectionChangedEventHandler> {

    public static Type<ProductSelectionChangedEventHandler> TYPE = new Type<ProductSelectionChangedEventHandler>();

    private ListGridRecord record;
    private DataSource dataSource;

    public ProductSelectionChangedEvent(ListGridRecord record, DataSource dataSource) {
        this.record = record;
        this.dataSource = dataSource;
    }

    @Override
    public Type<ProductSelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ProductSelectionChangedEventHandler handler) {
        handler.onChangeSelection(this);
    }

    public ListGridRecord getRecord() {
        return record;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }

}
