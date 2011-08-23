package org.broadleafcommerce.cms.admin.client.view.pages;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagesView extends HLayout implements Instantiable, PagesDisplay {

    protected DynamicEntityTreeView listDisplay;
    protected DynamicFormView dynamicFormDisplay;

    public PagesView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("pagesLeftVerticalLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);

		listDisplay = new DynamicEntityTreeView(BLCMain.getMessageManager().getString("pagesTitle"), entityDataSource);
		listDisplay.setShowResizeBar(true);
        leftVerticalLayout.addMember(listDisplay);

        TabSet topTabSet = new TabSet();
        topTabSet.setID("pagesTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);

        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("detailsTitle"));
        detailsTab.setID("pagesDetailsTab");
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("detailsTitle"), entityDataSource);
        detailsTab.setPane(dynamicFormDisplay);

        topTabSet.addTab(detailsTab);

        addMember(leftVerticalLayout);
        addMember(topTabSet);
	}

    public Canvas asCanvas() {
		return this;
	}

	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

    public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
}
