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
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.layout.VStack;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;

/**
 * @author jfischer
 */
public class AssetCanvas extends VStack {

    protected IButton imageUpdateButton;
    protected Canvas previewContainer;

    public AssetCanvas(final FormItem formItem) {
        setWidth(100);
        setAlign(Alignment.CENTER);
        previewContainer = new Canvas();
        addMember(previewContainer);
        previewContainer.setWidth(100);
        previewContainer.setHeight(100);
        previewContainer.setBorder("1px solid #a6abb4");
        imageUpdateButton = new IButton("Update Artifact");
        imageUpdateButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String formItemName = formItem.getName();
                ((DynamicEntityDataSource) formItem.getForm().getDataSource()).getFormItemCallbackHandlerManager().getFormItemCallback(formItemName).execute(formItem);
            }
        });
        addMember(imageUpdateButton);
        if (formItem.getDisabled()) {
            imageUpdateButton.setVisible(false);
        }
    }

    public void updateImg(String src) {
        String key = "[ISOMORPHIC]/../";
        if (src.contains(key)) {
            src = GWT.getModuleBaseURL() + src.substring(key.length(), src.length());
        }
        previewContainer.setContents("<table width='99' height='99'><tr><td align='center' valign='middle'><img src='" + src + "' align='middle'/></td></tr></table>");
    }

    public void clearImage() {
        previewContainer.setContents("&nbsp;");
    }

    public IButton getImageUpdateButton() {
        return imageUpdateButton;
    }

    public void setImageUpdateButton(IButton imageUpdateButton) {
        this.imageUpdateButton = imageUpdateButton;
    }

}
