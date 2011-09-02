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

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsFolderTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.file.FileUploadDialog;
import org.broadleafcommerce.cms.admin.client.view.file.StaticAssetsDisplay;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class StaticAssetsPresenter extends DynamicEntityPresenter implements Instantiable {

    public static FileUploadDialog FILE_UPLOAD = new FileUploadDialog();
    protected HandlerRegistration leafAddClickHandlerRegistration;

    @Override
	public void bind() {
		super.bind();
        leafAddClickHandlerRegistration = getDisplay().getListLeafDisplay().getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("_type", new String[]{getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").getDefaultNewEntityFullyQualifiedClassname()});
                    initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(getDisplay().getListDisplay().getGrid().getSelectedRecord()));
                    FILE_UPLOAD.editNewRecord("Upload Artifact", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            //do something
                        }
                    }, "90%", new String[]{"file", "name"}, null);
				}
			}
        });
	}

    public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetFolderTreeDS", new StaticAssetsFolderTreeDataSourceFactory(), null, new Object[]{}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTreeDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                setupDisplayItems(getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS"), dataSource);
            }
        }));
	}

	@Override
	public StaticAssetsDisplay getDisplay() {
		return (StaticAssetsDisplay) display;
	}
	
}
