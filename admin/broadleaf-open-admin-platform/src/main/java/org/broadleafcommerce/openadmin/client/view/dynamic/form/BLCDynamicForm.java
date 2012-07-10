package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class BLCDynamicForm extends DynamicForm {
	public BLCDynamicForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BLCDynamicForm(JavaScriptObject jsObj) {
		super(jsObj);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void editRecord(Record record)
	{
		FormItem[] fields = this.getFields();
		for(FormItem field : fields)
		{

			if(field instanceof HTMLTextItem)
			{
				String jsObj = record.getAttributeAsString(field.getName());
				
				
				((HTMLTextItem)field).setValue(jsObj);
			}
		}

super.editRecord(record);

	}
	
////	@Override
//	public void saveData()
//	{
//		FormItem[] fields = this.getFields();
//		
//		for(FormItem field : fields)
//		{
//			if(field instanceof AbstractCanvasItem)
//			{
//				this.setValue(field.getName(), (String)field.getValue());
//			}
//		}
//		
//	super.saveData();
//	}



}
