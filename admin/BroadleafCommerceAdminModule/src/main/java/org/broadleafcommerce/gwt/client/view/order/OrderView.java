package org.broadleafcommerce.gwt.client.view.order;

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
        
		listDisplay = new DynamicEntityListView("Orders", entityDataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);
        
        TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab("Details");
        dynamicFormDisplay = new DynamicFormView("Order Details", entityDataSource);
        orderAdjustmentDisplay = new GridStructureView("Order Adjustments", false, false);
        ((FormOnlyView) ((DynamicFormView) dynamicFormDisplay).getFormOnlyDisplay()).addMember(orderAdjustmentDisplay);
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);
        
        Tab orderItemsTab = new Tab("Order Items"); 
        orderItemsDisplay = new OrderItemView("Order Items", false, false);
        orderItemsTab.setPane(orderItemsDisplay);
        orderItemAdjustmentDisplay = new GridStructureView("Order Item Adjustments", false, false);
        ((FormOnlyView) orderItemsDisplay.getFormOnlyDisplay()).addMember(orderItemAdjustmentDisplay);
        topTabSet.addTab(orderItemsTab);
        
        Tab fgTab = new Tab("Fulfillment Groups"); 
        fulfillmentGroupDisplay = new SubItemView("Fulfillment Groups", false, false);
        fulfillmentGroupAdjustmentDisplay = new GridStructureView("Fulfillment Group Adjustments", false, false);
        ((FormOnlyView) ((SubItemView) fulfillmentGroupDisplay).getFormOnlyDisplay()).addMember(fulfillmentGroupAdjustmentDisplay);
        fgTab.setPane(fulfillmentGroupDisplay);
        topTabSet.addTab(fgTab);
        
        Tab paymentInfoTab = new Tab("Payment Infos"); 
        paymentInfoDisplay = new SubItemView("Payment Infos", false, false);
        additionalAttributesDisplay = new GridStructureView("Additional Attributes", false, false);
        ((FormOnlyView) ((SubItemView) paymentInfoDisplay).getFormOnlyDisplay()).addMember(additionalAttributesDisplay);
        paymentInfoTab.setPane(paymentInfoDisplay);
        topTabSet.addTab(paymentInfoTab);
        
        Tab offerCodesTab = new Tab("Offer Codes"); 
        offerCodeDisplay = new SubItemView("Offer Codes", false, false);
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
}
