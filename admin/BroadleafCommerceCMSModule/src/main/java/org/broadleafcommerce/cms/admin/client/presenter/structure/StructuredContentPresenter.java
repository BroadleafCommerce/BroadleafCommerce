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

package org.broadleafcommerce.cms.admin.client.presenter.structure;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.*;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import org.broadleafcommerce.cms.admin.client.datasource.structure.StructuredContentListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.structure.StructuredContentTypeFormListDataSource;
import org.broadleafcommerce.cms.admin.client.datasource.structure.StructuredContentTypeFormListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.structure.StructuredContentTypeSearchListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter;
import org.broadleafcommerce.cms.admin.client.view.structure.StructuredContentDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextCanvasItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextHTMLPane;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class StructuredContentPresenter extends HtmlEditingPresenter implements Instantiable {

    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration refreshButtonHandlerRegistration;
    protected Record currentStructuredContentRecord;
    protected boolean isFetched = false;

	@Override
	protected void removeClicked() {
		display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
            @Override
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                destroyContentTypeForm();
                formPresenter.disable();
		        display.getListDisplay().getRemoveButton().disable();
            }
        }, null);
	}

    protected void destroyContentTypeForm() {
        Canvas legacyForm = ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).getMember("contentTypeForm");
        if (legacyForm != null) {
            legacyForm.destroy();
        }
    }

    @Override
    protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
        initialValues.put("priority", new Integer(5));
		BLCMain.ENTITY_ADD.editNewRecord(newItemTitle, (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
            public void onNewItemCreated(NewItemCreatedEvent event) {
                Criteria myCriteria = new Criteria();
                myCriteria.addCriteria("contentName", event.getRecord().getAttribute("contentName"));
                display.getListDisplay().getGrid().fetchData(myCriteria, new DSCallback() {
                    @Override
                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                        getDisplay().getListDisplay().getGrid().selectRecord(0);
                    }
                });
            }
        }, "90%", null, null);
	}

    @Override
	protected void changeSelection(final Record selectedRecord) {
        if (!selectedRecord.getAttributeAsBoolean("lockedFlag")) {
            getDisplay().getListDisplay().getRemoveButton().enable();
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().enable();
        } else {
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
            getDisplay().getListDisplay().getRemoveButton().disable();
        }
        currentStructuredContentRecord = selectedRecord;
        loadContentTypeForm(selectedRecord);
	}

    protected void loadContentTypeForm(final Record selectedRecord) {
        //load the page template form
        BLCMain.NON_MODAL_PROGRESS.startProgress();
        StructuredContentTypeFormListDataSourceFactory.createDataSource("contentTypeFormDS", new String[]{"constructForm", selectedRecord.getAttribute("structuredContentType")}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(final DataSource dataSource) {
                destroyContentTypeForm();
                final FormOnlyView formOnlyView = new FormOnlyView(dataSource, true, true, false);
                formOnlyView.getForm().addItemChangedHandler(new ItemChangedHandler() {
                    public void onItemChanged(ItemChangedEvent event) {
                        getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                    }
                });
                formOnlyView.setID("contentTypeForm");
                formOnlyView.setOverflow(Overflow.VISIBLE);
                ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).addMember(formOnlyView);
                ((StructuredContentTypeFormListDataSource) dataSource).setCustomCriteria(new String[]{"constructForm", selectedRecord.getAttribute("id")});
                BLCMain.NON_MODAL_PROGRESS.startProgress();
                formOnlyView.getForm().fetchData(new Criteria(), new DSCallback() {
                    @Override
                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                        if (!selectedRecord.getAttributeAsBoolean("lockedFlag")) {
                            formOnlyView.getForm().enable();
                        }
                        for (FormItem formItem : formOnlyView.getForm().getFields()) {
                            if (formItem instanceof RichTextCanvasItem) {
                                formItem.setValue(formOnlyView.getForm().getValue(formItem.getFieldName()));
                            }

                        }
                    }
                });
            }
        });
    }

    @Override
	public void bind() {
		super.bind();
        formPresenter.getSaveButtonHandlerRegistration().removeHandler();
        formPresenter.getRefreshButtonHandlerRegistration().removeHandler();
        refreshButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getRefreshButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().reset();
                    FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).getMember("contentTypeForm");
                    if (legacyForm != null) {
                        legacyForm.getForm().reset();
                    }
					getDisplay().getDynamicFormDisplay().getSaveButton().disable();
				}
			}
        });
        saveButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getSaveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //save the regular entity form and the page template form
                if (event.isLeftButtonDown()) { 
                    DSRequest requestProperties = new DSRequest();
                    requestProperties.setAttribute("dirtyValues", getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getChangedValues());

                    getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().saveData(new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            if (response.getStatus()!=RPCResponse.STATUS_FAILURE) {
                                final Record newRecord = response.getData()[0];
                                final String newId = getPresenterSequenceSetupManager().getDataSource("structuredContentDS").getPrimaryKeyValue(newRecord);
                                FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).getMember("contentTypeForm");
                                final DynamicForm form = legacyForm.getForm();
                                for (FormItem formItem : form.getFields()) {
                                    if (formItem instanceof RichTextCanvasItem) {
                                        form.setValue(formItem.getFieldName(), ((RichTextHTMLPane)((RichTextCanvasItem) formItem).getCanvas()).getValue());
                                    }
                                }
                                StructuredContentTypeFormListDataSource dataSource = (StructuredContentTypeFormListDataSource) form.getDataSource();
                                dataSource.setCustomCriteria(new String[]{"constructForm", newId});
                                form.saveData(new DSCallback() {
                                    @Override
                                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                                        if (response.getStatus()!=RPCResponse.STATUS_FAILURE) {
                                            getDisplay().getDynamicFormDisplay().getSaveButton().disable();
                                        }
                                    }
                                });
                                if (!getPresenterSequenceSetupManager().getDataSource("structuredContentDS").getPrimaryKeyValue(currentStructuredContentRecord).equals(newId)) {
                                    display.getListDisplay().getGrid().getRecordList().remove(currentStructuredContentRecord);
                                    currentStructuredContentRecord = newRecord;
                                }
							}
                        }
                    }, requestProperties);
                }
            }
        });
        getDisplay().getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                isFetched = true;
            }
        });
	}

	public void setup() {
        super.setup();
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("structuredContentDS", new StructuredContentListDataSourceFactory(), null, new Object[]{}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("structuredContentTypeSearchDS", new StructuredContentTypeSearchListDataSourceFactory(), new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				ListGridDataSource structuredContentTypeDataSource = (ListGridDataSource) result;
				structuredContentTypeDataSource.resetPermanentFieldVisibility(
					"name","description"
				);
				EntitySearchDialog structuredContentTypeSearchView = new EntitySearchDialog(structuredContentTypeDataSource, true);
                setupDisplayItems(getPresenterSequenceSetupManager().getDataSource("structuredContentDS"), result);
				getPresenterSequenceSetupManager().getDataSource("structuredContentDS").
				getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                        "structuredContentType",
                        structuredContentTypeSearchView,
                        "Structured Content Type Search",
                        getDisplay().getDynamicFormDisplay(),
                        new FormItemCallback() {
                            @Override
                            public void execute(FormItem formItem) {
                                if (currentStructuredContentRecord != null) {
                                    destroyContentTypeForm();
                                    loadContentTypeForm(currentStructuredContentRecord);
                                }
                            }
                        }
                );
                ((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("structuredContentDS")).setupGridFields(new String[]{"locked", "structuredContentTypeGrid", "contentName", "locale", "offlineFlag"});
			}
		}));
	}

	@Override
	public StructuredContentDisplay getDisplay() {
		return (StructuredContentDisplay) display;
	}
	
}
