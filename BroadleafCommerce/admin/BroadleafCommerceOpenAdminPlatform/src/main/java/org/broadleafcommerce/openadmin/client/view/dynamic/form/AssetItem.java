package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.CanvasItem;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/13/11
 * Time: 6:17 PM
 * To change this template use File | Settings | File Templates.
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
