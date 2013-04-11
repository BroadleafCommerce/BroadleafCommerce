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

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.form.fields.BlurbItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.RichTextEditorDialog;

public class BLCRichTextItem extends BlurbItem {

    public BLCRichTextItem() {

        setShouldSaveValue(true);
        setShowTitle(true);
        setTextBoxStyle("bl-rich-text-item");
        setWidth(480);

        FormItemIcon editIcon = new FormItemIcon();
        editIcon.setSrc(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/glyphicons/edit.png");
        editIcon.setHeight(16);
        editIcon.setWidth(16);
        setIcons(editIcon);

        addIconClickHandler(new IconClickHandler() {
            public void onIconClick(IconClickEvent event) {
                RichTextEditorDialog dialog = new RichTextEditorDialog();
                dialog.show(BLCRichTextItem.this);
                dialog.centerInPage();
            }
        });

    }

}
