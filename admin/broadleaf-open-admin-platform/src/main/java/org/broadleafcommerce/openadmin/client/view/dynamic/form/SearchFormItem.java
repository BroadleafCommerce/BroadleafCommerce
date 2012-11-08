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

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;

import java.util.logging.Level;

/**
 * 
 * @author jfischer
 *
 */
public class SearchFormItem extends TextItem {

	public SearchFormItem() {

        super();
        setAttribute("readOnly", true);

        FormItemIcon formItemIcon = new FormItemIcon();
        formItemIcon.setSrc(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/glyphicons/binoculars.png");
        formItemIcon.setWidth(16);
        formItemIcon.setHeight(16);
        formItemIcon.setNeverDisable(true);
        setIcons(formItemIcon);
        
        addIconClickHandler(new IconClickHandler() {  
            public void onIconClick(IconClickEvent event) {  
            	final String formItemName = event.getItem().getName();
                if (formItemName == null) {
                    java.util.logging.Logger.getLogger(getClass().toString()).log(Level.SEVERE,"The name associated with this item is null. Have you chosen a display field for a @AdminPresentationToOneLookup that may contain a null value?");
                    throw new RuntimeException("The name associated with this item is null - cannot continue");
                }
            	((DynamicEntityDataSource) event.getItem().getForm().getDataSource()).getFormItemCallbackHandlerManager().getFormItemCallback(formItemName).execute(event.getItem());
            }  
        });
	}


    
}
