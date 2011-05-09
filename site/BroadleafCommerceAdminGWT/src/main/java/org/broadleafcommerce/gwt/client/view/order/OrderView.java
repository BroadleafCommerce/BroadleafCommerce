package org.broadleafcommerce.gwt.client.view.order;

import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.TabSet;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;

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
	protected OrderItemGridStructureView orderItemsDisplay;
	protected FulfillmentGroupView fulfillmentGroupDisplay;
    
	public OrderView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(final DataSource dataSource) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView("Orders", dataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);
        
        TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("60%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab("Details");
        dynamicFormDisplay = new DynamicFormView("Order Details", dataSource);
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);
        
        Tab orderItemsTab = new Tab("Order Items"); 
        orderItemsDisplay = new OrderItemGridStructureView("Order Items", false, false);
        orderItemsTab.setPane(orderItemsDisplay);
        topTabSet.addTab(orderItemsTab);
        
        Tab fgTab = new Tab("Fulfillment Groups"); 
        fulfillmentGroupDisplay = new FulfillmentGroupView("Fulfillment Groups", false, false);
        fgTab.setPane(fulfillmentGroupDisplay);
        topTabSet.addTab(fgTab);
        
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

	public OrderItemGridStructureDisplay getOrderItemsDisplay() {
		return orderItemsDisplay;
	}

	public FulfillmentGroupDisplay getFulfillmentGroupDisplay() {
		return fulfillmentGroupDisplay;
	}

}
