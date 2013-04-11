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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;

/**
 * 
 * @author jfischer
 *
 */
public interface SubPresentable {

    public void setStartState();
    
    public void enable();
    
    public void disable();
    
    public boolean load(Record associatedRecord, AbstractDynamicDataSource associatedDataSource, final DSCallback cb);

    public boolean load(Record associatedRecord, AbstractDynamicDataSource associatedDataSource);
    
    public void bind();

    public void setReadOnly(Boolean readOnly);
    
    public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable);

    public Canvas getDisplay();
    
}
