package org.broadleafcommerce.cms.admin.client.view.file;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripSeparator;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityColumnTreeDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityColumnTreeView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * Created by jfischer
 */
public class StaticAssetsView extends VLayout implements Instantiable, StaticAssetsDisplay {

    protected DynamicEntityColumnTreeView listDisplay;
    protected DynamicFormView dynamicFormDisplay;
    protected ComboBoxItem currentLocale = new ComboBoxItem();
    protected ToolStripButton addPageFolderButton;
    protected ToolStripButton addPageButton;

    public StaticAssetsView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("pagesLeftVerticalLayout");
		leftVerticalLayout.setHeight("50%");
		leftVerticalLayout.setWidth100();
		leftVerticalLayout.setShowResizeBar(true);

		listDisplay = new DynamicEntityColumnTreeView(BLCMain.getMessageManager().getString("pagesTitle"), entityDataSource);
        Canvas[] members = listDisplay.getToolBar().getMembers();

        currentLocale.setShowTitle(false);
        currentLocale.setWidth(120);
        currentLocale.setOptionDataSource(additionalDataSources[0]);
        currentLocale.setDisplayField("friendlyName");
        currentLocale.setValueField("localeName");
        currentLocale.setDefaultToFirstOption(true);
        listDisplay.getToolBar().addFormItem(currentLocale, 6);
        listDisplay.getToolBar().getMember(7).destroy();
        listDisplay.getToolBar().getMember(1).destroy();
        addPageFolderButton = new ToolStripButton();
        addPageFolderButton.setDisabled(true);
        addPageFolderButton.setIcon(GWT.getModuleBaseURL()+"admin/images/button/folder_open.png");
        listDisplay.getToolBar().addButton(addPageFolderButton, 1);
        addPageButton = new ToolStripButton();
        addPageButton.setDisabled(true);
        addPageButton.setIcon(GWT.getModuleBaseURL()+"admin/images/button/file.png");
        listDisplay.getToolBar().addButton(addPageButton, 2);
        listDisplay.getToolBar().addMember(new ToolStripSeparator(), 3);

        leftVerticalLayout.addMember(listDisplay);

        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("detailsTitle"), entityDataSource);

        addMember(leftVerticalLayout);
        addMember(dynamicFormDisplay);
	}

    public Canvas asCanvas() {
		return this;
	}

	@Override
    public DynamicEntityColumnTreeDisplay getListDisplay() {
		return listDisplay;
	}

    @Override
    public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}

    @Override
    public ToolStripButton getAddPageButton() {
        return addPageButton;
    }

    @Override
    public void setAddPageButton(ToolStripButton addPageButton) {
        this.addPageButton = addPageButton;
    }

    @Override
    public ToolStripButton getAddPageFolderButton() {
        return addPageFolderButton;
    }

    @Override
    public void setAddPageFolderButton(ToolStripButton addPageFolderButton) {
        this.addPageFolderButton = addPageFolderButton;
    }

    @Override
    public ComboBoxItem getCurrentLocale() {
        return currentLocale;
    }

    @Override
    public void setCurrentLocale(ComboBoxItem currentLocale) {
        this.currentLocale = currentLocale;
    }
}
