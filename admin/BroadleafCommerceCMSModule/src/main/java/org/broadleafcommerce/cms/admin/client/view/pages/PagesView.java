/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.client.view.pages;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripSeparator;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
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
    protected ComboBoxItem currentLocale = new ComboBoxItem();
    protected ToolStripButton addPageFolderButton;
    protected ToolStripButton addPageButton;

    public PagesView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("pagesLeftVerticalLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);

		listDisplay = new DynamicEntityTreeView(BLCMain.getMessageManager().getString("pagesTitle"), entityDataSource, true);
        listDisplay.getGrid().setHoverMoveWithMouse(true);
        listDisplay.getGrid().setCanHover(true);
        listDisplay.getGrid().setShowHover(true);
        listDisplay.getGrid().setHoverOpacity(75);
        listDisplay.getGrid().setHoverCustomizer(new HoverCustomizer() {
            @Override
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (record.getAttribute("lockedFlag") != null && record.getAttributeAsBoolean("lockedFlag")) {
                    return BLCMain.getMessageManager().replaceKeys(BLCMain.getMessageManager().getString("lockedMessage"), new String[]{"userName", "date"}, new String[]{record.getAttribute("auditable.updatedBy.name"), record.getAttribute("auditable.dateUpdated")});
                }
                return null;
            }
        });
        Canvas[] members = listDisplay.getToolBar().getMembers();

        currentLocale.setShowTitle(false);
        currentLocale.setWidth(120);
        currentLocale.setOptionDataSource(additionalDataSources[0]);
        currentLocale.setDisplayField("friendlyName");
        currentLocale.setValueField("localeCode");
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

	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

    public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}

    public ToolStripButton getAddPageButton() {
        return addPageButton;
    }

    public void setAddPageButton(ToolStripButton addPageButton) {
        this.addPageButton = addPageButton;
    }

    public ToolStripButton getAddPageFolderButton() {
        return addPageFolderButton;
    }

    public void setAddPageFolderButton(ToolStripButton addPageFolderButton) {
        this.addPageFolderButton = addPageFolderButton;
    }

    public ComboBoxItem getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(ComboBoxItem currentLocale) {
        this.currentLocale = currentLocale;
    }
}
