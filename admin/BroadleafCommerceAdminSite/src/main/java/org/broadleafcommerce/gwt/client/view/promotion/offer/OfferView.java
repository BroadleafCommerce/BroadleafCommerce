package org.broadleafcommerce.gwt.client.view.promotion.offer;

import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.TabSet;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class OfferView extends HLayout implements Instantiable, OfferDisplay {
	
	protected DynamicFormView dynamicFormDisplay;
	protected DynamicEntityListView listDisplay;
	protected ImgButton orderButton;
	protected FilterBuilder orderFilterBuilder;
	protected ImgButton orderItemButton;
	protected FilterBuilder orderItemFilterBuilder;
	protected ImgButton fulfillmentGroupButton;
	protected FilterBuilder fulfillmentGroupFilterBuilder;
	protected ImgButton customerButton;
	protected FilterBuilder customerFilterBuilder;
	protected ToolStrip rulesToolbar;
	protected ToolStripButton rulesSaveButton;
	protected ToolStripButton rulesRefreshButton;
	protected ToolStripButton rulesBuilderButton;
    
	protected VLayout rulesBuilderLayout;
	protected VLayout rulesRawLayout;
	
	public OfferView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		DataSource orderDataSource = additionalDataSources[0]; 
		DataSource orderItemDataSource = additionalDataSources[1];
		DataSource fulfillmentGroupDataSource = additionalDataSources[2];
		DataSource customerDataSource = additionalDataSources[3];
		
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView("Promotions", entityDataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);
        
        TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("60%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab("Offer");
        dynamicFormDisplay = new DynamicFormView("Offer Details", entityDataSource);
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);
        
        Tab rulesTab = new Tab("Rules");
        VLayout rulesLayout = new VLayout();
        rulesLayout.setWidth100();
        rulesLayout.setBackgroundColor("#eaeaea");
        rulesLayout.setLayoutMargin(0);
        rulesToolbar = new ToolStrip();
        rulesToolbar.setHeight(20);
        rulesToolbar.setWidth100();
        rulesToolbar.addSpacer(6);
        rulesSaveButton = new ToolStripButton();  
        rulesSaveButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/save.png");   
        rulesToolbar.addButton(rulesSaveButton);
        rulesSaveButton.setDisabled(true);
        rulesRefreshButton = new ToolStripButton();  
        rulesRefreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/refresh.png");   
        rulesToolbar.addButton(rulesRefreshButton);
        rulesToolbar.addSpacer(6);
        Label rulesLabel = new Label("Rule Details");
        rulesLabel.setWrap(false);
        rulesToolbar.addMember(rulesLabel);
        rulesToolbar.addFill();
        Label wizardLabel = new Label("Use Rules Builder");
        wizardLabel.setWrap(false);
        rulesToolbar.addMember(wizardLabel);
        rulesBuilderButton = new ToolStripButton();  
        rulesBuilderButton.setIcon(GWT.getModuleBaseURL()+"admin/images/wizard.png");  
        rulesBuilderButton.setActionType(SelectionType.CHECKBOX);  
        rulesBuilderButton.setSelected(true);
        rulesBuilderButton.setDisabled(true);
        rulesToolbar.addButton(rulesBuilderButton); 
        rulesToolbar.addSpacer(6);
        rulesLayout.addMember(rulesToolbar);
        
        rulesBuilderLayout = new VLayout();
        rulesBuilderLayout.setWidth100();
        rulesBuilderLayout.setHeight100();
        rulesBuilderLayout.setBackgroundColor("#eaeaea");
        rulesBuilderLayout.setLayoutMargin(10);
        rulesBuilderLayout.setOverflow(Overflow.AUTO);
        
        rulesLayout.addMember(rulesBuilderLayout);
        
        HLayout orderStack = new HLayout();
        orderStack.setLayoutMargin(10);
        orderStack.setMembersMargin(10);
        orderStack.setHeight(30);
        orderButton = new ImgButton();  
        orderButton.setWidth(18);  
        orderButton.setHeight(18);  
        orderButton.setShowRollOver(false);  
        orderButton.setShowDown(false); 
        orderButton.setActionType(SelectionType.CHECKBOX);
        orderButton.setSrc(GWT.getModuleBaseURL()+"admin/images/button/button.png");
        orderButton.setDisabled(true);
        orderStack.addMember(orderButton);
        Label orderLabel = new Label("Order Match Rules");
        orderLabel.setWrap(false);
        orderStack.addMember(orderLabel);
        rulesBuilderLayout.addMember(orderStack);
        
        orderFilterBuilder = new FilterBuilder();  
        orderFilterBuilder.setDataSource(orderDataSource);
        orderFilterBuilder.setLayoutBottomMargin(10);
        orderFilterBuilder.setDisabled(true);
        rulesBuilderLayout.addMember(orderFilterBuilder);
        
        HLayout orderItemStack = new HLayout();
        orderItemStack.setLayoutMargin(10);
        orderItemStack.setMembersMargin(10);
        orderItemStack.setHeight(30);
        orderItemButton = new ImgButton();  
        orderItemButton.setWidth(18);  
        orderItemButton.setHeight(18);  
        orderItemButton.setShowRollOver(false);  
        orderItemButton.setShowDown(false); 
        orderItemButton.setActionType(SelectionType.CHECKBOX);
        orderItemButton.setSrc(GWT.getModuleBaseURL()+"admin/images/button/button.png");
        orderItemButton.setDisabled(true);
        orderItemStack.addMember(orderItemButton);
        Label orderItemLabel = new Label("Order Item Match Rules");
        orderItemLabel.setWrap(false);
        orderItemStack.addMember(orderItemLabel);
        rulesBuilderLayout.addMember(orderItemStack);
        
        orderItemFilterBuilder = new FilterBuilder();  
        orderItemFilterBuilder.setDataSource(orderItemDataSource);
        orderItemFilterBuilder.setLayoutBottomMargin(10);
        orderItemFilterBuilder.setDisabled(true);
        rulesBuilderLayout.addMember(orderItemFilterBuilder);
        
        HLayout fulfillmentGroupStack = new HLayout();
        fulfillmentGroupStack.setLayoutMargin(10);
        fulfillmentGroupStack.setMembersMargin(10);
        fulfillmentGroupStack.setHeight(30);
        fulfillmentGroupButton = new ImgButton();  
        fulfillmentGroupButton.setWidth(18);  
        fulfillmentGroupButton.setHeight(18);  
        fulfillmentGroupButton.setShowRollOver(false);  
        fulfillmentGroupButton.setShowDown(false); 
        fulfillmentGroupButton.setActionType(SelectionType.CHECKBOX);
        fulfillmentGroupButton.setSrc(GWT.getModuleBaseURL()+"admin/images/button/button.png");
        fulfillmentGroupButton.setDisabled(true);
        fulfillmentGroupStack.addMember(fulfillmentGroupButton);
        Label fulfillmentGroupLabel = new Label("Fulfillment Group Match Rules");
        fulfillmentGroupLabel.setWrap(false);
        fulfillmentGroupStack.addMember(fulfillmentGroupLabel);
        rulesBuilderLayout.addMember(fulfillmentGroupStack);
        
        fulfillmentGroupFilterBuilder = new FilterBuilder();  
        fulfillmentGroupFilterBuilder.setDataSource(fulfillmentGroupDataSource);
        fulfillmentGroupFilterBuilder.setLayoutBottomMargin(10);
        fulfillmentGroupFilterBuilder.setDisabled(true);
        rulesBuilderLayout.addMember(fulfillmentGroupFilterBuilder);
        
        HLayout customerStack = new HLayout();
        customerStack.setLayoutMargin(10);
        customerStack.setMembersMargin(10);
        customerStack.setHeight(30);
        customerButton = new ImgButton();  
        customerButton.setWidth(18);  
        customerButton.setHeight(18);  
        customerButton.setShowRollOver(false);  
        customerButton.setShowDown(false); 
        customerButton.setActionType(SelectionType.CHECKBOX);
        customerButton.setSrc(GWT.getModuleBaseURL()+"admin/images/button/button.png");
        customerButton.setDisabled(true);
        customerStack.addMember(customerButton);
        Label customerLabel = new Label("Customer Match Rules");
        customerLabel.setWrap(false);
        customerStack.addMember(customerLabel);
        rulesBuilderLayout.addMember(customerStack);
        
        customerFilterBuilder = new FilterBuilder();  
        customerFilterBuilder.setDataSource(customerDataSource);
        customerFilterBuilder.setLayoutBottomMargin(10);
        customerFilterBuilder.setDisabled(true);
        rulesBuilderLayout.addMember(customerFilterBuilder);
        
        rulesRawLayout = new VLayout();
        rulesRawLayout.setWidth100();
        rulesRawLayout.setHeight100();
        rulesRawLayout.setBackgroundColor("#eaeaea");
        rulesRawLayout.setLayoutMargin(10);
        rulesRawLayout.setOverflow(Overflow.AUTO);
        rulesRawLayout.setVisible(false);
        
        DynamicForm rawForm = new DynamicForm();
        TextItem orderRule = new TextItem();
        orderRule.setWidth(550);
        orderRule.setName("appliesToOrderRules");
        orderRule.setTitle("Order-Related Match Rules");
        orderRule.setHeight(150);
        orderRule.setWrapTitle(false);
        TextItem customerRule = new TextItem();
        customerRule.setWidth(550);
        customerRule.setName("appliesToCustomerRules");
        customerRule.setTitle("Customer Match Rules");
        customerRule.setHeight(150);
        customerRule.setWrapTitle(false);
        rawForm.setFields(orderRule, customerRule);
        rawForm.setDataSource(entityDataSource);
        
        rulesRawLayout.addMember(rawForm);
        
        rulesLayout.addMember(rulesRawLayout);
        
        rulesTab.setPane(rulesLayout);
        topTabSet.addTab(rulesTab);
        
        addMember(leftVerticalLayout);
        addMember(topTabSet);
	}
	
	public void showFilterBuilder() {
		rulesBuilderLayout.show();
		rulesRawLayout.hide();
	}
	
	public void showRawFields() {
		rulesRawLayout.show();
		rulesBuilderLayout.hide();
	}

	public Canvas asCanvas() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.promotion.OfferDisplay#getDynamicFormDisplay()
	 */
	public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.promotion.OfferDisplay#getListDisplay()
	 */
	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

	public ImgButton getOrderButton() {
		return orderButton;
	}

	public FilterBuilder getOrderFilterBuilder() {
		return orderFilterBuilder;
	}

	public ImgButton getOrderItemButton() {
		return orderItemButton;
	}

	public FilterBuilder getOrderItemFilterBuilder() {
		return orderItemFilterBuilder;
	}

	public ImgButton getFulfillmentGroupButton() {
		return fulfillmentGroupButton;
	}

	public FilterBuilder getFulfillmentGroupFilterBuilder() {
		return fulfillmentGroupFilterBuilder;
	}

	public ImgButton getCustomerButton() {
		return customerButton;
	}

	public FilterBuilder getCustomerFilterBuilder() {
		return customerFilterBuilder;
	}

	public ToolStrip getRulesToolbar() {
		return rulesToolbar;
	}

	public ToolStripButton getRulesSaveButton() {
		return rulesSaveButton;
	}

	public ToolStripButton getRulesRefreshButton() {
		return rulesRefreshButton;
	}

	public ToolStripButton getRulesBuilderButton() {
		return rulesBuilderButton;
	}
	
}
