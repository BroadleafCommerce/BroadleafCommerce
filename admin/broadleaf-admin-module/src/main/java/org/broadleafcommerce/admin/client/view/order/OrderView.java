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

package org.broadleafcommerce.admin.client.view.order;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
public class OrderView extends HLayout implements Instantiable, OrderDisplay {
    
    protected DynamicFormView dynamicFormDisplay;
    protected DynamicEntityListView listDisplay;
    protected OrderItemView orderItemsDisplay;
    protected SubItemView fulfillmentGroupDisplay;
    protected SubItemView paymentInfoDisplay;
    protected GridStructureView additionalAttributesDisplay;
    protected SubItemView offerCodeDisplay;
    protected GridStructureView orderAdjustmentDisplay;
    protected GridStructureView orderItemAdjustmentDisplay;
    protected GridStructureView orderItemPriceDetailDisplay;
    protected GridStructureView orderItemFeeDisplay;
    protected GridStructureView fulfillmentGroupAdjustmentDisplay;
    protected GridStructureView paymentResponseDisplay;
    protected GridStructureView paymentLogDisplay;
    protected ToolStripButton exportOrdersButton;
    
    public OrderView() {
        setHeight100();
        setWidth100();
    }
    
    @Override
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("orderLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("50%");
        leftVerticalLayout.setShowResizeBar(true);
        
        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("ordersListTitle"), entityDataSource, false);
        leftVerticalLayout.addMember(listDisplay);
        
        exportOrdersButton = new ToolStripButton(BLCMain.getMessageManager().getString("exportOrdersButtonTitle"));
        listDisplay.getToolBar().addButton(exportOrdersButton);
        
        TabSet topTabSet = new TabSet(); 
        topTabSet.setID("orderTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("orderDetailsTabTitle"));
        detailsTab.setID("orderDetailsTab");
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("orderDetailsTitle"), entityDataSource);
        orderAdjustmentDisplay = new GridStructureView(BLCMain.getMessageManager().getString("orderAdjustmentsTitle"), false, false);
        orderAdjustmentDisplay.getAddButton().setVisible(false);
        orderAdjustmentDisplay.getRemoveButton().setVisible(false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(orderAdjustmentDisplay);
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);
        
        Tab orderItemsTab = new Tab(BLCMain.getMessageManager().getString("orderItemsTabTitle"));
        orderItemsTab.setID("orderOrderItemsTab");
        orderItemsDisplay = new OrderItemView(BLCMain.getMessageManager().getString("orderItemsListTitle"), false, false);
        orderItemsTab.setPane(orderItemsDisplay);
        orderItemFeeDisplay = new GridStructureView(BLCMain.getMessageManager().getString("orderItemFeeListTitle"), false, false);
        orderItemFeeDisplay.getAddButton().setVisible(false);
        orderItemFeeDisplay.getRemoveButton().setVisible(false);
        ((FormOnlyView) orderItemsDisplay.getFormOnlyDisplay()).addMember(orderItemFeeDisplay);
        orderItemAdjustmentDisplay = new GridStructureView(BLCMain.getMessageManager().getString("orderItemAdjustmentsListTitle"), false, false);
        orderItemAdjustmentDisplay.getAddButton().setVisible(false);
        orderItemAdjustmentDisplay.getRemoveButton().setVisible(false);
        ((FormOnlyView) orderItemsDisplay.getFormOnlyDisplay()).addMember(orderItemAdjustmentDisplay);

        orderItemPriceDetailDisplay = new GridStructureView(BLCMain.getMessageManager().getString("orderItemPriceDetailListTitle"), false, false);
        orderItemPriceDetailDisplay.getAddButton().setVisible(false);
        orderItemPriceDetailDisplay.getRemoveButton().setVisible(false);
        ((FormOnlyView) orderItemsDisplay.getFormOnlyDisplay()).addMember(orderItemPriceDetailDisplay);

        topTabSet.addTab(orderItemsTab);
        
        Tab fgTab = new Tab(BLCMain.getMessageManager().getString("fgTabTitle"));
        fgTab.setID("orderFgTab");
        fulfillmentGroupDisplay = new SubItemView(BLCMain.getMessageManager().getString("fgListTitle"), false, false);
        fulfillmentGroupAdjustmentDisplay = new GridStructureView(BLCMain.getMessageManager().getString("fgAdjustmentsListTitle"), false, false);
        fulfillmentGroupAdjustmentDisplay.getAddButton().setVisible(false);
        fulfillmentGroupAdjustmentDisplay.getRemoveButton().setVisible(false);
        ((FormOnlyView) fulfillmentGroupDisplay.getFormOnlyDisplay()).addMember(fulfillmentGroupAdjustmentDisplay);
        fgTab.setPane(fulfillmentGroupDisplay);
        topTabSet.addTab(fgTab);
        
        Tab paymentInfoTab = new Tab(BLCMain.getMessageManager().getString("paymentInfoTabTitle"));
        paymentInfoTab.setID("orderPaymentInfoTab");
        paymentInfoDisplay = new SubItemView(BLCMain.getMessageManager().getString("paymentInfoListTitle"), false, false);

        additionalAttributesDisplay = new GridStructureView(BLCMain.getMessageManager().getString("additionalAttributesListTitle"), false, false);
        ((FormOnlyView) paymentInfoDisplay.getFormOnlyDisplay()).addMember(additionalAttributesDisplay);

        paymentResponseDisplay = new GridStructureView(BLCMain.getMessageManager().getString("paymentResponseListTitle"), false, false);
        ((FormOnlyView) paymentInfoDisplay.getFormOnlyDisplay()).addMember(paymentResponseDisplay);

        paymentLogDisplay = new GridStructureView(BLCMain.getMessageManager().getString("paymentLogListTitle"), false, false);
        ((FormOnlyView) paymentInfoDisplay.getFormOnlyDisplay()).addMember(paymentLogDisplay);

        paymentInfoTab.setPane(paymentInfoDisplay);
        topTabSet.addTab(paymentInfoTab);
        
        Tab offerCodesTab = new Tab(BLCMain.getMessageManager().getString("offerCodeTabTitle"));
        offerCodesTab.setID("orderOfferCodesTab");
        offerCodeDisplay = new SubItemView(BLCMain.getMessageManager().getString("offerCodeListTitle"), false, false);
        offerCodesTab.setPane(offerCodeDisplay);
        topTabSet.addTab(offerCodesTab);
        leftVerticalLayout.setParentElement(this);
        addMember(leftVerticalLayout);
        addMember(topTabSet);
    }

    @Override
    public Canvas asCanvas() {
        return this;
    }

    @Override
    public DynamicFormDisplay getDynamicFormDisplay() {
        return dynamicFormDisplay;
    }
    
    @Override
    public DynamicEntityListDisplay getListDisplay() {
        return listDisplay;
    }

    @Override
    public OrderItemDisplay getOrderItemsDisplay() {
        return orderItemsDisplay;
    }

    @Override
    public SubItemDisplay getFulfillmentGroupDisplay() {
        return fulfillmentGroupDisplay;
    }

    @Override
    public SubItemDisplay getPaymentInfoDisplay() {
        return paymentInfoDisplay;
    }
    
    @Override
    public SubItemDisplay getOfferCodeDisplay() {
        return offerCodeDisplay;
    }

    @Override
    public GridStructureDisplay getAdditionalAttributesDisplay() {
        return additionalAttributesDisplay;
    }
    
    @Override
    public GridStructureDisplay getOrderAdjustmentDisplay() {
        return orderAdjustmentDisplay;
    }

    @Override
    public GridStructureDisplay getOrderItemAdjustmentDisplay() {
        return orderItemAdjustmentDisplay;
    }

    @Override
    public GridStructureDisplay getFulfillmentGroupAdjustmentDisplay() {
        return fulfillmentGroupAdjustmentDisplay;
    }

    @Override
    public GridStructureView getOrderItemFeeDisplay() {
        return orderItemFeeDisplay;
    }

    @Override
    public GridStructureView getPaymentLogDisplay() {
        return paymentLogDisplay;
    }

    @Override
    public GridStructureView getPaymentResponseDisplay() {
        return paymentResponseDisplay;
    }
    
    @Override
    public ToolStripButton getExportOrdersButton() {
        return exportOrdersButton;
    }

    @Override
    public GridStructureDisplay getOrderItemPriceDetailDisplay() {
        return orderItemPriceDetailDisplay;
    }

}
