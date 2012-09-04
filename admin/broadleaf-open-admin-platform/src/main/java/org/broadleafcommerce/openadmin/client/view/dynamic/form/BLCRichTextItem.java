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
