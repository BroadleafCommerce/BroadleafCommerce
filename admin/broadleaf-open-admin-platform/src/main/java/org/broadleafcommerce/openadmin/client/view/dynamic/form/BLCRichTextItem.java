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

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.RichTextEditorDialog;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class BLCRichTextItem extends CanvasItem {
    
    protected HTMLPane htmlPane;

    public BLCRichTextItem() {
        setShouldSaveValue(true);
        setShowTitle(true);

        Canvas canvas = new Canvas();
        canvas.setHeight(200);
        canvas.setWidth(600);
        
        Button editButton = new Button(BLCMain.getMessageManager().getString("editRichText"));
        editButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RichTextEditorDialog dialog = new RichTextEditorDialog();
                dialog.show(BLCRichTextItem.this);
                dialog.centerInPage();                
            }
        });
        
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.addMember(editButton);
        htmlPane = new HTMLPane();
        htmlPane.setStyleName("blcHtmlPane");
        //htmlPane.setBorder("1px solid black");
        //htmlPane.setPadding(5);
        //htmlPane.setMargin(5);
        htmlPane.setDisabled(true);
        layout.addMember(htmlPane);
        
        addShowValueHandler(new ShowValueHandler() {
            @Override
            public void onShowValue(ShowValueEvent event) {
                htmlPane.setContents((String)event.getDataValue());
            }
        });
        
        addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                htmlPane.setContents((String)event.getValue());
            }
        });
        
        canvas.addChild(layout);
        setCanvas(canvas);
    }

}
