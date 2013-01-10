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

package org.broadleafcommerce.cms.admin.client.view.structure;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

import java.util.List;

public interface RulesDisplayIf {

    public abstract List<ItemBuilderDisplay> getItemBuilderViews();

    public abstract void setItemBuilderViews(List<ItemBuilderDisplay> itemBuilderViews);

    public abstract VLayout getItemBuilderContainerLayout();

    public abstract ItemBuilderDisplay addItemBuilder(DataSource orderItemDataSource);

    public abstract void removeItemBuilder(ItemBuilderDisplay itemBuilder);

    public abstract void removeAllItemBuilders();
   
    public ToolStripButton getRulesSaveButton();
    
    public ToolStripButton getRulesRefreshButton(); 

}