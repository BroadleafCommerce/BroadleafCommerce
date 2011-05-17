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
package org.broadleafcommerce.gwt.admin.client.view.order;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.TabSet;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.gwt.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.SubItemView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

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
	protected GridStructureView orderItemFeeDisplay;
	protected GridStructureView fulfillmentGroupAdjustmentDisplay;
    
	public OrderView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView(AdminModule.ADMINMESSAGES.ordersListTitle(), entityDataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);
        
        TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab(AdminModule.ADMINMESSAGES.orderDetailsTabTitle());
        dynamicFormDisplay = new DynamicFormView(AdminModule.ADMINMESSAGES.orderDetailsTitle(), entityDataSource);
        orderAdjustmentDisplay = new GridStructureView(AdminModule.ADMINMESSAGES.orderAdjustmentsTitle(), false, false);
        ((FormOnlyView) ((DynamicFormView) dynamicFormDisplay).getFormOnlyDisplay()).addMember(orderAdjustmentDisplay);
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);
        
        Tab orderItemsTab = new Tab(AdminModule.ADMINMESSAGES.orderItemsTabTitle()); 
        orderItemsDisplay = new OrderItemView(AdminModule.ADMINMESSAGES.orderItemsListTitle(), false, false);
        orderItemsTab.setPane(orderItemsDisplay);
        orderItemFeeDisplay = new GridStructureView(AdminModule.ADMINMESSAGES.orderItemFeeListTitle(), false, false);
        ((FormOnlyView) orderItemsDisplay.getFormOnlyDisplay()).addMember(orderItemFeeDisplay);
        orderItemAdjustmentDisplay = new GridStructureView(AdminModule.ADMINMESSAGES.orderItemAdjustmentsListTitle(), false, false);
        ((FormOnlyView) orderItemsDisplay.getFormOnlyDisplay()).addMember(orderItemAdjustmentDisplay);
        topTabSet.addTab(orderItemsTab);
        
        Tab fgTab = new Tab(AdminModule.ADMINMESSAGES.fgTabTitle()); 
        fulfillmentGroupDisplay = new SubItemView(AdminModule.ADMINMESSAGES.fgListTitle(), false, false);
        fulfillmentGroupAdjustmentDisplay = new GridStructureView(AdminModule.ADMINMESSAGES.fgAdjustmentsListTitle(), false, false);
        ((FormOnlyView) ((SubItemView) fulfillmentGroupDisplay).getFormOnlyDisplay()).addMember(fulfillmentGroupAdjustmentDisplay);
        fgTab.setPane(fulfillmentGroupDisplay);
        topTabSet.addTab(fgTab);
        
        Tab paymentInfoTab = new Tab(AdminModule.ADMINMESSAGES.paymentInfoTabTitle()); 
        paymentInfoDisplay = new SubItemView(AdminModule.ADMINMESSAGES.paymentInfoListTitle(), false, false);
        additionalAttributesDisplay = new GridStructureView(AdminModule.ADMINMESSAGES.additionalAttributesListTitle(), false, false);
        ((FormOnlyView) ((SubItemView) paymentInfoDisplay).getFormOnlyDisplay()).addMember(additionalAttributesDisplay);
        paymentInfoTab.setPane(paymentInfoDisplay);
        topTabSet.addTab(paymentInfoTab);
        
        Tab offerCodesTab = new Tab(AdminModule.ADMINMESSAGES.offerCodeTabTitle()); 
        offerCodeDisplay = new SubItemView(AdminModule.ADMINMESSAGES.offerCodeListTitle(), false, false);
        offerCodesTab.setPane(offerCodeDisplay);
        topTabSet.addTab(offerCodesTab);
        
        addMember(leftVerticalLayout);
        addMember(topTabSet);
	}

	public Canvas asCanvas() {
		return this;
	}

	public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
	
	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

	public OrderItemDisplay getOrderItemsDisplay() {
		return orderItemsDisplay;
	}

	public SubItemDisplay getFulfillmentGroupDisplay() {
		return fulfillmentGroupDisplay;
	}

	public SubItemDisplay getPaymentInfoDisplay() {
		return paymentInfoDisplay;
	}
	
	public SubItemDisplay getOfferCodeDisplay() {
		return offerCodeDisplay;
	}

	public GridStructureDisplay getAdditionalAttributesDisplay() {
		return additionalAttributesDisplay;
	}
	
	public GridStructureDisplay getOrderAdjustmentDisplay() {
		return orderAdjustmentDisplay;
	}
	
	public GridStructureDisplay getOrderItemAdjustmentDisplay() {
		return orderItemAdjustmentDisplay;
	}
	
	public GridStructureDisplay getFulfillmentGroupAdjustmentDisplay() {
		return fulfillmentGroupAdjustmentDisplay;
	}

	public GridStructureView getOrderItemFeeDisplay() {
		return orderItemFeeDisplay;
	}
}
