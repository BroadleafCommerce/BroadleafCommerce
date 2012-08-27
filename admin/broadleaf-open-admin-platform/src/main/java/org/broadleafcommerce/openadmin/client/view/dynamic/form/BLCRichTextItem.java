package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.smartgwt.client.widgets.form.fields.BlurbItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.RichTextEditorDialog;

public class BLCRichTextItem extends BlurbItem {

    public BLCRichTextItem() {
        setHeight(200);
        setWidth(600);
        setClipValue(true);
        FormItemIcon formItemIcon = new FormItemIcon();
        setIcons(formItemIcon);

        addIconClickHandler(new IconClickHandler() {
            public void onIconClick(IconClickEvent event) {
                RichTextEditorDialog dialog = new RichTextEditorDialog();
               dialog.show(BLCRichTextItem.this);
               dialog.centerInPage();
            }
        });
    }

}
