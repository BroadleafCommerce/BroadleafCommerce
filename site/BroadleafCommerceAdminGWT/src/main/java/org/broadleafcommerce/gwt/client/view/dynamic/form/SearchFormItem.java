package org.broadleafcommerce.gwt.client.view.dynamic.form;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.FormItemCallback;

import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;

public class SearchFormItem extends TextItem {

	public SearchFormItem() {  
        //use default trigger icon here. User can customize.  
        //[SKIN]/DynamicForm/default_formItem_icon.gif  
        FormItemIcon formItemIcon = new FormItemIcon();
        setIcons(formItemIcon);
        
        addIconClickHandler(new IconClickHandler() {  
            public void onIconClick(IconClickEvent event) {  
            	final String formItemName = event.getItem().getName();
            	((FormItemCallback) ((DynamicEntityDataSource) event.getItem().getForm().getDataSource()).getFormItemCallbackHandlerManager().getSearchFormItemCallback(formItemName)).execute(event.getItem());
            }  
        });
	}
    
}
