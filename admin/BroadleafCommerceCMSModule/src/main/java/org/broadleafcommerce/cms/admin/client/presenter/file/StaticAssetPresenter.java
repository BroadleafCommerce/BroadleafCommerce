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
package org.broadleafcommerce.cms.admin.client.presenter.file;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import org.broadleafcommerce.cms.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PagesTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.file.StaticAssetsDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicColumnTreePresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class StaticAssetPresenter extends DynamicColumnTreePresenter implements Instantiable {

    protected Record currentPageRecord;

    @Override
	protected void changeSelection(final Record selectedRecord) {
        String pageType = selectedRecord.getAttributeAsStringArray("_type")==null?null:selectedRecord.getAttributeAsStringArray("_type")[0];
        if (pageType!=null && pageType.equals("org.broadleafcommerce.cms.file.domain.StaticAssetImpl")) {
            getDisplay().getAddPageButton().disable();
            getDisplay().getAddPageFolderButton().disable();
            currentPageRecord = selectedRecord;
        } else {
            getDisplay().getAddPageButton().enable();
            getDisplay().getAddPageFolderButton().enable();
            currentPageRecord = null;
        }
	}

    @Override
	public void bind() {
		super.bind();
        getDisplay().getAddPageButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.PAGEIMPL);
                    Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
                    initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
                    initialValues.put("_type", new String[]{EntityImplementations.PAGEIMPL});
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS"), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            //TODO will probably have to get the column tree to have some remember state functionality
                        }
                    }, "90%", null, null);
                }
            }
        });
        getDisplay().getAddPageFolderButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.PAGEFOLDERIMPL);
                    Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
                    initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
                    initialValues.put("_type", new String[]{EntityImplementations.PAGEFOLDERIMPL});
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS"), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            //TODO will probably have to get the column tree to have some remember state functionality
                        }
                    }, "90%", null, null);
                }
            }
        });
	}

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetFolderTreeDS", new PagesTreeDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                setupDisplayItems(dataSource);
            }
        }));
	}

	@Override
	public StaticAssetsDisplay getDisplay() {
		return (StaticAssetsDisplay) display;
	}
	
}
