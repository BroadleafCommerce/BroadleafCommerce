package org.broadleafcommerce.gwt.admin.client.view.promotion.offer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class OfferView extends HLayout implements Instantiable, OfferDisplay {
	
	protected DynamicFormView dynamicFormDisplay;
	protected DynamicEntityListView listDisplay;
	protected ToolStripButton advancedButton;
	protected ImgButton helpButtonType;
	protected RadioGroupItem deliveryTypeRadio;
	protected TextItem codeField;
	protected IntegerItem maxUseField;
	protected RadioGroupItem customerRuleRadio;
	protected FilterBuilder customerFilterBuilder;
	protected FilterBuilder fulfillmentGroupFilterBuilder;
	protected Label stepFGLabel;
	protected RadioGroupItem fgRuleRadio;
	protected DynamicForm stepFGForm;
	protected Label stepItemLabel;
	protected DynamicForm stepItemForm;
	protected RadioGroupItem itemRuleRadio;
	protected List<ItemBuilderDisplay> itemBuilderViews = new ArrayList<ItemBuilderDisplay>();
	protected Button addItemButton;
	protected ImgButton helpButtonBogo;
	protected RadioGroupItem bogoRadio;
	protected ItemBuilderDisplay targetItemBuilder;
	
	public OfferView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		DataSource orderDataSource = additionalDataSources[0]; 
		DataSource orderItemDataSource = additionalDataSources[1];
		DataSource fulfillmentGroupDataSource = additionalDataSources[2];
		DataSource customerDataSource = additionalDataSources[3];
		
		VLayout leftVerticalLayout = new VLayout(10);
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView("Promotions", entityDataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);
        
        VLayout rightVerticalLayout = new VLayout();
        rightVerticalLayout.setHeight100();
        rightVerticalLayout.setWidth("60%");
        dynamicFormDisplay = new DynamicFormView("Offer Details", entityDataSource);
        
        dynamicFormDisplay.getToolbar().addFill();
        advancedButton = new ToolStripButton("Advanced Criteria");
        advancedButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/settings.png");   
        advancedButton.setActionType(SelectionType.CHECKBOX);
        advancedButton.setDisabled(true);
        dynamicFormDisplay.getToolbar().addMember(advancedButton);
        
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).setLayoutLeftMargin(10);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).setLayoutTopMargin(10);
        
        Label step1Label = new Label("1. Enter Basic Promotion Information");
        step1Label.setHeight(30);
        step1Label.setBackgroundColor("#eaeaea");
        step1Label.setStyleName("label-bold");
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(step1Label, 0);
        
        HStack hStack1 = new HStack(10);
        hStack1.setWidth100();
        hStack1.setHeight(30);
        Label step2Label = new Label("2. How Should Customers Obtain This Offer?");
        step2Label.setWrap(false);
        step2Label.setHeight(30);
        step2Label.setBackgroundColor("#eaeaea");
        step2Label.setStyleName("label-bold");
        hStack1.addMember(step2Label);
        VStack helpButton1Stack = new VStack();
        helpButton1Stack.setAlign(VerticalAlignment.CENTER);
        helpButtonType = new ImgButton();
        helpButtonType.setSrc(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/help.png");
        helpButtonType.setWidth(16);
        helpButtonType.setHeight(16);
        helpButtonType.setShowRollOver(false);
        helpButtonType.setShowDownIcon(false);
        helpButtonType.setShowDown(false);
        helpButton1Stack.addMember(helpButtonType);
        hStack1.addMember(helpButton1Stack);
        
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(hStack1);
        
        DynamicForm step2Form = new DynamicForm();
        step2Form.setNumCols(6);
        deliveryTypeRadio = new RadioGroupItem();   
        deliveryTypeRadio.setShowTitle(false);
        deliveryTypeRadio.setWrap(false);
        deliveryTypeRadio.setDefaultValue("AUTOMATIC");
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("AUTOMATIC", "Automatic");
        valueMap.put("CODE", "Shared Code");
        valueMap.put("LIMITED-USE", "Limited-Use Code");
        valueMap.put("MANUAL", "System");
        deliveryTypeRadio.setValueMap(valueMap);
        codeField = new TextItem();
        codeField.setTitle("Offer Code");
        codeField.setWrapTitle(false);
        codeField.setDisabled(true);
        maxUseField = new IntegerItem();
        maxUseField.setTitle("Max Uses");
        maxUseField.setWrapTitle(false);
        maxUseField.setDisabled(true);
        step2Form.setFields(deliveryTypeRadio, codeField, maxUseField);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(step2Form);
        
        Label step3Label = new Label("3. Which Customers Should Receive This Offer?");
        step3Label.setWrap(false);
        step3Label.setHeight(30);
        step3Label.setBackgroundColor("#eaeaea");
        step3Label.setStyleName("label-bold");
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(step3Label);
        
        DynamicForm step3Form = new DynamicForm();
        customerRuleRadio = new RadioGroupItem();   
        customerRuleRadio.setShowTitle(false);
        customerRuleRadio.setWrap(false);
        customerRuleRadio.setDefaultValue("ALL");
        LinkedHashMap<String, String> valueMap3 = new LinkedHashMap<String, String>();
        valueMap3.put("ALL", "All Customers");
        valueMap3.put("CUSTOMER_RULE", "Build A Customer Selection Rule");
        customerRuleRadio.setValueMap(valueMap3);
        step3Form.setFields(customerRuleRadio);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(step3Form);
        
        customerFilterBuilder = new FilterBuilder();  
        customerFilterBuilder.setDataSource(customerDataSource);
        customerFilterBuilder.setDisabled(true);
        customerFilterBuilder.setLayoutBottomMargin(10);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(customerFilterBuilder);
        
        stepItemLabel = new Label("4. What Items Are Required To Receive This Offer?");
        stepItemLabel.setWrap(false);
        stepItemLabel.setHeight(30);
        stepItemLabel.setBackgroundColor("#eaeaea");
        stepItemLabel.setStyleName("label-bold");
        stepItemLabel.setVisible(true);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(stepItemLabel);
        
        stepItemForm = new DynamicForm();
        itemRuleRadio = new RadioGroupItem();   
        itemRuleRadio.setShowTitle(false);
        itemRuleRadio.setWrap(false);
        itemRuleRadio.setDefaultValue("NONE");
        LinkedHashMap<String, String> valueMapItem = new LinkedHashMap<String, String>();
        valueMapItem.put("NONE", "None");
        valueMapItem.put("ITEM_RULE", "Build Item Selection Rules");
        itemRuleRadio.setValueMap(valueMapItem);
        stepItemForm.setFields(itemRuleRadio);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(stepItemForm);
        itemBuilderViews.add(new ItemBuilderView(orderItemDataSource));
        
        HLayout buttonLayout = new HLayout();
        buttonLayout.setWidth100();
        buttonLayout.setAlign(Alignment.LEFT);
        buttonLayout.setHeight(30);
        buttonLayout.setLayoutTopMargin(15);
        addItemButton = new Button();
        addItemButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/actions/add.png");
        addItemButton.setTitle("Add New Item Rule");
        addItemButton.setWidth(136);
        addItemButton.setWrap(false);
        addItemButton.setDisabled(true);
        buttonLayout.addMember(addItemButton);
        buttonLayout.setLayoutBottomMargin(10);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(buttonLayout);
        
        for (ItemBuilderDisplay widget : itemBuilderViews) {
        	((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember((ItemBuilderView) widget);
        }
        
        HStack hStackBogo = new HStack(10);
        hStackBogo.setWidth100();
        hStackBogo.setHeight(30);
        Label stepBogoLabel = new Label("5. Is This A Buy One/Get One Style Offer?");
        stepBogoLabel.setWrap(false);
        stepBogoLabel.setHeight(30);
        stepBogoLabel.setBackgroundColor("#eaeaea");
        stepBogoLabel.setStyleName("label-bold");
        hStackBogo.addMember(stepBogoLabel);
        VStack helpButtonBogoStack = new VStack();
        helpButtonBogoStack.setAlign(VerticalAlignment.CENTER);
        helpButtonBogo = new ImgButton();
        helpButtonBogo.setSrc(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/help.png");
        helpButtonBogo.setWidth(16);
        helpButtonBogo.setHeight(16);
        helpButtonBogo.setShowRollOver(false);
        helpButtonBogo.setShowDownIcon(false);
        helpButtonBogo.setShowDown(false);
        helpButtonBogoStack.addMember(helpButtonBogo);
        hStackBogo.addMember(helpButtonBogoStack);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(hStackBogo);
        
        DynamicForm stepBogoForm = new DynamicForm();
        bogoRadio = new RadioGroupItem();   
        bogoRadio.setShowTitle(false);
        bogoRadio.setWrap(false);
        bogoRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMapBogo = new LinkedHashMap<String, String>();
        valueMapBogo.put("NO", "No");
        valueMapBogo.put("YES", "Yes");
        bogoRadio.setValueMap(valueMapBogo);
        stepBogoForm.setFields(bogoRadio);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(stepBogoForm);
        
        Label stepBogoTargetLabel = new Label("What Items Get The Discount?");
        stepBogoTargetLabel.setWrap(false);
        stepBogoTargetLabel.setHeight(30);
        stepBogoTargetLabel.setBackgroundColor("#eaeaea");
        stepBogoTargetLabel.setDisabled(true);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(stepBogoTargetLabel);
        
        targetItemBuilder = new ItemBuilderView(orderItemDataSource);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember((ItemBuilderView) targetItemBuilder);
        
        stepFGLabel = new Label("6. Which Fulfillment Groups Are Eligible To Receive The Discount?");
        stepFGLabel.setWrap(false);
        stepFGLabel.setHeight(30);
        stepFGLabel.setBackgroundColor("#eaeaea");
        stepFGLabel.setStyleName("label-bold");
        stepFGLabel.setVisible(false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(stepFGLabel);
        
        stepFGForm = new DynamicForm();
        fgRuleRadio = new RadioGroupItem();   
        fgRuleRadio.setShowTitle(false);
        fgRuleRadio.setWrap(false);
        fgRuleRadio.setDefaultValue("ALL");
        LinkedHashMap<String, String> valueMapFG = new LinkedHashMap<String, String>();
        valueMapFG.put("ALL", "All Fulfillment Groups");
        valueMapFG.put("FG_RULE", "Build A Fulfillment Group Selection Rule");
        fgRuleRadio.setValueMap(valueMapFG);
        stepFGForm.setFields(fgRuleRadio);
        stepFGForm.setVisible(false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(stepFGForm);
        fulfillmentGroupFilterBuilder = new FilterBuilder();  
        fulfillmentGroupFilterBuilder.setDataSource(fulfillmentGroupDataSource);
        fulfillmentGroupFilterBuilder.setDisabled(true);
        fulfillmentGroupFilterBuilder.setVisible(false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(fulfillmentGroupFilterBuilder);
        		
        rightVerticalLayout.addMember(dynamicFormDisplay);
        
        addMember(leftVerticalLayout);
        addMember(rightVerticalLayout);
	}

	public Canvas asCanvas() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.admin.client.view.promotion.OfferDisplay#getDynamicFormDisplay()
	 */
	public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.admin.client.view.promotion.OfferDisplay#getListDisplay()
	 */
	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

	public ToolStripButton getAdvancedButton() {
		return advancedButton;
	}

	public ImgButton getHelpButtonType() {
		return helpButtonType;
	}

	public RadioGroupItem getDeliveryTypeRadio() {
		return deliveryTypeRadio;
	}

	public TextItem getCodeField() {
		return codeField;
	}

	public IntegerItem getMaxUseField() {
		return maxUseField;
	}

	public FilterBuilder getCustomerFilterBuilder() {
		return customerFilterBuilder;
	}

	public RadioGroupItem getCustomerRuleRadio() {
		return customerRuleRadio;
	}

	public FilterBuilder getFulfillmentGroupFilterBuilder() {
		return fulfillmentGroupFilterBuilder;
	}

	public Label getStepFGLabel() {
		return stepFGLabel;
	}

	public RadioGroupItem getFgRuleRadio() {
		return fgRuleRadio;
	}

	public DynamicForm getStepFGForm() {
		return stepFGForm;
	}

	public Label getStepItemLabel() {
		return stepItemLabel;
	}

	public Button getAddItemButton() {
		return addItemButton;
	}

	public RadioGroupItem getItemRuleRadio() {
		return itemRuleRadio;
	}

	public List<ItemBuilderDisplay> getItemBuilderViews() {
		return itemBuilderViews;
	}

	public ImgButton getHelpButtonBogo() {
		return helpButtonBogo;
	}
	
}
