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

package org.broadleafcommerce.cms.admin.client.view.pages;

import org.broadleafcommerce.openadmin.client.rules.RulesDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.layout.VLayout;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: jfischer Date: 8/22/11 Time: 3:51 PM To
 * change this template use File | Settings | File Templates.
 */
public interface PagesDisplay extends DynamicEditDisplay, RulesDisplay {

    public FilterBuilder getCustomerFilterBuilder();

    public FilterBuilder getProductFilterBuilder();

    public FilterBuilder getTimeFilterBuilder();

    public FilterBuilder getRequestFilterBuilder();

    @Override
    public DynamicEntityListDisplay getListDisplay();

    @Override
    public DynamicFormDisplay getDynamicFormDisplay();

    public ComboBoxItem getCurrentLocale();

    public void setCurrentLocale(ComboBoxItem currentLocale);

    public void enableRules();

    public void disableRules();

    public VLayout getNewItemBuilderLayout();

    public void setNewItemBuilderLayout(VLayout newItemBuilderLayout);

    public Button getAddItemButton();

    public void setAddItemButton(Button addItemButton);

    public void setItemBuilderContainerLayout(VLayout itemBuilderContainerLayout);

    public Label getCustomerLabel();

    public void setCustomerLabel(Label customerLabel);

    public Label getTimeLabel();

    public void setTimeLabel(Label timeLabel);

    public Label getRequestLabel();

    public void setRequestLabel(Label requestLabel);

    public Label getProductLabel();

    public void setProductLabel(Label productLabel);

    public Label getOrderItemLabel();

    public void setOrderItemLabel(Label orderItemLabel);
    
    public  List<ItemBuilderDisplay> getItemBuilderViews();

    public  void setItemBuilderViews(List<ItemBuilderDisplay> itemBuilderViews);

    public  VLayout getItemBuilderContainerLayout();

    public  ItemBuilderDisplay addItemBuilder(DataSource orderItemDataSource);

    public  void removeItemBuilder(ItemBuilderDisplay itemBuilder);
    
    public  void removeAllItemBuilders();
    
}
