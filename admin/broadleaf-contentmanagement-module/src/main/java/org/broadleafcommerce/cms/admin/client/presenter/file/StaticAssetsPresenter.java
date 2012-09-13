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
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.broadleafcommerce.cms.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.file.StaticAssetsDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.FileUploadDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.AssetItem;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class StaticAssetsPresenter extends DynamicEntityPresenter implements Instantiable {

    public static FileUploadDialog FILE_UPLOAD = new FileUploadDialog();

    protected Record currentSelectedRecord;
    protected String currentId;
    protected HandlerRegistration saveButtonHandlerRegistration;

    @Override
	protected void changeSelection(Record selectedRecord) {
        if (!selectedRecord.getAttributeAsBoolean("lockedFlag")) {
            getDisplay().getListDisplay().getRemoveButton().enable();
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().enable();
        } else {
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
            getDisplay().getListDisplay().getRemoveButton().disable();
        }
        currentSelectedRecord = selectedRecord;
        currentId = getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS").getPrimaryKeyValue(currentSelectedRecord);
        getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("name").disable();
        getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("fullUrl").disable();
        AssetItem assetItem = (AssetItem) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("pictureLarge");
        assetItem.updateState();
	}

    @Override
    protected void addClicked() {
        initialValues = new HashMap<String, Object>(10);
        initialValues.put("operation", "add");
        initialValues.put("customCriteria", "assetListUi");
        initialValues.put("ceilingEntityFullyQualifiedClassname", CeilingEntities.STATICASSETS);
        addNewItem(BLCMain.getMessageManager().getString("newItemTitle"));
    }

	@Override
	protected void addNewItem(String newItemTitle) {
        initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
        initialValues.put("csrfToken", BLCMain.csrfToken);
        compileDefaultValuesFromCurrentFilter(initialValues);
        Map<String, String> hints = new HashMap<String, String>();
        hints.put("name", BLCMain.getMessageManager().getString("assetUploadNameHint"));
        hints.put("fullUrl", BLCMain.getMessageManager().getString("assetUploadFullUrlHint"));
		FILE_UPLOAD.editNewRecord("Upload Artifact", getPresenterSequenceSetupManager().getDataSource("staticAssetTreeDS"), initialValues, hints, new ItemEditedHandler() {
            public void onItemEdited(ItemEdited event) {
                ListGridRecord[] recordList = new ListGridRecord[]{(ListGridRecord) event.getRecord()};
                DSResponse updateResponse = new DSResponse();
                updateResponse.setData(recordList);
                DSRequest updateRequest = new DSRequest();
                updateRequest.setOperationType(DSOperationType.UPDATE);
                getDisplay().getListDisplay().getGrid().getDataSource().updateCaches(updateResponse, updateRequest);
                getDisplay().getListDisplay().getGrid().deselectAllRecords();
                getDisplay().getListDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(event.getRecord()));
                String primaryKey = getDisplay().getListDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
                ResultSet results = getDisplay().getListDisplay().getGrid().getResultSet();
                boolean foundRecord = false;
                if (results != null) {
                    foundRecord = getDisplay().getListDisplay().getGrid().getResultSet().find(primaryKey, event.getRecord().getAttribute(primaryKey)) != null;
                }
                if (!foundRecord) {
                    ((AbstractDynamicDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).setAddedRecord(event.getRecord());
                    getDisplay().getListDisplay().getGrid().getDataSource().fetchData(new Criteria("blc.fetch.from.cache", event.getRecord().getAttribute(primaryKey)), new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            getDisplay().getListDisplay().getGrid().setData(response.getData());
                            getDisplay().getListDisplay().getGrid().selectRecord(0);
                        }
                    });
                }
            }
        }, null, new String[]{"file", "name", "fullUrl", "callbackName", "operation", "ceilingEntityFullyQualifiedClassname", "parentFolder", "customCriteria", "csrfToken"}, null);
	}

    @Override
    protected void removeClicked() {
        super.removeClicked();
        clearAssetPreviewImage();
    }

    @Override
	public void bind() {
		super.bind();
        if (!FILE_UPLOAD.isDrawn()) {
            FILE_UPLOAD.draw();
            FILE_UPLOAD.hide();
        }
        getSaveButtonHandlerRegistration().removeHandler();
        saveButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getSaveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //save the regular entity form and the page template form
                if (event.isLeftButtonDown()) {
                    DSRequest requestProperties = new DSRequest();
					//requestProperties.setAttribute("dirtyValues", getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getChangedValues());
					getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().saveData(new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            if (response.getStatus()!= RPCResponse.STATUS_FAILURE) {
                                final String newId = response.getAttribute("newId");
                                getDisplay().getDynamicFormDisplay().getSaveButton().disable();
                                getDisplay().getDynamicFormDisplay().getRefreshButton().disable();
                                if (!currentId.equals(newId)) {
                                    Record myRecord = getDisplay().getListDisplay().getGrid().getResultSet().find("id", currentId);
                                    if (myRecord != null) {
                                        myRecord.setAttribute("id", newId);
                                        currentSelectedRecord = myRecord;
                                        currentId = newId;
                                    }  else {
                                        String primaryKey = getDisplay().getListDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
                                        getDisplay().getListDisplay().getGrid().getDataSource().
                                            fetchData(new Criteria(primaryKey, newId), new DSCallback() {
                                                @Override
                                                public void execute(DSResponse response, Object rawData, DSRequest request) {
                                                    getDisplay().getListDisplay().getGrid().clearCriteria();
                                                    getDisplay().getListDisplay().getGrid().setData(response.getData());
                                                    getDisplay().getListDisplay().getGrid().selectRecord(0);
                                                }
                                            });
                                        SC.say(BLCMain.getMessageManager().getString("criteriaDoesNotMatch"));
                                    }
                                }
                                getDisplay().getListDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(currentSelectedRecord));
							}
                        }
                    }, requestProperties);
                }
            }
        });
        display.getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                clearAssetPreviewImage();
            }
        });
	}

    protected void clearAssetPreviewImage() {
        AssetItem assetItem = (AssetItem) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("pictureLarge");
        assetItem.clearImage();
    }

    public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTreeDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                setupDisplayItems(dataSource);
                ((ListGridDataSource) dataSource).setupGridFields(new String[]{"picture", "name", "fullUrl", "fileSize", "mimeType"});
            }
        }));
	}

	@Override
	public StaticAssetsDisplay getDisplay() {
		return (StaticAssetsDisplay) display;
	}
	
}
