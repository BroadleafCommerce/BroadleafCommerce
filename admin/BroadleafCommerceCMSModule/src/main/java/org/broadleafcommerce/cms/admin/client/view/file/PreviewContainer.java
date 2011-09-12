package org.broadleafcommerce.cms.admin.client.view.file;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VStack;


/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/12/11
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreviewContainer extends VStack {

    protected VStack previewContainer;
    protected Img previewImg;
    protected Img loadingImage;
    protected Label previewLabel;
    protected IButton imageUpdateButton;

    public PreviewContainer() {
        setLayoutMargin(10);
        setWidth(76);
        previewContainer = new VStack();
        addMember(previewContainer);
        previewContainer.setWidth(60);
        previewContainer.setHeight(60);
        previewContainer.setBorder("1px solid #a6abb4");
        loadingImage = new Img();
        loadingImage.setImageType(ImageStyle.CENTER);
        loadingImage.setSrc(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/loadingSmall.gif");
        loadingImage.setVisible(false);
        previewContainer.addChild(loadingImage);
        previewImg = new Img();
        previewImg.setImageType(ImageStyle.CENTER);
        previewImg.setVisible(false);
        previewContainer.addChild(previewImg);
        imageUpdateButton = new IButton("Update Image");
        imageUpdateButton.setWidth(60);
        addMember(imageUpdateButton);
        previewLabel = new Label();
        previewLabel.setAlign(Alignment.CENTER);
        previewLabel.setContents("<span style=\"color: #a6abb4\">Preview</span>");
        previewContainer.addChild(previewLabel);
    }

    public void setImage(final String src) {
        final Image actualImage = new Image(src);
        actualImage.setVisible(false);
        actualImage.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                previewImg.setSrc("../" + src);
                previewImg.setVisible(true);
                loadingImage.setVisible(false);
                actualImage.removeFromParent();
            }
        });
        addMember(actualImage);
        previewLabel.setVisible(false);
        loadingImage.setVisible(true);
    }

    public void reset() {
        previewImg.setVisible(false);
        previewImg.setVisible(true);
    }

    public Img getPreviewImg() {
        return previewImg;
    }

    public Label getPreviewLabel() {
        return previewLabel;
    }

    public IButton getImageUpdateButton() {
        return imageUpdateButton;
    }
}
