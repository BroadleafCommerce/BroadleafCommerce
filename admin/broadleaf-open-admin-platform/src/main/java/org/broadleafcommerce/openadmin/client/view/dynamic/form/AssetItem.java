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

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.CanvasItem;

/**
 * @author Jeff Fischer
 */
public class AssetItem extends CanvasItem {

    @Override
    protected Canvas createCanvas() {
        return new AssetCanvas(this);
    }

    @Override
    public void setDisabled(Boolean disabled) {
        super.setDisabled(disabled);
        if (getCanvas() != null) {
            if (disabled) {
                ((AssetCanvas) getCanvas()).getImageUpdateButton().setVisible(false);
            } else {
                ((AssetCanvas) getCanvas()).getImageUpdateButton().setVisible(true);
            }
        }
    }

    @Override
    public void updateState() {
        ((AssetCanvas) getCanvas()).updateImg(getValue().toString());
    }

    public void clearImage() {
        ((AssetCanvas) getCanvas()).clearImage();
    }
}
