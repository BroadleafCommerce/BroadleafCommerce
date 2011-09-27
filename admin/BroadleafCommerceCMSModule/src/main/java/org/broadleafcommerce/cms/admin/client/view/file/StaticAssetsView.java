package org.broadleafcommerce.cms.admin.client.view.file;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeView;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

/**
 * Created by jfischer
 */
public class StaticAssetsView extends HLayout implements Instantiable, StaticAssetsDisplay {

    protected DynamicEntityTreeView treeDisplay;
    protected SubItemView listDisplay;
    protected GridStructureView assetDescriptionDisplay;

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

		treeDisplay = new DynamicEntityTreeView(BLCMain.getMessageManager().getString("staticAssetFoldersTitle"), entityDataSource, true);
        treeDisplay.setHeight("80%");
        treeDisplay.getToolBar().getMember(6).destroy();
        treeGridLayout.addMember(treeDisplay);

        VLayout listGridLayout = new VLayout();
		listGridLayout.setID("staticAssetsGridLayout");
		listGridLayout.setHeight100();
		listGridLayout.setWidth("60%");

        listDisplay = new SubItemView(BLCMain.getMessageManager().getString("staticAssetsTitle"), false, true, true);
        listDisplay.getSaveButton().setVisible(false);
        listGridLayout.addMember(listDisplay);

        assetDescriptionDisplay = new GridStructureView(BLCMain.getMessageManager().getString("assetDescriptionTitle"), false, true);
        ((FormOnlyView) listDisplay.getFormOnlyDisplay()).addMember(assetDescriptionDisplay);

        addMember(treeGridLayout);
        addMember(listGridLayout);
	}

    public Canvas asCanvas() {
		return this;
	}

    public DynamicEntityListDisplay getListDisplay() {
		return treeDisplay;
	}

    @Override
    public SubItemDisplay getListLeafDisplay() {
		return listDisplay;
	}

    @Override
    public GridStructureDisplay getAssetDescriptionDisplay() {
        return assetDescriptionDisplay;
    }
}
