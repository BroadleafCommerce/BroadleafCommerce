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

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public interface OfferDisplay extends DynamicEditDisplay {

	public DynamicFormDisplay getDynamicFormDisplay();
	public DynamicEntityListDisplay getListDisplay();
	public ToolStripButton getAdvancedButton();
	public ImgButton getHelpButtonType();
	public RadioGroupItem getDeliveryTypeRadio();
	public TextItem getCodeField();
	public FilterBuilder getCustomerFilterBuilder();
	public RadioGroupItem getCustomerRuleRadio();
	public FilterBuilder getFulfillmentGroupFilterBuilder();
	public Label getStepFGLabel();
	public RadioGroupItem getFgRuleRadio();
	public DynamicForm getStepFGForm();
	public Label getRequiredItemsLabel();
	public Button getAddItemButton();
	public RadioGroupItem getItemRuleRadio();
	public List<ItemBuilderDisplay> getItemBuilderViews();
	public ImgButton getHelpButtonBogo();
	public RadioGroupItem getBogoRadio();
	public Label getTargetItemsLabel();
	public Label getBogoQuestionLabel();
	public void setHelpButtonBogo(ImgButton helpButtonBogo);
	public DynamicForm getStepBogoForm();
	public VLayout getBogoQuestionLayout();
	public VLayout getFgQuestionLayout();
	public RadioGroupItem getOrderRuleRadio();
	public FilterBuilder getOrderFilterBuilder();
	public ItemBuilderDisplay addItemBuilder(DataSource orderItemDataSource);
	public void removeItemBuilder(ItemBuilderDisplay itemBuilder);
	public VLayout getRequiredItemsLayout();
	public VLayout getTargetItemsLayout();
	public VLayout getNewItemBuilderLayout();
	public VLayout getOrderItemLayout();
	public SectionView getFgSectionView();
	public RadioGroupItem getReceiveFromAnotherPromoRadio();
	public SectionView getItemTargetSectionView();
	public RadioGroupItem getQualifyForAnotherPromoRadio();
	public RadioGroupItem getReceiveFromAnotherPromoTargetRadio();
	public RadioGroupItem getQualifyForAnotherPromoTargetRadio();
	public VLayout getAdvancedItemCriteriaTarget();
	public VLayout getAdvancedItemCriteria();
	public Label getOrderCombineLabel();
	public DynamicForm getOrderCombineForm();
	public RadioGroupItem getOrderCombineRuleRadio();
	public RadioGroupItem getFgCombineRuleRadio();
	public RadioGroupItem getRestrictRuleRadio();
	public SectionView getRestrictionSectionView();
	public DynamicForm getRawCustomerForm();
	public TextAreaItem getRawCustomerTextArea();
	public DynamicForm getRawOrderForm();
	public TextAreaItem getRawOrderTextArea();
	public void removeAllItemBuilders();
	public DynamicForm getRawFGForm();
	public TextAreaItem getRawFGTextArea();
	public SectionView getItemQualificationSectionView();
	public DynamicForm getRestrictForm();
	public DynamicForm getCustomerObtainForm();
	public DynamicForm getWhichCustomerForm();
	public DynamicForm getOrderForm();
	public DynamicForm getReceiveFromAnotherPromoForm();
	public DynamicForm getQualifyForAnotherPromoForm();
	public DynamicForm getReceiveFromAnotherPromoTargetForm();
	public DynamicForm getQualifyForAnotherPromoTargetForm();
	public DynamicForm getFGCombineForm();
	public DynamicForm getStepItemForm();
	public ToolStripButton getCloneButton();
	public DynamicForm getOrderItemCombineForm();
	public RadioGroupItem getOrderItemCombineRuleRadio();
	public Label getOrderItemCombineLabel();
    public VLayout getCustomerLayout();
    public VLayout getOrderSectionLayout();
    public SectionView getCustomerSection();
    public SectionView getOrderSection();
	public FloatItem getQualifyingItemSubTotal();
	public DynamicForm getQualifyingItemSubTotalForm();

    public VLayout getNewTargetItemBuilderLayout();
    public List<ItemBuilderDisplay> getTargetItemBuilderViews();
    public VLayout getTargetItemBuilderContainerLayout();
    public Button getTargetAddItemButton();
    public ItemBuilderDisplay addTargetItemBuilder(DataSource orderItemDataSource);
    public void removeTargetItemBuilder(ItemBuilderDisplay itemBuilder);
    public void removeAllTargetItemBuilders();
}