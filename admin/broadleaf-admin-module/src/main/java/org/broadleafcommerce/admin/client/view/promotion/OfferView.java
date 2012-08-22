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

package org.broadleafcommerce.admin.client.view.promotion;

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
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.FloatRangeValidator;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.FieldDataSourceWrapper;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author jfischer
 */
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
    protected DynamicForm fgCombineForm;
    protected DynamicForm orderItemCombineForm;
    protected DynamicForm qualifyingItemSubTotalForm;

    protected VLayout customerLayout;
    protected VLayout orderSectionLayout;
    protected SectionView customerSection;
    protected SectionView orderSection;

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
    protected ImgButton helpButtonBogo;
    protected RadioGroupItem bogoRadio;
    protected Label targetItemsLabel;
    protected Label bogoQuestionLabel;
    protected VLayout bogoQuestionLayout;
    protected VLayout fgQuestionLayout;
    protected RadioGroupItem orderRuleRadio;
    protected FilterBuilder orderFilterBuilder;
    protected VLayout requiredItemsLayout;
    protected VLayout targetItemsLayout;
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
    protected Label orderItemCombineLabel;
    protected RadioGroupItem orderItemCombineRuleRadio;

    protected TextAreaItem rawCustomerTextArea;
    protected TextAreaItem rawOrderTextArea;
    protected TextAreaItem rawFGTextArea;

    protected ToolStripButton cloneButton;
    protected FloatItem qualifyingItemSubTotal;

    protected List<ItemBuilderDisplay> itemBuilderViews = new ArrayList<ItemBuilderDisplay>();
    protected List<ItemBuilderDisplay> targetItemBuilderViews = new ArrayList<ItemBuilderDisplay>();
    protected VLayout newItemBuilderLayout;
    protected VLayout newTargetItemBuilderLayout;
    protected VLayout itemBuilderContainerLayout;
    protected VLayout targetItemBuilderContainerLayout;
    protected Button addItemButton;
    protected Button targetAddItemButton;


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
        leftVerticalLayout.setID("offerLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("30%");
        leftVerticalLayout.setShowResizeBar(true);

        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("promotionsListTitle"), entityDataSource, false, false);
        //listDisplay.getToolBar().addFill();
        cloneButton = new ToolStripButton();
        cloneButton.setDisabled(true);
        cloneButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/double_arrow_right.png");
        cloneButton.setPrompt(BLCMain.getMessageManager().getString("clonePromotionHelp"));
        //listDisplay.getToolBar().addButton(cloneButton);
        //listDisplay.getToolBar().addSpacer(6);
        leftVerticalLayout.addMember(listDisplay);

        VLayout rightVerticalLayout = new VLayout();
        rightVerticalLayout.setID("offerRightVerticalLayout");
        rightVerticalLayout.setHeight100();
        rightVerticalLayout.setWidth("70%");
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("promotionDetailsTitle"), entityDataSource);

        dynamicFormDisplay.getToolbar().addFill();
        advancedButton = new ToolStripButton(BLCMain.getMessageManager().getString("advancedCriteriaButtonTitle"));
        advancedButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/settings.png");
        advancedButton.setActionType(SelectionType.CHECKBOX);
        advancedButton.setDisabled(true);
        dynamicFormDisplay.getToolbar().addMember(advancedButton);

        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).setLayoutLeftMargin(10);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).setLayoutTopMargin(10);


        VLayout restrictLayout = new VLayout();
        restrictLayout.setID("offerRestrictLayout");
        restrictLayout.setLayoutLeftMargin(10);
        Label restrictLabel = new Label(BLCMain.getMessageManager().getString("restrictOnlyPromotionLabel"));
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
        restrictMap.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        restrictMap.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        restrictRuleRadio.setValueMap(restrictMap);
        restrictForm.setFields(restrictRuleRadio);
        restrictLayout.addMember(restrictForm);

        restrictionSectionView = new SectionView(BLCMain.getMessageManager().getString("advancedRestrictionsViewTitle"));
        restrictionSectionView.setVisible(false);
        restrictionSectionView.setWidth("98%");
        restrictionSectionView.getContentLayout().addMember(restrictLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(restrictionSectionView);

        customerLayout = new VLayout();
        customerLayout.setVisible(false);
        customerLayout.setID("offerCustomerLayout");
        customerLayout.setLayoutLeftMargin(10);
        HStack customerObtainHStack = new HStack(10);
        customerObtainHStack.setID("offerCustomerObtainHStack");
        customerObtainHStack.setWidth100();
        customerObtainHStack.setHeight(30);
        Label customerObtainLabel = new Label(BLCMain.getMessageManager().getString("customerObtainLabel"));
        customerObtainLabel.setWrap(false);
        customerObtainLabel.setHeight(30);
        customerObtainLabel.setBackgroundColor("#eaeaea");
        customerObtainLabel.setStyleName("label-bold");
        customerObtainHStack.addMember(customerObtainLabel);
        VStack helpCustomerObtainVStack = new VStack();
        helpCustomerObtainVStack.setID("offerHelpCustomerObtainVStack");
        helpCustomerObtainVStack.setAlign(VerticalAlignment.CENTER);
        helpButtonType = new ImgButton();
        helpButtonType.setSrc(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/help.png");
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
        valueMap.put("AUTOMATIC", BLCMain.getMessageManager().getString("deliveryTypeEnumAutomatic"));
        valueMap.put("CODE", BLCMain.getMessageManager().getString("deliveryTypeEnumCode"));
        valueMap.put("MANUAL", BLCMain.getMessageManager().getString("deliveryTypeEnumManual"));
        deliveryTypeRadio.setValueMap(valueMap);
        codeField = new TextItem();
        codeField.setTitle(BLCMain.getMessageManager().getString("offerCodeFieldTitle"));
        codeField.setWrapTitle(false);
        codeField.setDisabled(true);
        customerObtainForm.setFields(deliveryTypeRadio, codeField);
        customerLayout.addMember(customerObtainForm);

        Label whichCustomerLabel = new Label(BLCMain.getMessageManager().getString("whichCustomerLabel"));
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
        valueMap3.put("ALL", BLCMain.getMessageManager().getString("allCustomerRadioChoice"));
        valueMap3.put("CUSTOMER_RULE", BLCMain.getMessageManager().getString("buildCustomerRadioChoice"));
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
        customerFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(customerDataSource));
        customerFilterBuilder.setVisible(false);
        customerFilterBuilder.setLayoutBottomMargin(10);
        customerFilterBuilder.setAllowEmpty(true);
        customerFilterBuilder.setValidateOnChange(false);
        customerLayout.addMember(customerFilterBuilder);
        customerLayout.setLayoutBottomMargin(10);

        customerSection = new SectionView(BLCMain.getMessageManager().getString("customerSectionViewTitle"));
        customerSection.setVisible(false);
        customerSection.setID("offerSectionStack");
        customerSection.setWidth("98%");
        customerSection.getContentLayout().addMember(customerLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(customerSection);

        orderSectionLayout = new VLayout();
        orderSectionLayout.setVisible(false);
        orderSectionLayout.setID("offerOrderSectionLayout");
        orderSectionLayout.setLayoutLeftMargin(10);
        Label orderLabel = new Label(BLCMain.getMessageManager().getString("orderSectionLabel"));
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
        orderMap.put("NONE", BLCMain.getMessageManager().getString("noneOrderRadioChoice"));
        orderMap.put("ORDER_RULE", BLCMain.getMessageManager().getString("buildOrderRadioChoice"));
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
        orderFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(orderDataSource));
        orderFilterBuilder.setVisible(false);
        orderFilterBuilder.setLayoutBottomMargin(10);
        orderFilterBuilder.setAllowEmpty(true);
        orderFilterBuilder.setValidateOnChange(false);
        orderSectionLayout.addMember(orderFilterBuilder);

        orderCombineLabel = new Label(BLCMain.getMessageManager().getString("orderCombineLabel"));
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
        orderCombineMap.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        orderCombineMap.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        orderCombineRuleRadio.setValueMap(orderCombineMap);
        orderCombineForm.setFields(orderCombineRuleRadio);
        orderSectionLayout.addMember(orderCombineForm);

        orderSection = new SectionView(BLCMain.getMessageManager().getString("orderQualificationSectionViewTitle"));
        orderSection.setVisible(false);
        orderSection.setID("offerSectionStack2");
        orderSection.setWidth("98%");
        orderSection.getContentLayout().addMember(orderSectionLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(orderSection);

        VStack itemSectionLayout = new VStack();
        itemSectionLayout.setID("offerItemSectionLayout");
        itemSectionLayout.setLayoutLeftMargin(10);

        orderItemCombineLabel = new Label(BLCMain.getMessageManager().getString("orderItemCombineLabel"));
        orderItemCombineLabel.setVisible(false);
        orderItemCombineLabel.setWrap(false);
        orderItemCombineLabel.setHeight(30);
        orderItemCombineLabel.setBackgroundColor("#eaeaea");
        orderItemCombineLabel.setStyleName("label-bold");
        itemSectionLayout.addMember(orderItemCombineLabel);

        orderItemCombineForm = new DynamicForm();
        orderItemCombineForm.setVisible(false);
        orderItemCombineRuleRadio = new RadioGroupItem();
        orderItemCombineRuleRadio.setShowTitle(false);
        orderItemCombineRuleRadio.setWrap(false);
        orderItemCombineRuleRadio.setDefaultValue("YES");
        LinkedHashMap<String, String> orderItemCombineMap = new LinkedHashMap<String, String>();
        orderItemCombineMap.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        orderItemCombineMap.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        orderItemCombineRuleRadio.setValueMap(orderItemCombineMap);
        orderItemCombineForm.setFields(orderItemCombineRuleRadio);
        itemSectionLayout.addMember(orderItemCombineForm);

        bogoQuestionLayout = new VLayout();
        bogoQuestionLayout.setVisible(false);
        HStack hStackBogo = new HStack(10);
        hStackBogo.setID("offerHStackBogo");
        hStackBogo.setWidth100();
        hStackBogo.setHeight(30);
        bogoQuestionLabel = new Label(BLCMain.getMessageManager().getString("bogoQuestionLabel"));
        bogoQuestionLabel.setWrap(false);
        bogoQuestionLabel.setHeight(30);
        bogoQuestionLabel.setBackgroundColor("#eaeaea");
        bogoQuestionLabel.setStyleName("label-bold");
        hStackBogo.addMember(bogoQuestionLabel);
        VStack helpButtonBogoStack = new VStack();
        helpButtonBogoStack.setID("offerHelpButtonBogoStack");
        helpButtonBogoStack.setAlign(VerticalAlignment.CENTER);
        helpButtonBogo = new ImgButton();
        helpButtonBogo.setSrc(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/help.png");
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
        valueMapBogo.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMapBogo.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        bogoRadio.setValueMap(valueMapBogo);
        stepBogoForm.setFields(bogoRadio);
        bogoQuestionLayout.addMember(stepBogoForm);
        itemSectionLayout.addMember(bogoQuestionLayout);

        requiredItemsLayout = new VLayout();
        requiredItemsLayout.setVisible(false);
        requiredItemsLabel = new Label(BLCMain.getMessageManager().getString("requiredItemsLabel"));
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
        valueMapItem.put("NONE", BLCMain.getMessageManager().getString("noneItemRadioChoice"));
        valueMapItem.put("ITEM_RULE", BLCMain.getMessageManager().getString("buildItemRadioChoice"));
        itemRuleRadio.setValueMap(valueMapItem);
        stepItemForm.setFields(itemRuleRadio);
        orderItemLayout.addMember(stepItemForm);
        requiredItemsLayout.addMember(orderItemLayout);
        itemBuilderViews.add(new ItemBuilderView(orderItemDataSource, true));

        newItemBuilderLayout = new VLayout();
        newItemBuilderLayout.setVisible(false);
        HLayout buttonLayout = new HLayout();
        buttonLayout.setID("offerButtonLayout");
        buttonLayout.setWidth100();
        buttonLayout.setAlign(Alignment.LEFT);
        buttonLayout.setHeight(30);
        buttonLayout.setLayoutTopMargin(15);
        addItemButton = new Button();
        addItemButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/add.png");
        addItemButton.setTitle(BLCMain.getMessageManager().getString("newItemRuleButtonTitle"));
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
        Label receiveFromAnotherPromoLabel = new Label(BLCMain.getMessageManager().getString("receiveFromAnotherPromoLabel"));
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
        valueMap4.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap4.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        receiveFromAnotherPromoRadio.setValueMap(valueMap4);
        receiveFromAnotherPromoForm.setFields(receiveFromAnotherPromoRadio);
        advancedItemCriteria.addMember(receiveFromAnotherPromoForm);

        Label qualifiyForAnotherPromoLabel = new Label(BLCMain.getMessageManager().getString("qualifiyForAnotherPromoLabel"));
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
        valueMap5.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap5.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        qualifyForAnotherPromoRadio.setValueMap(valueMap5);
        qualifyForAnotherPromoForm.setFields(qualifyForAnotherPromoRadio);
        advancedItemCriteria.addMember(qualifyForAnotherPromoForm);

        qualifyingItemSubTotalForm = new DynamicForm();
        qualifyingItemSubTotalForm.setNumCols(2);
        //qualifyingItemSubTotal = new TextItem();
        qualifyingItemSubTotal = new FloatItem();
        qualifyingItemSubTotal.setAttribute("type", "localMoneyDecimal");
        qualifyingItemSubTotal.setTitle(BLCMain.getMessageManager().getString("qualifiyngItemSubTotal"));
        qualifyingItemSubTotal.setWrapTitle(false);
        qualifyingItemSubTotal.setDisabled(false);

        FloatRangeValidator floatRangeValidator = new FloatRangeValidator();
        floatRangeValidator.setMin(0.0f);
        qualifyingItemSubTotal.setValidators(floatRangeValidator);
        // qualifyingItemSubTotalForm.setDataSource(entityDataSource);
        qualifyingItemSubTotal.setCellStyle("label-bold");
        qualifyingItemSubTotalForm.setFields(qualifyingItemSubTotal);
        requiredItemsLayout.addMember(qualifyingItemSubTotalForm);

        itemSectionLayout.addMember(requiredItemsLayout);
        itemSectionLayout.setLayoutBottomMargin(10);

        itemQualificationSectionView = new SectionView(BLCMain.getMessageManager().getString("itemQualificationSectionTitle"));
        itemQualificationSectionView.setWidth("98%");
        itemQualificationSectionView.getContentLayout().addMember(itemSectionLayout);
        itemQualificationSectionView.setVisible(false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(itemQualificationSectionView);

        targetItemsLayout = new VLayout();
        targetItemsLayout.setLayoutLeftMargin(10);
        targetItemsLabel = new Label(BLCMain.getMessageManager().getString("targetItemsLabel"));
        targetItemsLabel.setWrap(false);
        targetItemsLabel.setHeight(30);
        targetItemsLabel.setBackgroundColor("#eaeaea");
        targetItemsLabel.setStyleName("label-bold");
        targetItemsLayout.addMember(targetItemsLabel);

        targetItemBuilderViews.add(new ItemBuilderView(orderItemDataSource, true));

        newTargetItemBuilderLayout = new VLayout();
        newTargetItemBuilderLayout.setVisible(false);
        HLayout targetButtonLayout = new HLayout();
        targetButtonLayout.setID("targetOfferButtonLayout");
        targetButtonLayout.setWidth100();
        targetButtonLayout.setAlign(Alignment.LEFT);
        targetButtonLayout.setHeight(30);
        targetButtonLayout.setLayoutTopMargin(15);
        targetAddItemButton = new Button();
        targetAddItemButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/add.png");
        targetAddItemButton.setTitle(BLCMain.getMessageManager().getString("newItemRuleButtonTitle"));
        targetAddItemButton.setWidth(136);
        targetAddItemButton.setWrap(false);
        targetButtonLayout.addMember(targetAddItemButton);
        targetButtonLayout.setLayoutBottomMargin(10);
        newTargetItemBuilderLayout.addMember(targetButtonLayout);
        targetItemBuilderContainerLayout = new VLayout();
        newTargetItemBuilderLayout.addMember(targetItemBuilderContainerLayout);
        for (ItemBuilderDisplay widget : targetItemBuilderViews) {
            targetItemBuilderContainerLayout.addMember((ItemBuilderView) widget);
        }
        targetItemsLayout.addMember(newTargetItemBuilderLayout);
        targetItemsLayout.setLayoutBottomMargin(10);

        advancedItemCriteriaTarget = new VLayout();
        advancedItemCriteriaTarget.setVisible(false);
        targetItemsLayout.addMember(advancedItemCriteriaTarget);
        Label receiveFromAnotherPromoTargetLabel = new Label(BLCMain.getMessageManager().getString("receiveFromAnotherPromoTargetLabel"));
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
        valueMap6.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap6.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        receiveFromAnotherPromoTargetRadio.setValueMap(valueMap6);
        receiveFromAnotherPromoTargetForm.setFields(receiveFromAnotherPromoTargetRadio);
        advancedItemCriteriaTarget.addMember(receiveFromAnotherPromoTargetForm);

        Label qualifiyForAnotherPromoTargetLabel = new Label(BLCMain.getMessageManager().getString("qualifiyForAnotherPromoTargetLabel"));
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
        valueMap7.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap7.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        qualifyForAnotherPromoTargetRadio.setValueMap(valueMap7);
        qualifyForAnotherPromoTargetForm.setFields(qualifyForAnotherPromoTargetRadio);
        advancedItemCriteriaTarget.addMember(qualifyForAnotherPromoTargetForm);

        itemTargetSectionView = new SectionView(BLCMain.getMessageManager().getString("itemTargetSectionTitle"));
        itemTargetSectionView.setVisible(false);
        itemTargetSectionView.setWidth("98%");
        itemTargetSectionView.getContentLayout().addMember(targetItemsLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(itemTargetSectionView);

        fgQuestionLayout = new VLayout();
        fgQuestionLayout.setLayoutLeftMargin(10);

        Label fgCombineLabel = new Label(BLCMain.getMessageManager().getString("fgCombineLabel"));
        fgCombineLabel.setWrap(false);
        fgCombineLabel.setHeight(30);
        fgCombineLabel.setBackgroundColor("#eaeaea");
        fgCombineLabel.setStyleName("label-bold");
        fgQuestionLayout.addMember(fgCombineLabel);

        fgCombineForm = new DynamicForm();
        fgCombineRuleRadio = new RadioGroupItem();
        fgCombineRuleRadio.setShowTitle(false);
        fgCombineRuleRadio.setWrap(false);
        fgCombineRuleRadio.setDefaultValue("NO");
        LinkedHashMap<String, String> valueMapCombineFG = new LinkedHashMap<String, String>();
        valueMapCombineFG.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMapCombineFG.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        fgCombineRuleRadio.setValueMap(valueMapCombineFG);
        fgCombineForm.setFields(fgCombineRuleRadio);
        fgQuestionLayout.addMember(fgCombineForm);

        stepFGLabel = new Label(BLCMain.getMessageManager().getString("stepFGLabel"));
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
        valueMapFG.put("ALL", BLCMain.getMessageManager().getString("allFGRadioChoice"));
        valueMapFG.put("FG_RULE", BLCMain.getMessageManager().getString("buildFGRadioChoice"));
        fgRuleRadio.setValueMap(valueMapFG);
        stepFGForm.setFields(fgRuleRadio);
        fgQuestionLayout.addMember(stepFGForm);
        fulfillmentGroupFilterBuilder = new FilterBuilder();
        fulfillmentGroupFilterBuilder.setDataSource(fulfillmentGroupDataSource);
        fulfillmentGroupFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(fulfillmentGroupDataSource));
        fulfillmentGroupFilterBuilder.setVisible(false);
        fulfillmentGroupFilterBuilder.setAllowEmpty(true);
        fulfillmentGroupFilterBuilder.setValidateOnChange(false);
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

        fgSectionView = new SectionView(BLCMain.getMessageManager().getString("fgSectionViewTitle"));
        fgSectionView.setVisible(false);
        fgSectionView.setWidth("98%");
        fgSectionView.getContentLayout().addMember(fgQuestionLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(fgSectionView);

        rightVerticalLayout.addMember(dynamicFormDisplay);

        addMember(leftVerticalLayout);
        addMember(rightVerticalLayout);
    }

    public DynamicForm getQualifyingItemSubTotalForm() {
        return qualifyingItemSubTotalForm;
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

    public ItemBuilderDisplay addTargetItemBuilder(DataSource orderItemDataSource) {
        ItemBuilderDisplay builder = new ItemBuilderView(orderItemDataSource, true);
        builder.enable();
        builder.setDirty(true);
        targetItemBuilderContainerLayout.addMember((ItemBuilderView) builder);
        targetItemBuilderViews.add(builder);
        return builder;
    }

    public void removeTargetItemBuilder(ItemBuilderDisplay itemBuilder) {
        targetItemBuilderContainerLayout.removeMember((ItemBuilderView) itemBuilder);
        targetItemBuilderViews.remove(itemBuilder);
    }

    public void removeAllTargetItemBuilders() {
        ItemBuilderView[] myViews = targetItemBuilderViews.toArray(new ItemBuilderView[]{});
        for (ItemBuilderView view : myViews) {
            removeTargetItemBuilder(view);
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

    public DynamicForm getFGCombineForm() {
        return fgCombineForm;
    }

    public DynamicForm getStepItemForm() {
        return stepItemForm;
    }

    public ToolStripButton getCloneButton() {
        return cloneButton;
    }

    public DynamicForm getOrderItemCombineForm() {
        return orderItemCombineForm;
    }

    public RadioGroupItem getOrderItemCombineRuleRadio() {
        return orderItemCombineRuleRadio;
    }

    public Label getOrderItemCombineLabel() {
        return orderItemCombineLabel;
    }

    public VLayout getCustomerLayout() {
        return customerLayout;
    }

    public VLayout getOrderSectionLayout() {
        return orderSectionLayout;
    }

    public SectionView getCustomerSection() {
        return customerSection;
    }

    public SectionView getOrderSection() {
        return orderSection;
    }

    public FloatItem getQualifyingItemSubTotal() {
        return qualifyingItemSubTotal;
    }

    public void setQualifyingItemSubTotal(FloatItem qualifyingItemSubTotal) {
        this.qualifyingItemSubTotal = qualifyingItemSubTotal;
    }

    public VLayout getNewTargetItemBuilderLayout() {
        return newTargetItemBuilderLayout;
    }

    public List<ItemBuilderDisplay> getTargetItemBuilderViews() {
        return targetItemBuilderViews;
    }

    public VLayout getTargetItemBuilderContainerLayout() {
        return targetItemBuilderContainerLayout;
    }

    public Button getTargetAddItemButton() {
        return targetAddItemButton;
    }

}
