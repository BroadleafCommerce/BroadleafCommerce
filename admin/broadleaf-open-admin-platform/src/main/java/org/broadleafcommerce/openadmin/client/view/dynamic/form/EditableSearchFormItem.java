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

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;

/**
 * 
 * @author jfischer
 *
 */
public class EditableSearchFormItem extends TextItem {

    public EditableSearchFormItem() {
        //use default trigger icon here. User can customize.  
        //[SKIN]/DynamicForm/default_formItem_icon.gif  
        FormItemIcon formItemIcon = new FormItemIcon();
        setIcons(formItemIcon);
        
        addIconClickHandler(new IconClickHandler() {  
            public void onIconClick(IconClickEvent event) {  
                final String formItemName = event.getItem().getName();
                ((DynamicEntityDataSource) event.getItem().getForm().getDataSource()).getFormItemCallbackHandlerManager().getFormItemCallback(formItemName).execute(event.getItem());
            }  
        });
    }
    
}
