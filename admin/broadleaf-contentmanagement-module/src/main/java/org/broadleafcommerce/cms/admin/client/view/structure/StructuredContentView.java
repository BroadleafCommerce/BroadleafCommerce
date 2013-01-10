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

package org.broadleafcommerce.cms.admin.client.view.structure;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.FieldDataSourceWrapper;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredContentView extends HLayout implements Instantiable, StructuredContentDisplay {

    protected DynamicEntityListView listDisplay;
    protected DynamicFormView dynamicFormDisplay;

    protected FilterBuilder customerFilterBuilder;
    protected FilterBuilder productFilterBuilder;
    protected FilterBuilder timeFilterBuilder;
    protected FilterBuilder requestFilterBuilder;
    protected ToolStrip structuredContentToolBar;
    protected ToolStripButton structuredContentSaveButton;
    protected ToolStripButton structuredContentRefreshButton;
    protected List<ItemBuilderDisplay> itemBuilderViews = new ArrayList<ItemBuilderDisplay>();
    protected VLayout newItemBuilderLayout;
    protected Button addItemButton;
    protected VLayout itemBuilderContainerLayout;
    protected Label customerLabel;
    protected Label timeLabel;
    protected Label requestLabel;
    protected Label productLabel;
    protected Label orderItemLabel;

    public StructuredContentView() {
        setHeight100();
        setWidth100();
    }

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        DataSource customerDataSource = additionalDataSources[0];
        DataSource timeDataSource = additionalDataSources[1];
        DataSource requestDataSource = additionalDataSources[2];
        DataSource orderItemDataSource = additionalDataSources[3];
        DataSource productDataSource = additionalDataSources[4];

        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("structureLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("40%");
        leftVerticalLayout.setShowResizeBar(true);
        listDisplay = new DynamicEntityListView("", entityDataSource);
        listDisplay.getGrid().setHoverMoveWithMouse(true);
        listDisplay.getGrid().setCanHover(true);
        listDisplay.getGrid().setShowHover(true);
        listDisplay.getGrid().setHoverOpacity(75);
        listDisplay.getGrid().setHoverCustomizer(new HoverCustomizer() {
            @Override
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (record != null && record.getAttribute("lockedFlag") != null && record.getAttributeAsBoolean("lockedFlag")) {
                    return BLCMain.getMessageManager().replaceKeys(BLCMain.getMessageManager().getString("lockedMessage"), new String[]{"userName", "date"}, new String[]{record.getAttribute("auditable.updatedBy.name"), record.getAttribute("auditable.dateUpdated")});
                }
                return null;
            }
        });

        leftVerticalLayout.addMember(listDisplay);
        dynamicFormDisplay = new DynamicFormView("", entityDataSource);

        addMember(leftVerticalLayout);

        TabSet topTabSet = new TabSet();
        topTabSet.setID("scTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);

        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("scDetailsTabTitle"));
        detailsTab.setID("scDetailsTab");
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);

        Tab rulesTab = new Tab(BLCMain.getMessageManager().getString("scRulesTabTitle"));
        rulesTab.setID("scRulesTab");
        VLayout rulesLayout = new VLayout();
        rulesLayout.setHeight100();
        rulesLayout.setWidth100();
        rulesLayout.setBackgroundColor("#eaeaea");
        rulesLayout.setOverflow(Overflow.AUTO);

        structuredContentToolBar = new ToolStrip();
        structuredContentToolBar.setHeight(30);
        structuredContentToolBar.setWidth100();
        structuredContentToolBar.addSpacer(6);
        structuredContentSaveButton = new ToolStripButton();
        structuredContentSaveButton.setIcon(GWT.getModuleBaseURL()+"admin/images/button/save.png");
        structuredContentSaveButton.setTitle(BLCMain.getMessageManager().getString("saveTitle"));
        structuredContentToolBar.addButton(structuredContentSaveButton);
        structuredContentSaveButton.setDisabled(true);
        structuredContentRefreshButton = new ToolStripButton();
        structuredContentRefreshButton.setIcon(GWT.getModuleBaseURL()+"admin/images/button/refresh.png");
        structuredContentRefreshButton.setTitle(BLCMain.getMessageManager().getString("restoreTitle"));
        structuredContentRefreshButton.setTooltip(BLCMain.getMessageManager().getString("restoreTooltip"));
        structuredContentRefreshButton.setDisabled(true);
        structuredContentToolBar.addButton(structuredContentRefreshButton);
        structuredContentToolBar.addSpacer(6);
        rulesLayout.addMember(structuredContentToolBar);

        VLayout innerLayout = new VLayout();
        innerLayout.setHeight100();
        innerLayout.setWidth100();
        innerLayout.setBackgroundColor("#eaeaea");
        innerLayout.setLayoutMargin(20);

        customerLabel = new Label();
        customerLabel.setContents(BLCMain.getMessageManager().getString("scCustomerRule"));
        customerLabel.setHeight(20);
        customerLabel.setBaseStyle("disabledLabel");
        innerLayout.addMember(customerLabel);
        
        customerFilterBuilder = new FilterBuilder();
        customerFilterBuilder.setDataSource(customerDataSource);
        customerFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(customerDataSource));
        customerFilterBuilder.setLayoutBottomMargin(20);
        customerFilterBuilder.setAllowEmpty(true);
        customerFilterBuilder.setValidateOnChange(false);
        customerFilterBuilder.setDisabled(true);
        innerLayout.addMember(customerFilterBuilder);

        timeLabel = new Label();
        timeLabel.setContents(BLCMain.getMessageManager().getString("scTimeRule"));
        timeLabel.setHeight(20);
        timeLabel.setBaseStyle("disabledLabel");
        innerLayout.addMember(timeLabel);

        timeFilterBuilder = new FilterBuilder();
        timeFilterBuilder.setDataSource(timeDataSource);
        timeFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(timeDataSource));
        timeFilterBuilder.setLayoutBottomMargin(20);
        timeFilterBuilder.setAllowEmpty(true);
        timeFilterBuilder.setValidateOnChange(false);
        timeFilterBuilder.setDisabled(true);
        innerLayout.addMember(timeFilterBuilder);

        requestLabel = new Label();
        requestLabel.setContents(BLCMain.getMessageManager().getString("scRequestRule"));
        requestLabel.setHeight(20);
        requestLabel.setBaseStyle("disabledLabel");
        innerLayout.addMember(requestLabel);

        requestFilterBuilder = new FilterBuilder();
        requestFilterBuilder.setDataSource(requestDataSource);
        requestFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(requestDataSource));
        requestFilterBuilder.setLayoutBottomMargin(20);
        requestFilterBuilder.setAllowEmpty(true);
        requestFilterBuilder.setValidateOnChange(false);
        requestFilterBuilder.setDisabled(true);
        innerLayout.addMember(requestFilterBuilder);

        productLabel = new Label();
        productLabel.setContents(BLCMain.getMessageManager().getString("scProductRule"));
        productLabel.setHeight(20);
        productLabel.setBaseStyle("disabledLabel");
        innerLayout.addMember(productLabel);

        productFilterBuilder = new FilterBuilder();
        productFilterBuilder.setDataSource(productDataSource);
        productFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(productDataSource));
        productFilterBuilder.setLayoutBottomMargin(20);
        productFilterBuilder.setAllowEmpty(true);
        productFilterBuilder.setValidateOnChange(false);
        productFilterBuilder.setDisabled(true);
        innerLayout.addMember(productFilterBuilder);

        orderItemLabel = new Label();
        orderItemLabel.setContents(BLCMain.getMessageManager().getString("scOrderItemRule"));
        orderItemLabel.setHeight(20);
        orderItemLabel.setBaseStyle("disabledLabel");
        innerLayout.addMember(orderItemLabel);

        newItemBuilderLayout = new VLayout();
        HLayout buttonLayout = new HLayout();
        buttonLayout.setID("offerButtonLayout");
        buttonLayout.setWidth100();
        buttonLayout.setAlign(Alignment.LEFT);
        buttonLayout.setHeight(30);
        addItemButton = new Button();
        addItemButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/actions/add.png");
        addItemButton.setTitle(BLCMain.getMessageManager().getString("newItemRuleButtonTitle"));
        addItemButton.setWidth(136);
        addItemButton.setWrap(false);
        addItemButton.setDisabled(true);
        buttonLayout.addMember(addItemButton);
        buttonLayout.setLayoutBottomMargin(10);
        newItemBuilderLayout.addMember(buttonLayout);
        itemBuilderContainerLayout = new VLayout();
        newItemBuilderLayout.addMember(itemBuilderContainerLayout);

        innerLayout.addMember(newItemBuilderLayout);

        rulesLayout.addMember(innerLayout);
        rulesTab.setPane(rulesLayout);
        topTabSet.addTab(rulesTab);

        addMember(topTabSet);
    }

    public ItemBuilderDisplay addItemBuilder(DataSource orderItemDataSource) {
        ItemBuilderDisplay builder = new ItemBuilderView(orderItemDataSource, true);
        builder.enable();
        builder.setDirty(true);
        itemBuilderContainerLayout.addMember((ItemBuilderView) builder);
        itemBuilderViews.add(builder);
        return builder;
    }

    public void removeItemBuilder(ItemBuilderDisplay itemBuilder) {
        itemBuilderContainerLayout.removeMember((ItemBuilderView) itemBuilder);
        itemBuilderViews.remove(itemBuilder);
    }

    public void removeAllItemBuilders() {
        ItemBuilderView[] myViews = itemBuilderViews.toArray(new ItemBuilderView[]{});
        for (ItemBuilderView view : myViews) {
            removeItemBuilder(view);
        }
    }

    public Canvas asCanvas() {
        return this;
    }

    public DynamicEntityListDisplay getListDisplay() {
        return listDisplay;
    }

    public DynamicFormDisplay getDynamicFormDisplay() {
        return dynamicFormDisplay;
    }

    public FilterBuilder getCustomerFilterBuilder() {
        return customerFilterBuilder;
    }

    public void setCustomerFilterBuilder(FilterBuilder customerFilterBuilder) {
        this.customerFilterBuilder = customerFilterBuilder;
    }

    public FilterBuilder getProductFilterBuilder() {
        return productFilterBuilder;
    }

    public void setProductFilterBuilder(FilterBuilder productFilterBuilder) {
        this.productFilterBuilder = productFilterBuilder;
    }

    public FilterBuilder getTimeFilterBuilder() {
        return timeFilterBuilder;
    }

    public void setTimeFilterBuilder(FilterBuilder timeFilterBuilder) {
        this.timeFilterBuilder = timeFilterBuilder;
    }

    public FilterBuilder getRequestFilterBuilder() {
        return requestFilterBuilder;
    }

    public void setRequestFilterBuilder(FilterBuilder requestFilterBuilder) {
        this.requestFilterBuilder = requestFilterBuilder;
    }

    public ToolStrip getStructuredContentToolBar() {
        return structuredContentToolBar;
    }

    public void setStructuredContentToolBar(ToolStrip structuredContentToolBar) {
        this.structuredContentToolBar = structuredContentToolBar;
    }

    public ToolStripButton getStructuredContentSaveButton() {
        return structuredContentSaveButton;
    }

    public void setStructuredContentSaveButton(ToolStripButton structuredContentSaveButton) {
        this.structuredContentSaveButton = structuredContentSaveButton;
    }

    public ToolStripButton getStructuredContentRefreshButton() {
        return structuredContentRefreshButton;
    }

    public void setStructuredContentRefreshButton(ToolStripButton structuredContentRefreshButton) {
        this.structuredContentRefreshButton = structuredContentRefreshButton;
    }

    public List<ItemBuilderDisplay> getItemBuilderViews() {
        return itemBuilderViews;
    }

    public void setItemBuilderViews(List<ItemBuilderDisplay> itemBuilderViews) {
        this.itemBuilderViews = itemBuilderViews;
    }

    public VLayout getNewItemBuilderLayout() {
        return newItemBuilderLayout;
    }

    public void setNewItemBuilderLayout(VLayout newItemBuilderLayout) {
        this.newItemBuilderLayout = newItemBuilderLayout;
    }

    public Button getAddItemButton() {
        return addItemButton;
    }

    public void setAddItemButton(Button addItemButton) {
        this.addItemButton = addItemButton;
    }

    public VLayout getItemBuilderContainerLayout() {
        return itemBuilderContainerLayout;
    }

    public void setItemBuilderContainerLayout(VLayout itemBuilderContainerLayout) {
        this.itemBuilderContainerLayout = itemBuilderContainerLayout;
    }

    public Label getCustomerLabel() {
        return customerLabel;
    }

    public void setCustomerLabel(Label customerLabel) {
        this.customerLabel = customerLabel;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }

    public Label getRequestLabel() {
        return requestLabel;
    }

    public void setRequestLabel(Label requestLabel) {
        this.requestLabel = requestLabel;
    }

    public Label getProductLabel() {
        return productLabel;
    }

    public void setProductLabel(Label productLabel) {
        this.productLabel = productLabel;
    }

    public Label getOrderItemLabel() {
        return orderItemLabel;
    }

    public void setOrderItemLabel(Label orderItemLabel) {
        this.orderItemLabel = orderItemLabel;
    }
}
