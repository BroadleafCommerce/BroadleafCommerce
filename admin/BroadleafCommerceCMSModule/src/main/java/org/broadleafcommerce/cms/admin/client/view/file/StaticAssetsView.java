package org.broadleafcommerce.cms.admin.client.view.file;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * Created by jfischer
 */
public class StaticAssetsView extends HLayout implements Instantiable, StaticAssetsDisplay {

    protected DynamicEntityTreeView treeDisplay;
    protected DynamicFormView treeDynamicFormDisplay;
    protected DynamicEntityListView listDisplay;
    protected DynamicFormView listDynamicFormDisplay;

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
		listGridLayout.setShowResizeBar(true);

		listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("pagesTitle"), additionalDataSources[0]);
        listDisplay.setHeight("60%");
        listDisplay.setShowResizeBar(true);
        listDynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("detailsTitle"), additionalDataSources[0]);
        listDynamicFormDisplay.setHeight("40%");
        listGridLayout.addMember(listDisplay);
        listGridLayout.addMember(listDynamicFormDisplay);

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
    public DynamicEntityListDisplay getListLeafDisplay() {
		return listDisplay;
	}

    @Override
    public DynamicFormDisplay getDynamicFormLeafDisplay() {
		return listDynamicFormDisplay;
	}
}
