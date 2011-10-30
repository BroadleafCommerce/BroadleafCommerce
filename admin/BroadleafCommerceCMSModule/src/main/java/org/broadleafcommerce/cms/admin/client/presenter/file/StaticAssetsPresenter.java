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
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.cms.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.cms.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetDescriptionMapDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsFolderTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.LocaleListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.file.StaticAssetsDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenterWithoutForm;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.MapStructurePresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.FileUploadDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.MapStructureEntityEditDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.AssetItem;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class StaticAssetsPresenter extends DynamicEntityPresenterWithoutForm implements Instantiable {

    public static FileUploadDialog FILE_UPLOAD = new FileUploadDialog();

    protected MapStructureEntityEditDialog staticAssetDescriptionEntityAdd;
    protected HandlerRegistration leafAddClickHandlerRegistration;
    protected SubPresentable leafAssetPresenter;
    protected SubPresentable staticAssetDescriptionPresenter;
    protected TreeNode currentSelectedRecord;

    @Override
	protected void changeSelection(Record selectedRecord) {
        currentSelectedRecord = (TreeNode) selectedRecord;
        if (selectedRecord.getAttributeAsStringArray("_type") == null) {
            selectedRecord.setAttribute("_type", new String[]{EntityImplementations.STATICASSETIMPL});
        }
		leafAssetPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS"), new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
                if (response.getStatus()== RPCResponse.STATUS_FAILURE) {
				    leafAssetPresenter.disable();
				} else {
					leafAssetPresenter.enable();
                    leafAssetPresenter.setStartState();
                    display.getListDisplay().getAddButton().enable();
                    resetForm();
				}
			}
		});
	}

    @Override
    protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>(2);
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(getDisplay().getListDisplay().getGrid().getSelectedRecord()));
        BLCMain.ENTITY_ADD.editNewRecord(newItemTitle, (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new ItemEditedHandler() {
			public void onItemEdited(ItemEdited event) {
                if (!((TreeGrid) getDisplay().getListDisplay().getGrid()).getTree().isOpen(currentSelectedRecord)) {
                   ((TreeGrid) getDisplay().getListDisplay().getGrid()).getTree().openFolder(currentSelectedRecord);
                }
                resetForm();
			}
		}, null, null);
	}

    @Override
	public void bind() {
		super.bind();
        leafAssetPresenter.bind();
        staticAssetDescriptionPresenter.bind();
        leafAddClickHandlerRegistration = getDisplay().getListLeafDisplay().getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
                    getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.STATICASSETIMPL);
					Map<String, Object> initialValues = new HashMap<String, Object>(4);
                    initialValues.put("operation", "add");
                    initialValues.put("customCriteria", "assetListUi");
                    initialValues.put("ceilingEntityFullyQualifiedClassname", CeilingEntities.STATICASSETS);
                    initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(getDisplay().getListDisplay().getGrid().getSelectedRecord()));
                    FILE_UPLOAD.editNewRecord("Upload Artifact", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), initialValues, new ItemEditedHandler() {
                        public void onItemEdited(ItemEdited event) {
                            ListGridRecord[] recordList = new ListGridRecord[]{event.getRecord()};
                            DSResponse updateResponse = new DSResponse();
                            updateResponse.setData(recordList);
                            getDisplay().getListLeafDisplay().getGrid().getDataSource().updateCaches(updateResponse);
                            getDisplay().getListLeafDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(event.getRecord()));
                            String primaryKey = getDisplay().getListLeafDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
                            boolean foundRecord = false;
                            for (Record record : getDisplay().getListLeafDisplay().getGrid().getRecords()) {
                                if (record.getAttribute(primaryKey).equals(event.getRecord().getAttribute(primaryKey))) {
                                    foundRecord = true;
                                    break;
                                }
                            }
                            if (!foundRecord) {
                                ((AbstractDynamicDataSource) getDisplay().getListLeafDisplay().getGrid().getDataSource()).setAddedRecord(event.getRecord());
                                getDisplay().getListLeafDisplay().getGrid().getDataSource().fetchData(new Criteria("blc.fetch.from.cache", event.getRecord().getAttribute(primaryKey)), new DSCallback() {
                                    @Override
                                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                                        getDisplay().getListLeafDisplay().getGrid().setData(response.getData());
                                        getDisplay().getListLeafDisplay().getGrid().selectRecord(0);
                                    }
                                });
                            }
                            //resetForm();
                        }
                    }, null, new String[]{"file", "name", "callbackName", "operation", "ceilingEntityFullyQualifiedClassname", "parentFolder", "customCriteria"}, null);
				}
			}
        });
        getDisplay().getListLeafDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
                    getDisplay().getListLeafDisplay().getFormOnlyDisplay().getForm().disable();
                    AssetItem assetItem = (AssetItem) getDisplay().getListLeafDisplay().getFormOnlyDisplay().getForm().getField("pictureLarge");
                    assetItem.setPreviewSrc(getDisplay().getListLeafDisplay().getFormOnlyDisplay().getForm().getField("pictureLarge").getValue().toString());
                    assetItem.setDisabled(true);
                    staticAssetDescriptionPresenter.enable();
                    staticAssetDescriptionPresenter.load(event.getSelectedRecord(), getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), null);
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
        if (!FILE_UPLOAD.isDrawn()) {
            FILE_UPLOAD.draw();
            FILE_UPLOAD.hide();
        }
	}

    public void resetForm() {
        getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").resetPermanentFieldVisibilityBasedOnType(new String[]{EntityImplementations.STATICASSETIMPL});
		getDisplay().getListLeafDisplay().getFormOnlyDisplay().buildFields(getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), true, false, false);
        staticAssetDescriptionPresenter.disable();
    }

    public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetFolderTreeDS", new StaticAssetsFolderTreeDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTreeDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                setupDisplayItems(getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS"), dataSource);
                leafAssetPresenter = new SubPresenter(getDisplay().getListLeafDisplay(), new String[]{EntityImplementations.STATICASSETIMPL}, true, true, false);
				leafAssetPresenter.setDataSource((ListGridDataSource) dataSource, new String[]{"picture", "name", "fullUrl", "fileSize", "mimeType"}, new Boolean[]{false, true, false, false, false});
                /*((ListGridDataSource) dataSource).getFormItemCallbackHandlerManager().addFormItemCallback("pictureLarge", new FormItemCallback() {
                        @Override
                        public void execute(FormItem formItem) {
                            getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.STATICASSETIMPL);
                            Map<String, Object> initialValues = new HashMap<String, Object>();
                            getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").resetVisibilityOnly();
                            initialValues.put("idHolder", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").getPrimaryKeyValue(getDisplay().getListLeafDisplay().getGrid().getSelectedRecord()));
                            initialValues.put("operation", "update");
                            initialValues.put("customCriteria", "assetListUi");
                            initialValues.put("sandbox", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").createSandBoxInfo().getSandBox());
                            initialValues.put("ceilingEntityFullyQualifiedClassname", CeilingEntities.STATICASSETS);
                            initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS").getPrimaryKeyValue(getDisplay().getListDisplay().getGrid().getSelectedRecord()));
                            FILE_UPLOAD.editNewRecord("Upload Artifact", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), initialValues, new ItemEditedHandler() {
                                public void onNewItemCreated(ItemEdited event) {
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
                            }, null, new String[]{"file", "callbackName", "operation", "sandbox", "ceilingEntityFullyQualifiedClassname", "parentFolder", "idHolder", "customCriteria"}, null);
                        }
                    }
                );*/
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("localeDS", new LocaleListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetDescriptionMapDS", new StaticAssetDescriptionMapDataSourceFactory(this), new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				staticAssetDescriptionPresenter = new MapStructurePresenter(getDisplay().getAssetDescriptionDisplay(), getStaticAssetDescriptionEntityView(), new String[]{EntityImplementations.STATICASSETIMPL}, BLCMain.getMessageManager().getString("newAssetDescriptionTitle"));
				staticAssetDescriptionPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "description", "longDescription"}, new Boolean[]{true, true, true});
			}
		}));
	}

    protected MapStructureEntityEditDialog getStaticAssetDescriptionEntityView() {
		 if (staticAssetDescriptionEntityAdd == null) {
			 staticAssetDescriptionEntityAdd = new MapStructureEntityEditDialog(StaticAssetDescriptionMapDataSourceFactory.MAPSTRUCTURE, getPresenterSequenceSetupManager().getDataSource("localeDS"), "friendlyName", "localeCode");
		 }
		 return staticAssetDescriptionEntityAdd;
	}

	@Override
	public StaticAssetsDisplay getDisplay() {
		return (StaticAssetsDisplay) display;
	}
	
}
