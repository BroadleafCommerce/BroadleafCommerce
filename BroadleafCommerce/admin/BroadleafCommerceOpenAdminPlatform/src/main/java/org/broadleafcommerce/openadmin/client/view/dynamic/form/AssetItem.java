package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.layout.VStack;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/13/11
 * Time: 6:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class AssetItem extends CanvasItem {

    private Img previewImg;
    protected IButton imageUpdateButton;

    public AssetItem() {
        VStack allStack = new VStack();
        allStack.setWidth(60);
        allStack.setAlign(Alignment.CENTER);
        VStack previewContainer = new VStack();
        allStack.addMember(previewContainer);
        previewContainer.setWidth(60);
        previewContainer.setHeight(60);
        previewContainer.setBorder("1px solid #a6abb4");
        previewImg = new Img();
        previewImg.setImageType(ImageStyle.CENTER);
        previewImg.setVisible(true);
        previewImg.setShowDisabled(false);
        previewImg.setSrc(GWT.getModuleBaseURL() + "admin/images/blank.gif");
        previewImg.setShowDown(false);
        previewContainer.addChild(previewImg);
        final FormItem formItem = this;
        imageUpdateButton = new IButton("Update Artifact");
        imageUpdateButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String formItemName = formItem.getName();
                ((DynamicEntityDataSource) formItem.getForm().getDataSource()).getFormItemCallbackHandlerManager().getFormItemCallback(formItemName).execute(formItem);
            }
        });
        allStack.addMember(imageUpdateButton);

        setCanvas(allStack);
    }

    @Override
    public void setDisabled(Boolean disabled) {
        super.setDisabled(disabled);
        if (disabled) {
            imageUpdateButton.setVisible(false);
        } else {
            imageUpdateButton.setVisible(true);
        }
    }

    public void setPreviewSrc(String value) {
        previewImg.setSrc(value);
    }

}
