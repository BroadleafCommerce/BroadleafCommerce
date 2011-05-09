package org.broadleafcommerce.gwt.admin.client.view.promotion.offer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
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
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class OfferView extends HLayout implements Instantiable, OfferDisplay {
	
	protected DynamicForm stepFGForm;
	protected DynamicForm stepItemForm;
	protected DynamicForm stepBogoForm;
	protected DynamicForm orderCombineForm;
	protected DynamicForm rawCustomerForm;
	protected DynamicForm rawOrderForm;
	protected DynamicForm rawFGForm;
	protected DynamicForm restrictForm;
	protected DynamicForm customerObtainForm;
	protected DynamicForm whichCustomerForm;
	protected DynamicForm orderForm;
	protected DynamicForm receiveFromAnotherPromoForm;
	protected DynamicForm qualifyForAnotherPromoForm;
	protected DynamicForm receiveFromAnotherPromoTargetForm;
	protected DynamicForm qualifyForAnotherPromoTargetForm;
	protected DynamicForm stepFGCombineForm;
	
	protected VLayout itemBuilderContainerLayout;
	protected DynamicFormView dynamicFormDisplay;
	protected DynamicEntityListView listDisplay;
	protected ToolStripButton advancedButton;
	protected ImgButton helpButtonType;
	protected RadioGroupItem deliveryTypeRadio;
	protected TextItem codeField;
	protected RadioGroupItem customerRuleRadio;
	protected FilterBuilder customerFilterBuilder;
	protected FilterBuilder fulfillmentGroupFilterBuilder;
	protected Label stepFGLabel;
	protected RadioGroupItem fgRuleRadio;
	protected Label requiredItemsLabel;
	protected RadioGroupItem itemRuleRadio;
	protected List<ItemBuilderDisplay> itemBuilderViews = new ArrayList<ItemBuilderDisplay>();
	protected Button addItemButton;
	protected ImgButton helpButtonBogo;
	protected RadioGroupItem bogoRadio;
	protected ItemBuilderDisplay targetItemBuilder;
	protected Label targetItemsLabel;
	protected Label bogoQuestionLabel;
	protected VLayout bogoQuestionLayout;
	protected VLayout fgQuestionLayout;
	protected RadioGroupItem orderRuleRadio;
	protected FilterBuilder orderFilterBuilder;
	protected VLayout requiredItemsLayout;
	protected VLayout targetItemsLayout;
	protected VLayout newItemBuilderLayout;
	protected VLayout orderItemLayout;
	protected SectionView fgSectionView;
	protected RadioGroupItem receiveFromAnotherPromoRadio;
	protected SectionView itemTargetSectionView;
	protected RadioGroupItem qualifyForAnotherPromoRadio;
	protected RadioGroupItem receiveFromAnotherPromoTargetRadio;
	protected RadioGroupItem qualifyForAnotherPromoTargetRadio;
	protected VLayout advancedItemCriteriaTarget;
	protected VLayout advancedItemCriteria;
	protected RadioGroupItem fgCombineRuleRadio;
	protected Label orderCombineLabel;
	protected RadioGroupItem orderCombineRuleRadio;
	protected RadioGroupItem restrictRuleRadio;
	protected SectionView restrictionSectionView;
	protected SectionView itemQualificationSectionView;
	
	protected TextAreaItem rawCustomerTextArea;
	protected TextAreaItem rawOrderTextArea;
	protected TextAreaItem rawFGTextArea;
	
	public OfferView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		DataSource orderDataSource = additionalDataSources[0]; 
		final DataSource orderItemDataSource = additionalDataSources[1];
		DataSource fulfillmentGroupDataSource = additionalDataSources[2];
		DataSource customerDataSource = additionalDataSources[3];
		
		VLayout leftVerticalLayout = new VLayout(10);
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("30%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView(AdminModule.ADMINMESSAGES.promotionsListTitle(), entityDataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);
        
        VLayout rightVerticalLayout = new VLayout();
        rightVerticalLayout.setHeight100();
        rightVerticalLayout.setWidth("70%");
        dynamicFormDisplay = new DynamicFormView(AdminModule.ADMINMESSAGES.promotionDetailsTitle(), entityDataSource);
        
        dynamicFormDisplay.getToolbar().addFill();
        advancedButton = new ToolStripButton(AdminModule.ADMINMESSAGES.advancedCriteriaButtonTitle());
        advancedButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/settings.png");   
        advancedButton.setActionType(SelectionType.CHECKBOX);
        advancedButton.setDisabled(true);
        dynamicFormDisplay.getToolbar().addMember(advancedButton);
        
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).setLayoutLeftMargin(10);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).setLayoutTopMargin(10);
        
        Label basicInformationLabel = new Label(AdminModule.ADMINMESSAGES.basicPromotionLabel());
        basicInformationLabel.setHeight(30);
        basicInformationLabel.setBackgroundColor("#eaeaea");
        basicInformationLabel.setStyleName("label-bold");
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(basicInformationLabel, 0);
        
        VLayout restrictLayout = new VLayout();
        restrictLayout.setLayoutLeftMargin(10);
        Label restrictLabel = new Label(AdminModule.ADMINMESSAGES.restrictOnlyPromotionLabel());
        restrictLabel.setWrap(false);
        restrictLabel.setHeight(30);
        restrictLabel.setBackgroundColor("#eaeaea");
        restrictLabel.setStyleName("label-bold");
        restrictLayout.addMember(restrictLabel);
        
        restrictForm = new DynamicForm();
        restrictRuleRadio = new RadioGroupItem();   
        restrictRuleRadio.setShowTitle(false);
        restrictRuleRadio.setWrap(false);
        restrictRuleRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> restrictMap = new LinkedHashMap<String, String>();
        restrictMap.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        restrictMap.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        restrictRuleRadio.setValueMap(restrictMap);
        restrictForm.setFields(restrictRuleRadio);
        restrictLayout.addMember(restrictForm);
        
        restrictionSectionView = new SectionView(AdminModule.ADMINMESSAGES.advancedRestrictionsViewTitle());  
        restrictionSectionView.setVisible(false);
        restrictionSectionView.setWidth("98%");
        restrictionSectionView.getContentLayout().addMember(restrictLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(restrictionSectionView);
        
        VLayout customerLayout = new VLayout();
        customerLayout.setLayoutLeftMargin(10);
        HStack customerObtainHStack = new HStack(10);
        customerObtainHStack.setWidth100();
        customerObtainHStack.setHeight(30);
        Label customerObtainLabel = new Label(AdminModule.ADMINMESSAGES.customerObtainLabel());
        customerObtainLabel.setWrap(false);
        customerObtainLabel.setHeight(30);
        customerObtainLabel.setBackgroundColor("#eaeaea");
        customerObtainLabel.setStyleName("label-bold");
        customerObtainHStack.addMember(customerObtainLabel);
        VStack helpCustomerObtainVStack = new VStack();
        helpCustomerObtainVStack.setAlign(VerticalAlignment.CENTER);
        helpButtonType = new ImgButton();
        helpButtonType.setSrc(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/help.png");
        helpButtonType.setWidth(16);
        helpButtonType.setHeight(16);
        helpCustomerObtainVStack.addMember(helpButtonType);
        customerObtainHStack.addMember(helpCustomerObtainVStack);
        customerLayout.addMember(customerObtainHStack);
        
        customerObtainForm = new DynamicForm();
        customerObtainForm.setNumCols(4);
        deliveryTypeRadio = new RadioGroupItem();   
        deliveryTypeRadio.setShowTitle(false);
        deliveryTypeRadio.setWrap(false);
        deliveryTypeRadio.setDisabled(true);
        deliveryTypeRadio.setDefaultValue("AUTOMATIC");
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("AUTOMATIC", AdminModule.ADMINMESSAGES.deliveryTypeEnumAutomatic());
        valueMap.put("CODE", AdminModule.ADMINMESSAGES.deliveryTypeEnumCode());
        valueMap.put("MANUAL", AdminModule.ADMINMESSAGES.deliveryTypeEnumManual());
        deliveryTypeRadio.setValueMap(valueMap);
        codeField = new TextItem();
        codeField.setTitle(AdminModule.ADMINMESSAGES.offerCodeFieldTitle());
        codeField.setWrapTitle(false);
        codeField.setDisabled(true);
        customerObtainForm.setFields(deliveryTypeRadio, codeField);
        customerLayout.addMember(customerObtainForm);
        
        Label whichCustomerLabel = new Label(AdminModule.ADMINMESSAGES.whichCustomerLabel());
        whichCustomerLabel.setWrap(false);
        whichCustomerLabel.setHeight(30);
        whichCustomerLabel.setBackgroundColor("#eaeaea");
        whichCustomerLabel.setStyleName("label-bold");
        customerLayout.addMember(whichCustomerLabel);
        
        whichCustomerForm = new DynamicForm();
        customerRuleRadio = new RadioGroupItem();   
        customerRuleRadio.setShowTitle(false);
        customerRuleRadio.setWrap(false);
        customerRuleRadio.setDisabled(true);
        customerRuleRadio.setDefaultValue("ALL");
        LinkedHashMap<String, String> valueMap3 = new LinkedHashMap<String, String>();
        valueMap3.put("ALL", AdminModule.ADMINMESSAGES.allCustomerRadioChoice());
        valueMap3.put("CUSTOMER_RULE", AdminModule.ADMINMESSAGES.buildCustomerRadioChoice());
        customerRuleRadio.setValueMap(valueMap3);
        whichCustomerForm.setFields(customerRuleRadio);
        
        customerLayout.addMember(whichCustomerForm);
        
        rawCustomerForm = new DynamicForm();
        rawCustomerForm.setVisible(false);
        rawCustomerTextArea = new TextAreaItem();
        rawCustomerTextArea.setHeight(70);
        rawCustomerTextArea.setWidth("600");
        rawCustomerTextArea.setShowTitle(false);
        rawCustomerForm.setFields(rawCustomerTextArea);
        rawCustomerTextArea.setAttribute("dirty", false);
        
        customerLayout.addMember(rawCustomerForm);
        
        customerFilterBuilder = new FilterBuilder();  
        customerFilterBuilder.setDataSource(customerDataSource);
        customerFilterBuilder.setVisible(false);
        customerFilterBuilder.setLayoutBottomMargin(10);
        customerLayout.addMember(customerFilterBuilder);
        customerLayout.setLayoutBottomMargin(10);
        
        SectionView sectionStack = new SectionView(AdminModule.ADMINMESSAGES.customerSectionViewTitle());  
        sectionStack.setWidth("98%");
        sectionStack.getContentLayout().addMember(customerLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(sectionStack);
        
        VLayout orderSectionLayout = new VLayout();
        orderSectionLayout.setLayoutLeftMargin(10);
        Label orderLabel = new Label(AdminModule.ADMINMESSAGES.orderSectionLabel());
        orderLabel.setWrap(false);
        orderLabel.setHeight(30);
        orderLabel.setBackgroundColor("#eaeaea");
        orderLabel.setStyleName("label-bold");
        orderSectionLayout.addMember(orderLabel);
        
        orderForm = new DynamicForm();
        orderRuleRadio = new RadioGroupItem();   
        orderRuleRadio.setShowTitle(false);
        orderRuleRadio.setWrap(false);
        orderRuleRadio.setDisabled(true);
        orderRuleRadio.setDefaultValue("NONE");
        LinkedHashMap<String, String> orderMap = new LinkedHashMap<String, String>();
        orderMap.put("NONE", AdminModule.ADMINMESSAGES.noneOrderRadioChoice());
        orderMap.put("ORDER_RULE", AdminModule.ADMINMESSAGES.buildOrderRadioChoice());
        orderRuleRadio.setValueMap(orderMap);
        orderForm.setFields(orderRuleRadio);
        orderSectionLayout.addMember(orderForm);
        
        rawOrderForm = new DynamicForm();
        rawOrderForm.setVisible(false);
        rawOrderTextArea = new TextAreaItem();
        rawOrderTextArea.setHeight(70);
        rawOrderTextArea.setWidth("600");
        rawOrderTextArea.setShowTitle(false);
        rawOrderTextArea.setAttribute("dirty", false);
        rawOrderForm.setFields(rawOrderTextArea);
        
        orderSectionLayout.addMember(rawOrderForm);
        
        orderFilterBuilder = new FilterBuilder();  
        orderFilterBuilder.setDataSource(orderDataSource);
        orderFilterBuilder.setVisible(false);
        orderFilterBuilder.setLayoutBottomMargin(10);
        orderSectionLayout.addMember(orderFilterBuilder);
        
        orderCombineLabel = new Label(AdminModule.ADMINMESSAGES.orderCombineLabel());
        orderCombineLabel.setVisible(false);
        orderCombineLabel.setWrap(false);
        orderCombineLabel.setHeight(30);
        orderCombineLabel.setBackgroundColor("#eaeaea");
        orderCombineLabel.setStyleName("label-bold");
        orderSectionLayout.addMember(orderCombineLabel);
        
        orderCombineForm = new DynamicForm();
        orderCombineForm.setVisible(false);
        orderCombineRuleRadio = new RadioGroupItem();   
        orderCombineRuleRadio.setShowTitle(false);
        orderCombineRuleRadio.setWrap(false);
        orderCombineRuleRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> orderCombineMap = new LinkedHashMap<String, String>();
        orderCombineMap.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        orderCombineMap.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        orderCombineRuleRadio.setValueMap(orderCombineMap);
        orderCombineForm.setFields(orderCombineRuleRadio);
        orderSectionLayout.addMember(orderCombineForm);
        
        SectionView sectionStack2 = new SectionView(AdminModule.ADMINMESSAGES.orderQualificationSectionViewTitle());
        sectionStack2.setWidth("98%");
        sectionStack2.getContentLayout().addMember(orderSectionLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(sectionStack2);
        
        VStack itemSectionLayout = new VStack();
        itemSectionLayout.setLayoutLeftMargin(10);
        bogoQuestionLayout = new VLayout();
        bogoQuestionLayout.setVisible(false);
        HStack hStackBogo = new HStack(10);
        hStackBogo.setWidth100();
        hStackBogo.setHeight(30);
        bogoQuestionLabel = new Label(AdminModule.ADMINMESSAGES.bogoQuestionLabel());
        bogoQuestionLabel.setWrap(false);
        bogoQuestionLabel.setHeight(30);
        bogoQuestionLabel.setBackgroundColor("#eaeaea");
        bogoQuestionLabel.setStyleName("label-bold");
        hStackBogo.addMember(bogoQuestionLabel);
        VStack helpButtonBogoStack = new VStack();
        helpButtonBogoStack.setAlign(VerticalAlignment.CENTER);
        helpButtonBogo = new ImgButton();
        helpButtonBogo.setSrc(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/help.png");
        helpButtonBogo.setWidth(16);
        helpButtonBogo.setHeight(16);
        helpButtonBogoStack.addMember(helpButtonBogo);
        hStackBogo.addMember(helpButtonBogoStack);
        bogoQuestionLayout.addMember(hStackBogo);
        
        stepBogoForm = new DynamicForm();
        bogoRadio = new RadioGroupItem();   
        bogoRadio.setShowTitle(false);
        bogoRadio.setWrap(false);
        bogoRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMapBogo = new LinkedHashMap<String, String>();
        valueMapBogo.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        valueMapBogo.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        bogoRadio.setValueMap(valueMapBogo);
        stepBogoForm.setFields(bogoRadio);
        bogoQuestionLayout.addMember(stepBogoForm);
        itemSectionLayout.addMember(bogoQuestionLayout);
        
        requiredItemsLayout = new VLayout();
        requiredItemsLayout.setVisible(false);
        requiredItemsLabel = new Label(AdminModule.ADMINMESSAGES.requiredItemsLabel());
        requiredItemsLabel.setWrap(false);
        requiredItemsLabel.setHeight(30);
        requiredItemsLabel.setBackgroundColor("#eaeaea");
        requiredItemsLabel.setStyleName("label-bold");
        requiredItemsLayout.addMember(requiredItemsLabel);
        
        orderItemLayout = new VLayout();
        orderItemLayout.setVisible(false);
        stepItemForm = new DynamicForm();
        itemRuleRadio = new RadioGroupItem();   
        itemRuleRadio.setShowTitle(false);
        itemRuleRadio.setWrap(false);
        itemRuleRadio.setDefaultValue("NONE");
        LinkedHashMap<String, String> valueMapItem = new LinkedHashMap<String, String>();
        valueMapItem.put("NONE", AdminModule.ADMINMESSAGES.noneItemRadioChoice());
        valueMapItem.put("ITEM_RULE", AdminModule.ADMINMESSAGES.buildItemRadioChoice());
        itemRuleRadio.setValueMap(valueMapItem);
        stepItemForm.setFields(itemRuleRadio);
        orderItemLayout.addMember(stepItemForm);
        requiredItemsLayout.addMember(orderItemLayout);
        itemBuilderViews.add(new ItemBuilderView(orderItemDataSource, true, false));
        
        newItemBuilderLayout = new VLayout();
        newItemBuilderLayout.setVisible(false);
        HLayout buttonLayout = new HLayout();
        buttonLayout.setWidth100();
        buttonLayout.setAlign(Alignment.LEFT);
        buttonLayout.setHeight(30);
        buttonLayout.setLayoutTopMargin(15);
        addItemButton = new Button();
        addItemButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/actions/add.png");
        addItemButton.setTitle(AdminModule.ADMINMESSAGES.newItemRuleButtonTitle());
        addItemButton.setWidth(136);
        addItemButton.setWrap(false);
        buttonLayout.addMember(addItemButton);
        buttonLayout.setLayoutBottomMargin(10);
        newItemBuilderLayout.addMember(buttonLayout);
        itemBuilderContainerLayout = new VLayout();
        newItemBuilderLayout.addMember(itemBuilderContainerLayout);
        for (ItemBuilderDisplay widget : itemBuilderViews) {
        	itemBuilderContainerLayout.addMember((ItemBuilderView) widget);
        }
        requiredItemsLayout.addMember(newItemBuilderLayout);
        
        advancedItemCriteria = new VLayout();
        advancedItemCriteria.setVisible(false);
        newItemBuilderLayout.addMember(advancedItemCriteria);
        Label receiveFromAnotherPromoLabel = new Label(AdminModule.ADMINMESSAGES.receiveFromAnotherPromoLabel());
        receiveFromAnotherPromoLabel.setWrap(false);
        receiveFromAnotherPromoLabel.setHeight(30);
        receiveFromAnotherPromoLabel.setBackgroundColor("#eaeaea");
        advancedItemCriteria.addMember(receiveFromAnotherPromoLabel);
        
        receiveFromAnotherPromoForm = new DynamicForm();
        receiveFromAnotherPromoRadio = new RadioGroupItem();   
        receiveFromAnotherPromoRadio.setShowTitle(false);
        receiveFromAnotherPromoRadio.setWrap(false);
        receiveFromAnotherPromoRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMap4 = new LinkedHashMap<String, String>();
        valueMap4.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        valueMap4.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        receiveFromAnotherPromoRadio.setValueMap(valueMap4);
        receiveFromAnotherPromoForm.setFields(receiveFromAnotherPromoRadio);
        advancedItemCriteria.addMember(receiveFromAnotherPromoForm);
        
        Label qualifiyForAnotherPromoLabel = new Label(AdminModule.ADMINMESSAGES.qualifiyForAnotherPromoLabel());
        qualifiyForAnotherPromoLabel.setWrap(false);
        qualifiyForAnotherPromoLabel.setHeight(30);
        qualifiyForAnotherPromoLabel.setBackgroundColor("#eaeaea");
        advancedItemCriteria.addMember(qualifiyForAnotherPromoLabel);
        
        qualifyForAnotherPromoForm = new DynamicForm();
        qualifyForAnotherPromoRadio = new RadioGroupItem();   
        qualifyForAnotherPromoRadio.setShowTitle(false);
        qualifyForAnotherPromoRadio.setWrap(false);
        qualifyForAnotherPromoRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMap5 = new LinkedHashMap<String, String>();
        valueMap5.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        valueMap5.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        qualifyForAnotherPromoRadio.setValueMap(valueMap5);
        qualifyForAnotherPromoForm.setFields(qualifyForAnotherPromoRadio);
        advancedItemCriteria.addMember(qualifyForAnotherPromoForm);
        
        itemSectionLayout.addMember(requiredItemsLayout);
        itemSectionLayout.setLayoutBottomMargin(10);
        
        itemQualificationSectionView = new SectionView(AdminModule.ADMINMESSAGES.itemQualificationSectionTitle());  
        itemQualificationSectionView.setWidth("98%");
        itemQualificationSectionView.getContentLayout().addMember(itemSectionLayout);
        itemQualificationSectionView.setVisible(false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(itemQualificationSectionView);
        
        targetItemsLayout = new VLayout();
        targetItemsLayout.setLayoutLeftMargin(10);
        targetItemsLabel = new Label(AdminModule.ADMINMESSAGES.targetItemsLabel());
        targetItemsLabel.setWrap(false);
        targetItemsLabel.setHeight(30);
        targetItemsLabel.setBackgroundColor("#eaeaea");
        targetItemsLabel.setStyleName("label-bold");
        targetItemsLayout.addMember(targetItemsLabel);
        
        targetItemBuilder = new ItemBuilderView(orderItemDataSource, false, true);
        targetItemsLayout.addMember((ItemBuilderView) targetItemBuilder);
        targetItemsLayout.setLayoutBottomMargin(10);
        
        advancedItemCriteriaTarget = new VLayout();
        advancedItemCriteriaTarget.setVisible(false);
        targetItemsLayout.addMember(advancedItemCriteriaTarget);
        Label receiveFromAnotherPromoTargetLabel = new Label(AdminModule.ADMINMESSAGES.receiveFromAnotherPromoTargetLabel());
        receiveFromAnotherPromoTargetLabel.setWrap(false);
        receiveFromAnotherPromoTargetLabel.setHeight(30);
        receiveFromAnotherPromoTargetLabel.setBackgroundColor("#eaeaea");
        advancedItemCriteriaTarget.addMember(receiveFromAnotherPromoTargetLabel);
        
        receiveFromAnotherPromoTargetForm = new DynamicForm();
        receiveFromAnotherPromoTargetRadio = new RadioGroupItem();   
        receiveFromAnotherPromoTargetRadio.setShowTitle(false);
        receiveFromAnotherPromoTargetRadio.setWrap(false);
        receiveFromAnotherPromoTargetRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMap6 = new LinkedHashMap<String, String>();
        valueMap6.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        valueMap6.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        receiveFromAnotherPromoTargetRadio.setValueMap(valueMap6);
        receiveFromAnotherPromoTargetForm.setFields(receiveFromAnotherPromoTargetRadio);
        advancedItemCriteriaTarget.addMember(receiveFromAnotherPromoTargetForm);
        
        Label qualifiyForAnotherPromoTargetLabel = new Label(AdminModule.ADMINMESSAGES.qualifiyForAnotherPromoTargetLabel());
        qualifiyForAnotherPromoTargetLabel.setWrap(false);
        qualifiyForAnotherPromoTargetLabel.setHeight(30);
        qualifiyForAnotherPromoTargetLabel.setBackgroundColor("#eaeaea");
        advancedItemCriteriaTarget.addMember(qualifiyForAnotherPromoTargetLabel);
        
        qualifyForAnotherPromoTargetForm = new DynamicForm();
        qualifyForAnotherPromoTargetRadio = new RadioGroupItem();   
        qualifyForAnotherPromoTargetRadio.setShowTitle(false);
        qualifyForAnotherPromoTargetRadio.setWrap(false);
        qualifyForAnotherPromoTargetRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMap7 = new LinkedHashMap<String, String>();
        valueMap7.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        valueMap7.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        qualifyForAnotherPromoTargetRadio.setValueMap(valueMap7);
        qualifyForAnotherPromoTargetForm.setFields(qualifyForAnotherPromoTargetRadio);
        advancedItemCriteriaTarget.addMember(qualifyForAnotherPromoTargetForm);
        
        itemTargetSectionView = new SectionView(AdminModule.ADMINMESSAGES.itemTargetSectionTitle());  
        itemTargetSectionView.setVisible(false);
        itemTargetSectionView.setWidth("98%");
        itemTargetSectionView.getContentLayout().addMember(targetItemsLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(itemTargetSectionView);
        
        fgQuestionLayout = new VLayout();
        fgQuestionLayout.setLayoutLeftMargin(10);
        stepFGLabel = new Label(AdminModule.ADMINMESSAGES.stepFGLabel());
        stepFGLabel.setWrap(false);
        stepFGLabel.setHeight(30);
        stepFGLabel.setBackgroundColor("#eaeaea");
        stepFGLabel.setStyleName("label-bold");
        fgQuestionLayout.addMember(stepFGLabel);
        
        stepFGForm = new DynamicForm();
        fgRuleRadio = new RadioGroupItem();   
        fgRuleRadio.setShowTitle(false);
        fgRuleRadio.setWrap(false);
        fgRuleRadio.setDefaultValue("ALL");
        LinkedHashMap<String, String> valueMapFG = new LinkedHashMap<String, String>();
        valueMapFG.put("ALL", AdminModule.ADMINMESSAGES.allFGRadioChoice());
        valueMapFG.put("FG_RULE", AdminModule.ADMINMESSAGES.buildFGRadioChoice());
        fgRuleRadio.setValueMap(valueMapFG);
        stepFGForm.setFields(fgRuleRadio);
        fgQuestionLayout.addMember(stepFGForm);
        fulfillmentGroupFilterBuilder = new FilterBuilder();  
        fulfillmentGroupFilterBuilder.setDataSource(fulfillmentGroupDataSource);
        fulfillmentGroupFilterBuilder.setVisible(false);
        fgQuestionLayout.addMember(fulfillmentGroupFilterBuilder);
        fgQuestionLayout.setLayoutBottomMargin(10);
        
        rawFGForm = new DynamicForm();
        rawFGForm.setVisible(false);
        rawFGTextArea = new TextAreaItem();
        rawFGTextArea.setHeight(70);
        rawFGTextArea.setWidth("600");
        rawFGTextArea.setShowTitle(false);
        rawFGTextArea.setAttribute("dirty", false);
        rawFGForm.setFields(rawFGTextArea);
        
        fgQuestionLayout.addMember(rawFGForm);
        
        Label fgCombineLabel = new Label(AdminModule.ADMINMESSAGES.fgCombineLabel());
        fgCombineLabel.setWrap(false);
        fgCombineLabel.setHeight(30);
        fgCombineLabel.setBackgroundColor("#eaeaea");
        fgCombineLabel.setStyleName("label-bold");
        fgQuestionLayout.addMember(fgCombineLabel);
        
        stepFGCombineForm = new DynamicForm();
        fgCombineRuleRadio = new RadioGroupItem();   
        fgCombineRuleRadio.setShowTitle(false);
        fgCombineRuleRadio.setWrap(false);
        fgCombineRuleRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMapCombineFG = new LinkedHashMap<String, String>();
        valueMapCombineFG.put("YES", AdminModule.ADMINMESSAGES.yesRadioChoice());
        valueMapCombineFG.put("NO", AdminModule.ADMINMESSAGES.noRadioChoice());
        fgCombineRuleRadio.setValueMap(valueMapCombineFG);
        stepFGCombineForm.setFields(fgCombineRuleRadio);
        fgQuestionLayout.addMember(stepFGCombineForm);
        
        fgSectionView = new SectionView(AdminModule.ADMINMESSAGES.fgSectionViewTitle());  
        fgSectionView.setVisible(false);
        fgSectionView.setWidth("98%");
        fgSectionView.getContentLayout().addMember(fgQuestionLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(fgSectionView);
        		
        rightVerticalLayout.addMember(dynamicFormDisplay);
        
        addMember(leftVerticalLayout);
        addMember(rightVerticalLayout);
	}
	
	public ItemBuilderDisplay addItemBuilder(DataSource orderItemDataSource) {
		ItemBuilderDisplay builder = new ItemBuilderView(orderItemDataSource, true, false);
		builder.enable();
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

	public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
	
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

	public Label getRequiredItemsLabel() {
		return requiredItemsLabel;
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

	public RadioGroupItem getBogoRadio() {
		return bogoRadio;
	}

	public Label getTargetItemsLabel() {
		return targetItemsLabel;
	}

	public Label getBogoQuestionLabel() {
		return bogoQuestionLabel;
	}

	public void setHelpButtonBogo(ImgButton helpButtonBogo) {
		this.helpButtonBogo = helpButtonBogo;
	}

	public ItemBuilderDisplay getTargetItemBuilder() {
		return targetItemBuilder;
	}

	public DynamicForm getStepBogoForm() {
		return stepBogoForm;
	}

	public VLayout getBogoQuestionLayout() {
		return bogoQuestionLayout;
	}

	public VLayout getFgQuestionLayout() {
		return fgQuestionLayout;
	}

	public RadioGroupItem getOrderRuleRadio() {
		return orderRuleRadio;
	}

	public FilterBuilder getOrderFilterBuilder() {
		return orderFilterBuilder;
	}

	public VLayout getRequiredItemsLayout() {
		return requiredItemsLayout;
	}

	public VLayout getTargetItemsLayout() {
		return targetItemsLayout;
	}

	public VLayout getNewItemBuilderLayout() {
		return newItemBuilderLayout;
	}

	public VLayout getOrderItemLayout() {
		return orderItemLayout;
	}

	public SectionView getFgSectionView() {
		return fgSectionView;
	}

	public RadioGroupItem getReceiveFromAnotherPromoRadio() {
		return receiveFromAnotherPromoRadio;
	}

	public SectionView getItemTargetSectionView() {
		return itemTargetSectionView;
	}

	public RadioGroupItem getQualifyForAnotherPromoRadio() {
		return qualifyForAnotherPromoRadio;
	}

	public RadioGroupItem getReceiveFromAnotherPromoTargetRadio() {
		return receiveFromAnotherPromoTargetRadio;
	}

	public RadioGroupItem getQualifyForAnotherPromoTargetRadio() {
		return qualifyForAnotherPromoTargetRadio;
	}

	public VLayout getAdvancedItemCriteriaTarget() {
		return advancedItemCriteriaTarget;
	}

	public VLayout getAdvancedItemCriteria() {
		return advancedItemCriteria;
	}

	public Label getOrderCombineLabel() {
		return orderCombineLabel;
	}

	public DynamicForm getOrderCombineForm() {
		return orderCombineForm;
	}

	public RadioGroupItem getOrderCombineRuleRadio() {
		return orderCombineRuleRadio;
	}

	public RadioGroupItem getFgCombineRuleRadio() {
		return fgCombineRuleRadio;
	}

	public RadioGroupItem getRestrictRuleRadio() {
		return restrictRuleRadio;
	}

	public SectionView getRestrictionSectionView() {
		return restrictionSectionView;
	}

	public DynamicForm getRawCustomerForm() {
		return rawCustomerForm;
	}

	public TextAreaItem getRawCustomerTextArea() {
		return rawCustomerTextArea;
	}

	public DynamicForm getRawOrderForm() {
		return rawOrderForm;
	}

	public TextAreaItem getRawOrderTextArea() {
		return rawOrderTextArea;
	}

	public DynamicForm getRawFGForm() {
		return rawFGForm;
	}

	public TextAreaItem getRawFGTextArea() {
		return rawFGTextArea;
	}

	public SectionView getItemQualificationSectionView() {
		return itemQualificationSectionView;
	}

	public DynamicForm getRestrictForm() {
		return restrictForm;
	}

	public DynamicForm getCustomerObtainForm() {
		return customerObtainForm;
	}

	public DynamicForm getWhichCustomerForm() {
		return whichCustomerForm;
	}

	public DynamicForm getOrderForm() {
		return orderForm;
	}

	public DynamicForm getReceiveFromAnotherPromoForm() {
		return receiveFromAnotherPromoForm;
	}

	public DynamicForm getQualifyForAnotherPromoForm() {
		return qualifyForAnotherPromoForm;
	}

	public DynamicForm getReceiveFromAnotherPromoTargetForm() {
		return receiveFromAnotherPromoTargetForm;
	}

	public DynamicForm getQualifyForAnotherPromoTargetForm() {
		return qualifyForAnotherPromoTargetForm;
	}

	public DynamicForm getStepFGCombineForm() {
		return stepFGCombineForm;
	}

	public DynamicForm getStepItemForm() {
		return stepItemForm;
	}
	
}
