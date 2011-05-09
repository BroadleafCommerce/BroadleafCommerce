package org.broadleafcommerce.gwt.client.presenter.dynamic.entity;

import java.util.HashMap;

import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGrid;


public class FormItemCallbackHandlerManager {
	
	private static final long serialVersionUID = 1L;
	
	protected HashMap<String, FormItemCallback> callbacks = new HashMap<String, FormItemCallback>();

	public void addSearchFormItemCallback(String fieldName, final EntitySearchDialog searchView, final String searchDialogTitle, final ListGrid grid, final DynamicFormDisplay dynamicFormDisplay) {
		callbacks.put(fieldName, new FormItemCallback() {
			public void execute(final FormItem formItem) {
				searchView.search(searchDialogTitle, new SearchItemSelectedEventHandler() {
					public void onSearchItemSelected(SearchItemSelectedEvent event) {
						final String myId = event.getRecord().getAttribute("id");
						String myName = event.getRecord().getAttribute("name");
						formItem.getForm().getField("__display_"+formItem.getName()).setValue(myName);
						Timer timer = new Timer() {  
				            public void run() {  
				            	formItem.setValue(myId);
				            	dynamicFormDisplay.getSaveButton().enable();
				            }  
				        };
				        timer.schedule(100);
					}
				});
			}	
		});
	}
	
	public void addSearchFormItemCallback(String fieldName, FormItemCallback formItemCallback) {
		callbacks.put(fieldName, formItemCallback);
	}
	
	public FormItemCallback getSearchFormItemCallback(String fieldName) {
		return callbacks.get(fieldName);
	}
	
}
