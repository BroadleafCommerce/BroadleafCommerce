package org.broadleafcommerce.cms.admin.client.view.file;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeView;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;

/**
 * Created by jfischer
 */
public class StaticAssetsView extends HLayout implements Instantiable, StaticAssetsDisplay {

    protected DynamicEntityTreeView treeDisplay;
    protected DynamicFormView treeDynamicFormDisplay;
    protected SubItemView listDisplay;
    protected Img previewImg;
    protected Canvas previewContainer;

    public StaticAssetsView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout treeGridLayout = new VLayout();
		treeGridLayout.setID("staticFolderAssetsGridLayout");
		treeGridLayout.setHeight100();
		treeGridLayout.setWidth("40%");
		treeGridLayout.setShowResizeBar(true);

		treeDisplay = new DynamicEntityTreeView(BLCMain.getMessageManager().getString("pagesTitle"), entityDataSource, true);
        treeDisplay.setHeight("80%");
        treeDisplay.setShowResizeBar(true);
        treeDisplay.getToolBar().getMember(6).destroy();
        treeDynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("detailsTitle"), entityDataSource);
        treeDynamicFormDisplay.setHeight("20%");
        treeGridLayout.addMember(treeDisplay);
        treeGridLayout.addMember(treeDynamicFormDisplay);

        VLayout listGridLayout = new VLayout();
		listGridLayout.setID("staticAssetsGridLayout");
		listGridLayout.setHeight100();
		listGridLayout.setWidth("60%");

        listDisplay = new SubItemView(BLCMain.getMessageManager().getString("pagesTitle"), false, true, true);
        listDisplay.getToolbar().getMember(6).destroy();
        HLayout previewSection = new HLayout();
        Canvas spacer = new Canvas();
        spacer.setWidth(20);
        spacer.setHeight(60);
        previewSection.addMember(spacer);
        previewContainer = new Canvas();
        previewContainer.setWidth(60);
        previewContainer.setHeight(60);
        previewContainer.setBorder("1px solid black");
        previewSection.addMember(previewContainer);
        previewImg = new Img();
        previewImg.setImageType(ImageStyle.CENTER);
        //myImage.setAppImgDir("pieces/48/");
        //myImage.setLeft(120);
        //myImage.setTop(20);
        previewContainer.addChild(previewImg);
        previewContainer.setVisible(false);
        ((FormOnlyView) listDisplay.getFormOnlyDisplay()).addMember(previewSection);

        listGridLayout.addMember(listDisplay);

        addMember(treeGridLayout);
        addMember(listGridLayout);
	}

    public Canvas asCanvas() {
		return this;
	}

	@Override
    public DynamicEntityListDisplay getListDisplay() {
		return treeDisplay;
	}

    @Override
    public DynamicFormDisplay getDynamicFormDisplay() {
		return treeDynamicFormDisplay;
	}

    @Override
    public Img getPreviewImg() {
        return previewImg;
    }

    @Override
    public Canvas getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public SubItemDisplay getListLeafDisplay() {
		return listDisplay;
	}

}
