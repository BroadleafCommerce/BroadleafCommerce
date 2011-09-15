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
import com.smartgwt.client.data.*;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import org.broadleafcommerce.cms.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsFolderTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.file.StaticAssetsDisplay;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.FileUploadDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.ArtifactItem;

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
    protected SubPresentable leafAssetPresenter;

    @Override
	protected void changeSelection(final Record selectedRecord) {
		leafAssetPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS"), new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				try {
					if (response.getErrors().size() > 0) {
						leafAssetPresenter.disable();
					}
				} catch (Exception e) {
					leafAssetPresenter.enable();
                    leafAssetPresenter.setStartState();
                    display.getListDisplay().getAddButton().enable();
                    resetForm();
				}
			}
		});
	}

    @Override
	public void bind() {
		super.bind();
        leafAssetPresenter.bind();
        leafAddClickHandlerRegistration = getDisplay().getListLeafDisplay().getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
                    getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.STATICASSETIMPL);
					Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("operation", "add");
                    initialValues.put("sandbox", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").createSandBoxInfo().getSandBox());
                    initialValues.put("ceilingEntityFullyQualifiedClassname", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").getDefaultNewEntityFullyQualifiedClassname());
                    initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(getDisplay().getListDisplay().getGrid().getSelectedRecord()));
                    FILE_UPLOAD.editNewRecord("Upload Artifact", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            Criteria myCriteria = new Criteria();
				            myCriteria.addCriteria("name", event.getRecord().getAttribute("name"));
				            getDisplay().getListLeafDisplay().getGrid().fetchData(myCriteria);
                            resetForm();
                        }
                    }, null, new String[]{"file", "name", "callbackName", "operation", "sandbox", "ceilingEntityFullyQualifiedClassname", "parentFolder"}, null);
				}
			}
        });
        getDisplay().getListLeafDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
                    ArtifactItem artifactItem = (ArtifactItem) getDisplay().getListLeafDisplay().getFormOnlyDisplay().getForm().getField("pictureLarge");
                    artifactItem.setPreviewSrc(getDisplay().getListLeafDisplay().getFormOnlyDisplay().getForm().getField("pictureLarge").getValue().toString());
				}
			}
		});
        getDisplay().getListLeafDisplay().getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
					resetForm();
				}
            }
        });
        FILE_UPLOAD.draw();
        FILE_UPLOAD.hide();
	}

    public void resetForm() {
        getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").resetPermanentFieldVisibilityBasedOnType(new String[]{EntityImplementations.STATICASSETIMPL});
		getDisplay().getListLeafDisplay().getFormOnlyDisplay().buildFields(getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), true, false, false);
    }

    public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetFolderTreeDS", new StaticAssetsFolderTreeDataSourceFactory(), null, new Object[]{}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTreeDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                setupDisplayItems(getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS"), dataSource);
                leafAssetPresenter = new LeafAssetPresenter(getDisplay().getListLeafDisplay(), true, true, false);
                //((ListGridDataSource) dataSource).
				leafAssetPresenter.setDataSource((ListGridDataSource) dataSource, new String[]{"picture", "name", "fullUrl", "fileSize", "mimeType"}, new Boolean[]{false, true, false, false, false});
                ((ListGridDataSource) dataSource).getFormItemCallbackHandlerManager().addFormItemCallback("pictureLarge", new FormItemCallback() {
                        @Override
                        public void execute(FormItem formItem) {
                            getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.STATICASSETIMPL);
                            Map<String, Object> initialValues = new HashMap<String, Object>();
                            getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").resetVisibilityOnly();
                            initialValues.put("idHolder", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").getPrimaryKeyValue(getDisplay().getListLeafDisplay().getGrid().getSelectedRecord()));
                            initialValues.put("operation", "update");
                            initialValues.put("sandbox", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").createSandBoxInfo().getSandBox());
                            initialValues.put("ceilingEntityFullyQualifiedClassname", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").getDefaultNewEntityFullyQualifiedClassname());
                            initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(getDisplay().getListDisplay().getGrid().getSelectedRecord()));
                            FILE_UPLOAD.editNewRecord("Upload Artifact", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), initialValues, new NewItemCreatedEventHandler() {
                                public void onNewItemCreated(NewItemCreatedEvent event) {
                                    final Record selectedRow = getDisplay().getListLeafDisplay().getGrid().getSelectedRecord();
                                    final int index = getDisplay().getListLeafDisplay().getGrid().getRecordIndex(selectedRow);
                                    getDisplay().getListLeafDisplay().getGrid().setData(new Record[]{});
                                    getDisplay().getListLeafDisplay().getGrid().fetchData(getDisplay().getListLeafDisplay().getGrid().getCriteria(), new DSCallback() {
                                        @Override
                                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                                            getDisplay().getListLeafDisplay().getGrid().selectRecord(index);
                                        }
                                    });
                                }
                            }, null, new String[]{"file", "callbackName", "operation", "sandbox", "ceilingEntityFullyQualifiedClassname", "parentFolder", "idHolder"}, null);
                        }
                    }
                );
            }
        }));
	}

	@Override
	public StaticAssetsDisplay getDisplay() {
		return (StaticAssetsDisplay) display;
	}
	
}
