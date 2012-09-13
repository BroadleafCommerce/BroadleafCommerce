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


import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StructuredContentDisplay extends DynamicEditDisplay, RulesDisplayIf {

    public FilterBuilder getCustomerFilterBuilder();

    public void setCustomerFilterBuilder(FilterBuilder customerFilterBuilder);

    public FilterBuilder getProductFilterBuilder();

    public void setProductFilterBuilder(FilterBuilder productFilterBuilder);

    public FilterBuilder getTimeFilterBuilder();

    public void setTimeFilterBuilder(FilterBuilder timeFilterBuilder);

    public FilterBuilder getRequestFilterBuilder();

    public void setRequestFilterBuilder(FilterBuilder requestFilterBuilder);

    public ToolStrip getStructuredContentToolBar();

    public void setStructuredContentToolBar(ToolStrip structuredContentToolBar);

    public ToolStripButton getRulesSaveButton();

    public void setStructuredContentSaveButton(ToolStripButton structuredContentSaveButton);

    public ToolStripButton getRulesRefreshButton();

    public void setStructuredContentRefreshButton(ToolStripButton structuredContentRefreshButton);

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

}
