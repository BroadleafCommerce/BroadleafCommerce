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

package org.broadleafcommerce.cms.admin.client.view.sandbox;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * 
 * @author jfischer
 *
 */
public class CommentDialog extends Window {

    protected IButton saveButton;
    protected CommentCallback callback;

    public CommentDialog() {
        super();
        this.setIsModal(true);
        this.setShowModalMask(true);
        this.setShowMinimizeButton(false);
        this.setWidth(350);
        this.setHeight(300);
        this.setCanDragResize(true);
        this.setOverflow(Overflow.AUTO);
        this.setVisible(false);

        HLayout formLayout = new HLayout();
        formLayout.setLayoutMargin(20);
        formLayout.setAlign(Alignment.CENTER);
        formLayout.setWidth100();
        DynamicForm form = new DynamicForm();
        final TextAreaItem formItem = new TextAreaItem();
        //formItem.setLength(255);
        formItem.setHeight(155);
        formItem.setColSpan(2);
        formItem.setShowTitle(false);
        formItem.setWidth(255);
        form.setFields(formItem);
        formLayout.addMember(form);
        
        addItem(formLayout);
        
        saveButton = new IButton("Ok");
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (callback != null) {
                    String val = formItem.getValueAsString();
                    callback.comment(val==null?"":val);
                }
                hide();
            }
        });

        IButton cancelButton = new IButton("Cancel");  
        cancelButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                hide();
            }  
        });
        
        HLayout hLayout = new HLayout(10);
        hLayout.setAlign(Alignment.CENTER);
        hLayout.addMember(saveButton);
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(10);
        hLayout.setLayoutBottomMargin(10);
        
        addItem(hLayout);
    }
    
    public void launch(String title, CommentCallback callback) {
        this.setTitle(title);
        this.callback = callback;
        centerInPage();
        show();
    }

    public IButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(IButton saveButton) {
        this.saveButton = saveButton;
    }
}
