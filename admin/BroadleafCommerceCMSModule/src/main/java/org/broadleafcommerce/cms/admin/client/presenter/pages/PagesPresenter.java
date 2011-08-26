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
package org.broadleafcommerce.cms.admin.client.presenter.pages;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PagesTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.pages.PagesDisplay;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;

import java.util.HashMap;

/**
 * 
 * @author jfischer
 *
 */
public class PagesPresenter extends DynamicEntityPresenter implements Instantiable {

	protected String rootId = null;
	protected String rootName = "Root";
	protected HashMap<String, Object> library = new HashMap<String, Object>();


	/*@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("parentFolder", ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
		initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				//do nothing
			}
		}, "90%", null, null);
	}*/

	@Override
	protected void removeClicked() {
		/*display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				getDisplay().getOrphanedCategoryGrid().invalidateCache();
				getDisplay().getRemoveOrphanedButton().disable();
				getDisplay().getInsertOrphanButton().disable();
			}
		}, null);
		formPresenter.disable();
		display.getListDisplay().getRemoveButton().disable();
		allChildCategoriesPresenter.disable();*/
	}

	@Override
	protected void changeSelection(final Record selectedRecord) {
        String pageType = selectedRecord.getAttributeAsStringArray("_type")[0];
        if (pageType.equals("org.broadleafcommerce.cms.page.domain.PageImpl")) {
            getDisplay().getAddPageButton().disable();
            getDisplay().getAddPageFolderButton().disable();
        } else {
            getDisplay().getAddPageButton().enable();
            getDisplay().getAddPageFolderButton().enable();
        }
	}

	@Override
	public void bind() {
		super.bind();
        getDisplay().getAddPageButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    //do something
                }
            }
        });
        getDisplay().getAddPageFolderButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    //do something
                }
            }
        });
	}

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pagesTreeDS", new PagesTreeDataSourceFactory(), null, new Object[]{rootId, rootName}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems(top);
				((TreeGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{}, "250", "100");
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTemplateSearch", new PageTemplateListDataSourceFactory(), new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				ListGridDataSource pageTemplateDataSource = (ListGridDataSource) result;
				pageTemplateDataSource.resetProminenceOnly(
					"templateName",
					"templatePath",
					"languageCode"
				);
				EntitySearchDialog pageTemplateSearchView = new EntitySearchDialog(pageTemplateDataSource);
				((DynamicEntityDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).
				getFormItemCallbackHandlerManager().addSearchFormItemCallback(
					"pageTemplate",
					pageTemplateSearchView,
					"Page Template Search",
					getDisplay().getDynamicFormDisplay()
				);
			}
		}));
	}

	@Override
	public PagesDisplay getDisplay() {
		return (PagesDisplay) display;
	}
	
}
