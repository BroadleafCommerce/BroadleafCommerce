/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.view.dynamic;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;

/**
 * 
 * @author jfischer
 *
 */
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

    public CriteriaCharacteristics getCharacteristics();

    public void setCharacteristics(CriteriaCharacteristics characteristics);
    
}