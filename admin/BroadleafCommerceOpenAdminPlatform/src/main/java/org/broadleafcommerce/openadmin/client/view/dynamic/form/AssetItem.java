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
}
