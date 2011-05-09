package org.broadleafcommerce.gwt.admin.client.view.promotion.offer;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;

public interface ItemBuilderDisplay {

	public FormItem getItemQuantity();

	public FilterBuilder getItemFilterBuilder();

	public ImgButton getRemoveButton();
	
	public void enable();
	
	public void disable();
	
	public void hide();
	
	public void show();
	
	public DynamicForm getRawItemForm();

	public TextAreaItem getRawItemTextArea();
	
	public DynamicForm getItemForm();
	
	public Boolean getIncompatibleMVEL();

	public void setIncompatibleMVEL(Boolean incompatibleMVEL);
	
	public Boolean getDirty();

	public void setDirty(Boolean dirty);
	
	public Record getRecord();

	public void setRecord(Record record);
	
}