package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.RichTextEditorDialog;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.fields.RichTextItem;

public class BLCRichTextItem extends RichTextItem {

    public BLCRichTextItem() {

        setControlGroups();
        setShowTitle(true);
        final BLCRichTextItem item = this;

       
        BLCRichTextItem e = this;
        RichTextEditor editor = new RichTextEditor();

        editor.setControlGroups();
        this.setCanvas(editor);
        editor.setDisabled(false);
        setDisabled(false);
        editor.addKeyPressHandler(new KeyPressHandler() {
            
            @Override
            public void onKeyPress(KeyPressEvent event) {
           
            }
        });
        Canvas canvas = e.getCanvas();

        Button b = new Button();
        b.setTitle(BLCMain.getMessageManager()
                .getString("BLCRichTextItem_Edit"));
        b.setIcon("[SKIN]/actions/edit.png");
        b.setWidth(50);
        canvas.addChild(b, "insertAsset", true);
        b.addIconClickHandler(new com.smartgwt.client.widgets.events.IconClickHandler() {

            @Override
            public void onIconClick(
                    com.smartgwt.client.widgets.events.IconClickEvent event) {
                showDialog(item);
            }

        });
        b.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

            @Override
            public void onClick(
                    com.smartgwt.client.widgets.events.ClickEvent event) {
                showDialog(item);
            }
        });

    }

    private void showDialog(final BLCRichTextItem item) {
        RichTextEditorDialog dialog = new RichTextEditorDialog();
        dialog.show(item);
        dialog.centerInPage();
    }
}
