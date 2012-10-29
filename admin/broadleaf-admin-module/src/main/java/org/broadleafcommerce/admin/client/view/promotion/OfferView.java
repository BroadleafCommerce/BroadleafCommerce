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

package org.broadleafcommerce.admin.client.view.promotion;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.*;
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
import org.broadleafcommerce.openadmin.client.view.dynamic.*;
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

    public static final int LAYOUT_MARGIN = 15;
    public static final int LABEL_HEIGHT = 30;
    public static final int ADD_ITEM_BUTTON_WIDTH = 160;
    public static final String RADIO_GROUP_WIDTH = "400px";
    public static final int HELP_BUTTON_WIDTH = 18;
    public static final int HELP_BUTTON_HEIGHT = 18;

    public static final int BL_PROMO_QUESTION_HEIGHT = 30;

    //css class names
    public static final String BL_PROMO_QUESTION = "bl-promo-question";
    public static final String BL_PROMO_QUESTION_ANSWERS = "bl-promo-question-answers";
    public static final String BL_PROMO_QUESTION_RADIO_LABELS = "bl-promo-question-radio-labels";

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

    @Override
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

        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("promotionsListTitle"), entityDataSource, false);
        listDisplay.getGrid().setCanEdit(true);
        listDisplay.getGrid().setEditEvent(ListGridEditEvent.DOUBLECLICK);
        listDisplay.getGrid().setEditByCell(true);
        listDisplay.getGrid().setAutoSaveEdits(true);
        listDisplay.getGrid().setSaveByCell(true);
        listDisplay.getGrid().setAlternateBodyStyleName("");

        //listDisplay.getToolBar().addFill();
        //cloneButton = new ToolStripButton();
        //cloneButton.setDisabled(true);
        //cloneButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/double_arrow_right.png");
        //cloneButton.setPrompt(BLCMain.getMessageManager().getString("clonePromotionHelp"));
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


        //====================Advanced Restrictions====================//
        VLayout restrictLayout = new VLayout();
        restrictLayout.setID("offerRestrictLayout");
        restrictLayout.setLayoutLeftMargin(LAYOUT_MARGIN);
        restrictLayout.addMember(new PromotionQuestion("restrictOnlyPromotionLabel"));

        restrictForm = new DynamicForm();

        LinkedHashMap<String, String> restrictMap = new LinkedHashMap<String, String>();
        restrictMap.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        restrictMap.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        restrictRuleRadio = new PromotionAnswerGroup(restrictMap, "NO", true);

        restrictForm.setFields(restrictRuleRadio);
        restrictLayout.addMember(restrictForm);

        restrictionSectionView = new SectionView(BLCMain.getMessageManager().getString("advancedRestrictionsViewTitle"));
        restrictionSectionView.setVisible(false);
        restrictionSectionView.setWidth("98%");
        restrictionSectionView.getContentLayout().addMember(restrictLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(restrictionSectionView);

        //====================Customer Qualification====================//
        customerLayout = new VLayout();
        customerLayout.setID("offerCustomerLayout");
        customerLayout.setVisible(false);
        customerLayout.setLayoutMargin(10);
        customerLayout.setLayoutLeftMargin(20);

        HStack customerObtainHStack = new HStack();
        customerObtainHStack.setID("offerCustomerObtainHStack");
        customerObtainHStack.setMembersMargin(10);
        customerObtainHStack.setWidth100();
        customerObtainHStack.addMember(new PromotionQuestion("customerObtainLabel"));

        helpButtonType = new ImgButton();
        helpButtonType.setSrc(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/headerIcons/help.png");
        helpButtonType.setWidth(HELP_BUTTON_WIDTH);
        helpButtonType.setHeight(HELP_BUTTON_HEIGHT);
        customerObtainHStack.addMember(helpButtonType);
        customerLayout.addMember(customerObtainHStack);

        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("AUTOMATIC", BLCMain.getMessageManager().getString("deliveryTypeEnumAutomatic"));
        valueMap.put("CODE", BLCMain.getMessageManager().getString("deliveryTypeEnumCode"));
        valueMap.put("MANUAL", BLCMain.getMessageManager().getString("deliveryTypeEnumManual"));

        deliveryTypeRadio = new PromotionAnswerGroup(valueMap, "AUTOMATIC", true);

        codeField = new TextItem();
        codeField.setTitle(BLCMain.getMessageManager().getString("offerCodeFieldTitle"));
        codeField.setWrapTitle(false);
        codeField.setVisible(false);
        codeField.setWidth(240);
        codeField.setTitleOrientation(TitleOrientation.TOP);

        customerObtainForm = new DynamicForm();
        customerObtainForm.setNumCols(1);
        customerObtainForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);
        customerObtainForm.setFields(deliveryTypeRadio, codeField);
        customerLayout.addMember(customerObtainForm);

        whichCustomerForm = new DynamicForm();
        whichCustomerForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);

        LinkedHashMap<String, String> valueMap3 = new LinkedHashMap<String, String>();
        valueMap3.put("ALL", BLCMain.getMessageManager().getString("allCustomerRadioChoice"));
        valueMap3.put("CUSTOMER_RULE", BLCMain.getMessageManager().getString("buildCustomerRadioChoice"));

        customerRuleRadio = new PromotionAnswerGroup(valueMap3, "ALL", true);
        whichCustomerForm.setFields(customerRuleRadio);

        customerLayout.addMember(new PromotionQuestion("whichCustomerLabel"));
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

        customerFilterBuilder = new BLCFilterBuilder();
        customerFilterBuilder.setDataSource(customerDataSource);
        customerFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(customerDataSource));
        customerFilterBuilder.setVisible(false);
        customerFilterBuilder.setLayoutBottomMargin(LAYOUT_MARGIN);
        customerFilterBuilder.setAllowEmpty(true);
        customerFilterBuilder.setValidateOnChange(false);
        customerLayout.addMember(customerFilterBuilder);
        customerLayout.setLayoutBottomMargin(LAYOUT_MARGIN);

        customerSection = new SectionView(BLCMain.getMessageManager().getString("customerSectionViewTitle"));
        customerSection.setVisible(false);
        customerSection.setID("offerSectionStack");
        customerSection.setWidth("98%");
        customerSection.getContentLayout().addMember(customerLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(customerSection);

        //===============Order Qualification===============//
        orderSectionLayout = new VLayout();
        orderSectionLayout.setID("offerOrderSectionLayout");
        orderSectionLayout.setLayoutMargin(10);
        orderSectionLayout.setLayoutLeftMargin(20);
        orderSectionLayout.setVisible(false);
        orderSectionLayout.addMember(new PromotionQuestion("orderSectionLabel"));

        orderForm = new DynamicForm();
        orderForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);

        LinkedHashMap<String, String> orderMap = new LinkedHashMap<String, String>();
        orderMap.put("NONE", BLCMain.getMessageManager().getString("noneOrderRadioChoice"));
        orderMap.put("ORDER_RULE", BLCMain.getMessageManager().getString("buildOrderRadioChoice"));
        orderRuleRadio = new PromotionAnswerGroup(orderMap, "NONE", true);

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

        orderFilterBuilder = new BLCFilterBuilder();
        orderFilterBuilder.setDataSource(orderDataSource);
        orderFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(orderDataSource));
        orderFilterBuilder.setVisible(false);
        orderFilterBuilder.setLayoutBottomMargin(LAYOUT_MARGIN);
        orderFilterBuilder.setAllowEmpty(true);
        orderFilterBuilder.setValidateOnChange(false);
        orderSectionLayout.addMember(orderFilterBuilder);

        orderCombineLabel = new PromotionQuestion("orderCombineLabel");
        orderCombineLabel.setVisible(false);
        orderSectionLayout.addMember(orderCombineLabel);

        orderCombineForm = new DynamicForm();
        orderCombineForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);
        orderCombineForm.setVisible(false);

        LinkedHashMap<String, String> orderCombineMap = new LinkedHashMap<String, String>();
        orderCombineMap.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        orderCombineMap.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        orderCombineRuleRadio = new PromotionAnswerGroup(orderCombineMap, "NO", false);

        orderCombineForm.setFields(orderCombineRuleRadio);
        orderSectionLayout.addMember(orderCombineForm);

        orderSection = new SectionView(BLCMain.getMessageManager().getString("orderQualificationSectionViewTitle"));
        orderSection.setVisible(false);
        orderSection.setID("offerSectionStack2");
        orderSection.setWidth("98%");
        orderSection.getContentLayout().addMember(orderSectionLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(orderSection);

        //===============Item Qualification===============//
        VStack itemSectionLayout = new VStack();
        itemSectionLayout.setLayoutMargin(10);
        itemSectionLayout.setLayoutLeftMargin(20);
        itemSectionLayout.setID("offerItemSectionLayout");

        orderItemCombineLabel = new PromotionQuestion("orderItemCombineLabel");
        orderItemCombineLabel.setVisible(false);
        itemSectionLayout.addMember(orderItemCombineLabel);

        orderItemCombineForm = new DynamicForm();
        orderItemCombineForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);
        orderItemCombineForm.setVisible(false);

        LinkedHashMap<String, String> orderItemCombineMap = new LinkedHashMap<String, String>();
        orderItemCombineMap.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        orderItemCombineMap.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        orderItemCombineRuleRadio = new PromotionAnswerGroup(orderItemCombineMap, "YES", false);

        orderItemCombineForm.setFields(orderItemCombineRuleRadio);
        itemSectionLayout.addMember(orderItemCombineForm);

        bogoQuestionLayout = new VLayout();
        bogoQuestionLayout.setVisible(false);
        HStack hStackBogo = new HStack(10);
        hStackBogo.setID("offerHStackBogo");
        hStackBogo.setWidth100();
        hStackBogo.setHeight(LABEL_HEIGHT);
        bogoQuestionLabel = new PromotionQuestion("bogoQuestionLabel");
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
        stepBogoForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);

        LinkedHashMap<String, String> valueMapBogo = new LinkedHashMap<String, String>();
        valueMapBogo.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMapBogo.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        bogoRadio = new PromotionAnswerGroup(valueMapBogo, "NO", false);

        stepBogoForm.setFields(bogoRadio);
        bogoQuestionLayout.addMember(stepBogoForm);
        itemSectionLayout.addMember(bogoQuestionLayout);

        requiredItemsLayout = new VLayout();
        requiredItemsLayout.setVisible(false);

        requiredItemsLabel = new PromotionQuestion("requiredItemsLabel");
        requiredItemsLayout.addMember(requiredItemsLabel);

        orderItemLayout = new VLayout();
        orderItemLayout.setVisible(false);
        stepItemForm = new DynamicForm();
        stepItemForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);

        LinkedHashMap<String, String> valueMapItem = new LinkedHashMap<String, String>();
        valueMapItem.put("NONE", BLCMain.getMessageManager().getString("noneItemRadioChoice"));
        valueMapItem.put("ITEM_RULE", BLCMain.getMessageManager().getString("buildItemRadioChoice"));
        itemRuleRadio = new PromotionAnswerGroup(valueMapItem, "NONE", false);

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
        buttonLayout.setHeight(LABEL_HEIGHT);
        buttonLayout.setLayoutTopMargin(LAYOUT_MARGIN);
        addItemButton = new Button();
        addItemButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/add.png");
        addItemButton.setTitle(BLCMain.getMessageManager().getString("newItemRuleButtonTitle"));
        addItemButton.setWidth(ADD_ITEM_BUTTON_WIDTH);
        addItemButton.setWrap(false);
        buttonLayout.addMember(addItemButton);
        buttonLayout.setLayoutBottomMargin(LAYOUT_MARGIN);
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
        receiveFromAnotherPromoLabel.setHeight(LABEL_HEIGHT);
        receiveFromAnotherPromoLabel.setStyleName("blcPromoAdditionalQuestion");
        advancedItemCriteria.addMember(receiveFromAnotherPromoLabel);

        receiveFromAnotherPromoForm = new DynamicForm();
        receiveFromAnotherPromoForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);

        LinkedHashMap<String, String> valueMap4 = new LinkedHashMap<String, String>();
        valueMap4.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap4.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        receiveFromAnotherPromoRadio = new PromotionAnswerGroup(valueMap4, "NO", false);

        receiveFromAnotherPromoForm.setFields(receiveFromAnotherPromoRadio);
        advancedItemCriteria.addMember(receiveFromAnotherPromoForm);

        Label qualifiyForAnotherPromoLabel = new Label(BLCMain.getMessageManager().getString("qualifiyForAnotherPromoLabel"));
        qualifiyForAnotherPromoLabel.setWrap(false);
        qualifiyForAnotherPromoLabel.setHeight(LABEL_HEIGHT);
        qualifiyForAnotherPromoLabel.setStyleName("blcPromoAdditionalQuestion");
        advancedItemCriteria.addMember(qualifiyForAnotherPromoLabel);

        qualifyForAnotherPromoForm = new DynamicForm();
        qualifyForAnotherPromoForm.setStyleName(BL_PROMO_QUESTION_ANSWERS);

        LinkedHashMap<String, String> valueMap5 = new LinkedHashMap<String, String>();
        valueMap5.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap5.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        qualifyForAnotherPromoRadio = new PromotionAnswerGroup(valueMap5, "NO", false);

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
        itemSectionLayout.setLayoutBottomMargin(LAYOUT_MARGIN);

        itemQualificationSectionView = new SectionView(BLCMain.getMessageManager().getString("itemQualificationSectionTitle"));
        itemQualificationSectionView.setWidth("98%");
        itemQualificationSectionView.getContentLayout().addMember(itemSectionLayout);
        itemQualificationSectionView.setVisible(false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(itemQualificationSectionView);


        //---- Item Discount Target ---
        targetItemsLayout = new VLayout();
        targetItemsLayout.setLayoutMargin(10);
        targetItemsLayout.setLayoutLeftMargin(20);
        targetItemsLabel = new PromotionQuestion("targetItemsLabel");
        targetItemsLayout.addMember(targetItemsLabel);

        targetItemBuilderViews.add(new ItemBuilderView(orderItemDataSource, true));

        newTargetItemBuilderLayout = new VLayout();
        newTargetItemBuilderLayout.setVisible(false);
        HLayout targetButtonLayout = new HLayout();
        targetButtonLayout.setID("targetOfferButtonLayout");
        targetButtonLayout.setWidth100();
        targetButtonLayout.setAlign(Alignment.LEFT);
        targetButtonLayout.setHeight(LABEL_HEIGHT);
        targetButtonLayout.setLayoutTopMargin(LAYOUT_MARGIN);
        targetAddItemButton = new Button();
        targetAddItemButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/add.png");
        targetAddItemButton.setTitle(BLCMain.getMessageManager().getString("newItemRuleButtonTitle"));
        targetAddItemButton.setWidth(ADD_ITEM_BUTTON_WIDTH);
        targetAddItemButton.setWrap(false);
        targetButtonLayout.addMember(targetAddItemButton);
        targetButtonLayout.setLayoutBottomMargin(LAYOUT_MARGIN);
        newTargetItemBuilderLayout.addMember(targetButtonLayout);
        targetItemBuilderContainerLayout = new VLayout();
        newTargetItemBuilderLayout.addMember(targetItemBuilderContainerLayout);
        for (ItemBuilderDisplay widget : targetItemBuilderViews) {
            targetItemBuilderContainerLayout.addMember((ItemBuilderView) widget);
        }
        targetItemsLayout.addMember(newTargetItemBuilderLayout);
        targetItemsLayout.setLayoutBottomMargin(LAYOUT_MARGIN);

        advancedItemCriteriaTarget = new VLayout();
        advancedItemCriteriaTarget.setVisible(false);
        targetItemsLayout.addMember(advancedItemCriteriaTarget);
        Label receiveFromAnotherPromoTargetLabel = new Label(BLCMain.getMessageManager().getString("receiveFromAnotherPromoTargetLabel"));
        receiveFromAnotherPromoTargetLabel.setWrap(false);
        receiveFromAnotherPromoTargetLabel.setHeight(LABEL_HEIGHT);
        receiveFromAnotherPromoTargetLabel.setStyleName("blcPromoAdditionalQuestion");
        advancedItemCriteriaTarget.addMember(receiveFromAnotherPromoTargetLabel);

        receiveFromAnotherPromoTargetForm = new DynamicForm();

        LinkedHashMap<String, String> valueMap6 = new LinkedHashMap<String, String>();
        valueMap6.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap6.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        receiveFromAnotherPromoTargetRadio = new PromotionAnswerGroup(valueMap6, "NO", false);

        receiveFromAnotherPromoTargetForm.setFields(receiveFromAnotherPromoTargetRadio);
        advancedItemCriteriaTarget.addMember(receiveFromAnotherPromoTargetForm);

        Label qualifiyForAnotherPromoTargetLabel = new Label(BLCMain.getMessageManager().getString("qualifiyForAnotherPromoTargetLabel"));
        qualifiyForAnotherPromoTargetLabel.setWrap(false);
        qualifiyForAnotherPromoTargetLabel.setHeight(LABEL_HEIGHT);
        qualifiyForAnotherPromoTargetLabel.setStyleName("blcPromoAdditionalQuestion");
        advancedItemCriteriaTarget.addMember(qualifiyForAnotherPromoTargetLabel);

        qualifyForAnotherPromoTargetForm = new DynamicForm();
        LinkedHashMap<String, String> valueMap7 = new LinkedHashMap<String, String>();
        valueMap7.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMap7.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        qualifyForAnotherPromoTargetRadio = new PromotionAnswerGroup(valueMap7, "NO", false);

        qualifyForAnotherPromoTargetForm.setFields(qualifyForAnotherPromoTargetRadio);
        advancedItemCriteriaTarget.addMember(qualifyForAnotherPromoTargetForm);

        itemTargetSectionView = new SectionView(BLCMain.getMessageManager().getString("itemTargetSectionTitle"));
        itemTargetSectionView.setVisible(false);
        itemTargetSectionView.setWidth("98%");
        itemTargetSectionView.getContentLayout().addMember(targetItemsLayout);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(itemTargetSectionView);

        fgQuestionLayout = new VLayout();
        fgQuestionLayout.setLayoutLeftMargin(LAYOUT_MARGIN);

        Label fgCombineLabel = new Label(BLCMain.getMessageManager().getString("fgCombineLabel"));
        fgCombineLabel.setWrap(false);
        fgCombineLabel.setHeight(LABEL_HEIGHT);
        fgCombineLabel.setStyleName("blcPromoAdditionalQuestion");
        fgCombineLabel.setStyleName("label-bold");
        fgQuestionLayout.addMember(fgCombineLabel);

        fgCombineForm = new DynamicForm();

        LinkedHashMap<String, String> valueMapCombineFG = new LinkedHashMap<String, String>();
        valueMapCombineFG.put("YES", BLCMain.getMessageManager().getString("yesRadioChoice"));
        valueMapCombineFG.put("NO", BLCMain.getMessageManager().getString("noRadioChoice"));
        fgCombineRuleRadio = new PromotionAnswerGroup(valueMapCombineFG, "NO", false);

        fgCombineForm.setFields(fgCombineRuleRadio);
        fgQuestionLayout.addMember(fgCombineForm);

        stepFGLabel = new Label(BLCMain.getMessageManager().getString("stepFGLabel"));
        stepFGLabel.setWrap(false);
        stepFGLabel.setHeight(LABEL_HEIGHT);
        stepFGLabel.setStyleName("blcPromoAdditionalQuestion");
        stepFGLabel.setStyleName("label-bold");
        fgQuestionLayout.addMember(stepFGLabel);

        stepFGForm = new DynamicForm();
        LinkedHashMap<String, String> valueMapFG = new LinkedHashMap<String, String>();
        valueMapFG.put("ALL", BLCMain.getMessageManager().getString("allFGRadioChoice"));
        valueMapFG.put("FG_RULE", BLCMain.getMessageManager().getString("buildFGRadioChoice"));
        fgRuleRadio = new PromotionAnswerGroup(valueMapFG, "ALL", false);

        stepFGForm.setFields(fgRuleRadio);
        fgQuestionLayout.addMember(stepFGForm);
        fulfillmentGroupFilterBuilder = new BLCFilterBuilder();
        fulfillmentGroupFilterBuilder.setDataSource(fulfillmentGroupDataSource);
        fulfillmentGroupFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(fulfillmentGroupDataSource));
        fulfillmentGroupFilterBuilder.setVisible(false);
        fulfillmentGroupFilterBuilder.setAllowEmpty(true);
        fulfillmentGroupFilterBuilder.setValidateOnChange(false);
        fgQuestionLayout.addMember(fulfillmentGroupFilterBuilder);
        fgQuestionLayout.setLayoutBottomMargin(LAYOUT_MARGIN);

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

    @Override
    public DynamicForm getQualifyingItemSubTotalForm() {
        return qualifyingItemSubTotalForm;
    }

    @Override
    public ItemBuilderDisplay addItemBuilder(DataSource orderItemDataSource) {
        ItemBuilderDisplay builder = new ItemBuilderView(orderItemDataSource, true);
        builder.enable();
        builder.setDirty(true);
        itemBuilderContainerLayout.addMember((ItemBuilderView) builder);
        itemBuilderViews.add(builder);
        return builder;
    }

    @Override
    public void removeItemBuilder(ItemBuilderDisplay itemBuilder) {
        itemBuilderContainerLayout.removeMember((ItemBuilderView) itemBuilder);
        itemBuilderViews.remove(itemBuilder);
    }

    @Override
    public void removeAllItemBuilders() {
        ItemBuilderView[] myViews = itemBuilderViews.toArray(new ItemBuilderView[]{});
        for (ItemBuilderView view : myViews) {
            removeItemBuilder(view);
        }
    }

    @Override
    public ItemBuilderDisplay addTargetItemBuilder(DataSource orderItemDataSource) {
        ItemBuilderDisplay builder = new ItemBuilderView(orderItemDataSource, true);
        builder.enable();
        builder.setDirty(true);
        targetItemBuilderContainerLayout.addMember((ItemBuilderView) builder);
        targetItemBuilderViews.add(builder);
        return builder;
    }

    @Override
    public void removeTargetItemBuilder(ItemBuilderDisplay itemBuilder) {
        targetItemBuilderContainerLayout.removeMember((ItemBuilderView) itemBuilder);
        targetItemBuilderViews.remove(itemBuilder);
    }

    @Override
    public void removeAllTargetItemBuilders() {
        ItemBuilderView[] myViews = targetItemBuilderViews.toArray(new ItemBuilderView[]{});
        for (ItemBuilderView view : myViews) {
            removeTargetItemBuilder(view);
        }
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
    public ToolStripButton getAdvancedButton() {
        return advancedButton;
    }

    @Override
    public ImgButton getHelpButtonType() {
        return helpButtonType;
    }

    @Override
    public RadioGroupItem getDeliveryTypeRadio() {
        return deliveryTypeRadio;
    }

    @Override
    public TextItem getCodeField() {
        return codeField;
    }

    @Override
    public FilterBuilder getCustomerFilterBuilder() {
        return customerFilterBuilder;
    }

    @Override
    public RadioGroupItem getCustomerRuleRadio() {
        return customerRuleRadio;
    }

    @Override
    public FilterBuilder getFulfillmentGroupFilterBuilder() {
        return fulfillmentGroupFilterBuilder;
    }

    @Override
    public Label getStepFGLabel() {
        return stepFGLabel;
    }

    @Override
    public RadioGroupItem getFgRuleRadio() {
        return fgRuleRadio;
    }

    @Override
    public DynamicForm getStepFGForm() {
        return stepFGForm;
    }

    @Override
    public Label getRequiredItemsLabel() {
        return requiredItemsLabel;
    }

    @Override
    public Button getAddItemButton() {
        return addItemButton;
    }

    @Override
    public RadioGroupItem getItemRuleRadio() {
        return itemRuleRadio;
    }

    @Override
    public List<ItemBuilderDisplay> getItemBuilderViews() {
        return itemBuilderViews;
    }

    @Override
    public ImgButton getHelpButtonBogo() {
        return helpButtonBogo;
    }

    @Override
    public RadioGroupItem getBogoRadio() {
        return bogoRadio;
    }

    @Override
    public Label getTargetItemsLabel() {
        return targetItemsLabel;
    }

    @Override
    public Label getBogoQuestionLabel() {
        return bogoQuestionLabel;
    }

    @Override
    public void setHelpButtonBogo(ImgButton helpButtonBogo) {
        this.helpButtonBogo = helpButtonBogo;
    }

    @Override
    public DynamicForm getStepBogoForm() {
        return stepBogoForm;
    }

    @Override
    public VLayout getBogoQuestionLayout() {
        return bogoQuestionLayout;
    }

    @Override
    public VLayout getFgQuestionLayout() {
        return fgQuestionLayout;
    }

    @Override
    public RadioGroupItem getOrderRuleRadio() {
        return orderRuleRadio;
    }

    @Override
    public FilterBuilder getOrderFilterBuilder() {
        return orderFilterBuilder;
    }

    @Override
    public VLayout getRequiredItemsLayout() {
        return requiredItemsLayout;
    }

    @Override
    public VLayout getTargetItemsLayout() {
        return targetItemsLayout;
    }

    @Override
    public VLayout getNewItemBuilderLayout() {
        return newItemBuilderLayout;
    }

    @Override
    public VLayout getOrderItemLayout() {
        return orderItemLayout;
    }

    @Override
    public SectionView getFgSectionView() {
        return fgSectionView;
    }

    @Override
    public RadioGroupItem getReceiveFromAnotherPromoRadio() {
        return receiveFromAnotherPromoRadio;
    }

    @Override
    public SectionView getItemTargetSectionView() {
        return itemTargetSectionView;
    }

    @Override
    public RadioGroupItem getQualifyForAnotherPromoRadio() {
        return qualifyForAnotherPromoRadio;
    }

    @Override
    public RadioGroupItem getReceiveFromAnotherPromoTargetRadio() {
        return receiveFromAnotherPromoTargetRadio;
    }

    @Override
    public RadioGroupItem getQualifyForAnotherPromoTargetRadio() {
        return qualifyForAnotherPromoTargetRadio;
    }

    @Override
    public VLayout getAdvancedItemCriteriaTarget() {
        return advancedItemCriteriaTarget;
    }

    @Override
    public VLayout getAdvancedItemCriteria() {
        return advancedItemCriteria;
    }

    @Override
    public Label getOrderCombineLabel() {
        return orderCombineLabel;
    }

    @Override
    public DynamicForm getOrderCombineForm() {
        return orderCombineForm;
    }

    @Override
    public RadioGroupItem getOrderCombineRuleRadio() {
        return orderCombineRuleRadio;
    }

    @Override
    public RadioGroupItem getFgCombineRuleRadio() {
        return fgCombineRuleRadio;
    }

    @Override
    public RadioGroupItem getRestrictRuleRadio() {
        return restrictRuleRadio;
    }

    @Override
    public SectionView getRestrictionSectionView() {
        return restrictionSectionView;
    }

    @Override
    public DynamicForm getRawCustomerForm() {
        return rawCustomerForm;
    }

    @Override
    public TextAreaItem getRawCustomerTextArea() {
        return rawCustomerTextArea;
    }

    @Override
    public DynamicForm getRawOrderForm() {
        return rawOrderForm;
    }

    @Override
    public TextAreaItem getRawOrderTextArea() {
        return rawOrderTextArea;
    }

    @Override
    public DynamicForm getRawFGForm() {
        return rawFGForm;
    }

    @Override
    public TextAreaItem getRawFGTextArea() {
        return rawFGTextArea;
    }

    @Override
    public SectionView getItemQualificationSectionView() {
        return itemQualificationSectionView;
    }

    @Override
    public DynamicForm getRestrictForm() {
        return restrictForm;
    }

    @Override
    public DynamicForm getCustomerObtainForm() {
        return customerObtainForm;
    }

    @Override
    public DynamicForm getWhichCustomerForm() {
        return whichCustomerForm;
    }

    @Override
    public DynamicForm getOrderForm() {
        return orderForm;
    }

    @Override
    public DynamicForm getReceiveFromAnotherPromoForm() {
        return receiveFromAnotherPromoForm;
    }

    @Override
    public DynamicForm getQualifyForAnotherPromoForm() {
        return qualifyForAnotherPromoForm;
    }

    @Override
    public DynamicForm getReceiveFromAnotherPromoTargetForm() {
        return receiveFromAnotherPromoTargetForm;
    }

    @Override
    public DynamicForm getQualifyForAnotherPromoTargetForm() {
        return qualifyForAnotherPromoTargetForm;
    }

    @Override
    public DynamicForm getFGCombineForm() {
        return fgCombineForm;
    }

    @Override
    public DynamicForm getStepItemForm() {
        return stepItemForm;
    }

    @Override
    public ToolStripButton getCloneButton() {
        return cloneButton;
    }

    @Override
    public DynamicForm getOrderItemCombineForm() {
        return orderItemCombineForm;
    }

    @Override
    public RadioGroupItem getOrderItemCombineRuleRadio() {
        return orderItemCombineRuleRadio;
    }

    @Override
    public Label getOrderItemCombineLabel() {
        return orderItemCombineLabel;
    }

    @Override
    public VLayout getCustomerLayout() {
        return customerLayout;
    }

    @Override
    public VLayout getOrderSectionLayout() {
        return orderSectionLayout;
    }

    @Override
    public SectionView getCustomerSection() {
        return customerSection;
    }

    @Override
    public SectionView getOrderSection() {
        return orderSection;
    }

    @Override
    public FloatItem getQualifyingItemSubTotal() {
        return qualifyingItemSubTotal;
    }

    public void setQualifyingItemSubTotal(FloatItem qualifyingItemSubTotal) {
        this.qualifyingItemSubTotal = qualifyingItemSubTotal;
    }

    @Override
    public VLayout getNewTargetItemBuilderLayout() {
        return newTargetItemBuilderLayout;
    }

    @Override
    public List<ItemBuilderDisplay> getTargetItemBuilderViews() {
        return targetItemBuilderViews;
    }

    @Override
    public VLayout getTargetItemBuilderContainerLayout() {
        return targetItemBuilderContainerLayout;
    }

    @Override
    public Button getTargetAddItemButton() {
        return targetAddItemButton;
    }

    class PromotionQuestion extends Label {

        public PromotionQuestion(String questionTextKey) {
            setContents(BLCMain.getMessageManager().getString(questionTextKey));
            setStyleName(BL_PROMO_QUESTION);
            setHeight(BL_PROMO_QUESTION_HEIGHT);
            setWrap(false);
        }

    }

    class PromotionAnswerGroup extends RadioGroupItem {

        public PromotionAnswerGroup(LinkedHashMap<String, String> options, String defaultValue, boolean disabled) {

            setTextBoxStyle(BL_PROMO_QUESTION_RADIO_LABELS);
            setDefaultValue(defaultValue);
            setDisabled(disabled);
            setValueMap(options);
            setWidth(RADIO_GROUP_WIDTH);
            setShowTitle(false);
            setWrap(false);

        }

    }


}
