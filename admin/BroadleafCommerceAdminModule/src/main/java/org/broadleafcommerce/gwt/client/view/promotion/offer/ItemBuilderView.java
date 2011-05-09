package org.broadleafcommerce.gwt.client.view.promotion.offer;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ItemBuilderView extends HLayout implements ItemBuilderDisplay {
	
	protected IntegerItem itemQuantity;
	protected FilterBuilder itemFilterBuilder;
	protected ImgButton removeButton;

	public ItemBuilderView(DataSource itemDataSource) {
		super(10);
		
		VLayout removeLayout = new VLayout();
		removeLayout.setAlign(VerticalAlignment.CENTER);
		removeLayout.setWidth(16);
		removeButton = new ImgButton();
		removeButton.setSrc(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/actions/remove.png");
		removeButton.setShowRollOver(false);
		removeButton.setShowDownIcon(false);
		removeButton.setShowDown(false);
		removeButton.setWidth(16);
		removeButton.setHeight(16);
		removeButton.setDisabled(true);
		removeLayout.addMember(removeButton);
		addMember(removeLayout);
        
		VLayout formLayout = new VLayout();
		formLayout.setAlign(VerticalAlignment.CENTER);
		formLayout.setWidth(30);
		DynamicForm itemForm = new DynamicForm();
		itemQuantity = new IntegerItem();
		itemQuantity.setShowTitle(false);
		//itemQuantity.setWidth(40);
		itemQuantity.setValue(1);
		itemForm.setItems(itemQuantity);
		itemQuantity.setDisabled(true);
		formLayout.addMember(itemForm);
		addMember(formLayout);
		
		VLayout labelLayout = new VLayout();
		labelLayout.setAlign(VerticalAlignment.CENTER);
		labelLayout.setWidth(20);
		Label label = new Label("Of");
		label.setWidth(20);
		label.setHeight(20);
		labelLayout.addMember(label);
		addMember(labelLayout);
		
		VLayout builderLayout = new VLayout();
		builderLayout.setAlign(VerticalAlignment.CENTER);
		itemFilterBuilder = new FilterBuilder();  
		itemFilterBuilder.setDataSource(itemDataSource);
		itemFilterBuilder.setDisabled(true);
		itemFilterBuilder.setLayoutBottomMargin(10);
		builderLayout.addMember(itemFilterBuilder);
		addMember(builderLayout);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.promotion.offer.ItemBuilderDisplay#getItemQuantity()
	 */
	public IntegerItem getItemQuantity() {
		return itemQuantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.promotion.offer.ItemBuilderDisplay#getItemFilterBuilder()
	 */
	public FilterBuilder getItemFilterBuilder() {
		return itemFilterBuilder;
	}

	public ImgButton getRemoveButton() {
		return removeButton;
	}
	
	public void enable() {
		removeButton.enable();
		itemQuantity.enable();
		itemFilterBuilder.enable();
	}
	
	public void disable() {
		removeButton.disable();
		itemQuantity.disable();
		itemFilterBuilder.disable();
	}
}
