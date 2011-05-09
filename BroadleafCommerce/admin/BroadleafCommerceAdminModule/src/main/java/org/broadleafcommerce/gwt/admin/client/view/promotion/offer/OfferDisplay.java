package org.broadleafcommerce.gwt.admin.client.view.promotion.offer;

import java.util.List;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface OfferDisplay extends DynamicEditDisplay {

	public DynamicFormDisplay getDynamicFormDisplay();

	public DynamicEntityListDisplay getListDisplay();
	
	public ToolStripButton getAdvancedButton();
	
	public ImgButton getHelpButtonType();
	
	public RadioGroupItem getDeliveryTypeRadio();
	
	public IntegerItem getMaxUseField();
	
	public TextItem getCodeField();
	
	public FilterBuilder getCustomerFilterBuilder();
	
	public RadioGroupItem getCustomerRuleRadio();
	
	public FilterBuilder getFulfillmentGroupFilterBuilder();

	public Label getStepFGLabel();
	
	public RadioGroupItem getFgRuleRadio();
	
	public DynamicForm getStepFGForm();
	
	public Label getStepItemLabel();
	
	public Button getAddItemButton();
	
	public RadioGroupItem getItemRuleRadio();
	
	public List<ItemBuilderDisplay> getItemBuilderViews();
	
	public ImgButton getHelpButtonBogo();
	
}