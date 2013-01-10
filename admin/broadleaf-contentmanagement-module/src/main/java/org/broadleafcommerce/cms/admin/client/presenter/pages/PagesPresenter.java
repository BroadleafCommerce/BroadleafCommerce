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

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateFormListDataSource;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateFormListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateSearchListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter;
import org.broadleafcommerce.cms.admin.client.view.pages.PagesDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextCanvasItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextHTMLPane;

/**
 * 
 * @author jfischer
 *
 */
public class PagesPresenter extends HtmlEditingPresenter implements Instantiable {

    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration refreshButtonHandlerRegistration;
    protected Record currentPageRecord;
    protected String currentPageId;
    protected EntitySearchDialog pageTemplateDialogView;

    @Override
    protected void removeClicked() {
        SC.confirm("Are your sure you want to delete this entity?", new BooleanCallback() {
            public void execute(Boolean value) {
                if (value) {
                    display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            destroyTemplateForm();
                            formPresenter.disable();
                            display.getListDisplay().getRemoveButton().disable();
                        }
                    }, null);
                }
            }
        });
    }

    protected void destroyTemplateForm() {
        Canvas legacyForm = ((FormOnlyView) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay()).getMember("pageTemplateForm");
        if (legacyForm != null) {
            legacyForm.destroy();
        }
    }

    @Override
    protected void changeSelection(Record selectedRecord) {
        if (!selectedRecord.getAttributeAsBoolean("lockedFlag")) {
            getDisplay().getListDisplay().getRemoveButton().enable();
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().enable();
        } else {
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
            getDisplay().getListDisplay().getRemoveButton().disable();
        }
        currentPageRecord = selectedRecord;
        currentPageId = getPresenterSequenceSetupManager().getDataSource("pageDS").getPrimaryKeyValue(currentPageRecord);
        loadTemplateForm(selectedRecord);

    }

    protected void loadTemplateForm(final Record selectedRecord) {
        //load the page template form
        BLCMain.NON_MODAL_PROGRESS.startProgress();
        PageTemplateFormListDataSourceFactory.createDataSource("pageTemplateFormDS", new String[]{"constructForm", selectedRecord.getAttribute("pageTemplate")}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(final DataSource dataSource) {
                destroyTemplateForm();
                final FormOnlyView formOnlyView = new FormOnlyView(dataSource, true, true, false);
                formOnlyView.getForm().addItemChangedHandler(new ItemChangedHandler() {
                    public void onItemChanged(ItemChangedEvent event) {
                        getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                        getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                    }
                });
                formOnlyView.setID("pageTemplateForm");
                formOnlyView.setOverflow(Overflow.VISIBLE);
                ((FormOnlyView) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay()).addMember(formOnlyView);
                ((PageTemplateFormListDataSource) dataSource).setCustomCriteria(new String[]{"constructForm", selectedRecord.getAttribute("id")});
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
        getSaveButtonHandlerRegistration().removeHandler();
        formPresenter.getRefreshButtonHandlerRegistration().removeHandler();
        refreshButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getRefreshButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().reset();
                    FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay()).getMember("pageTemplateForm");
                    if (legacyForm != null) {
                        legacyForm.getForm().reset();
                    }

                    for (FormItem formItem : legacyForm.getForm().getFields()) {
                        if (formItem instanceof RichTextCanvasItem) {
                            formItem.setValue(legacyForm.getForm().getValue(formItem.getFieldName()));
                        }
                    }
                    getDisplay().getDynamicFormDisplay().getSaveButton().disable();
                    getDisplay().getDynamicFormDisplay().getRefreshButton().disable();
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
                            if (response.getStatus()!= RPCResponse.STATUS_FAILURE) {
                                final String newId = response.getAttribute("newId");
                                FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay()).getMember("pageTemplateForm");
                                final DynamicForm form = legacyForm.getForm();
                                for (FormItem formItem : form.getFields()) {
                                    if (formItem instanceof RichTextCanvasItem) {
                                        form.setValue(formItem.getFieldName(), ((RichTextHTMLPane)((RichTextCanvasItem) formItem).getCanvas()).getValue());
                                    }
                                }
                                PageTemplateFormListDataSource dataSource = (PageTemplateFormListDataSource) form.getDataSource();
                                dataSource.setCustomCriteria(new String[]{"constructForm", newId});
                                form.saveData(new DSCallback() {
                                    @Override
                                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                                        if (response.getStatus()!=RPCResponse.STATUS_FAILURE) {
                                            getDisplay().getDynamicFormDisplay().getSaveButton().disable();
                                            getDisplay().getDynamicFormDisplay().getRefreshButton().disable();
                                            if (!currentPageId.equals(newId)) {
                                                Record myRecord = getDisplay().getListDisplay().getGrid().getResultSet().find("id", currentPageId);
                                                myRecord.setAttribute("id", newId);
                                                currentPageRecord = myRecord;
                                                currentPageId = newId;
                                            }
                                            getDisplay().getListDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(currentPageRecord));
                                        }
                                    }
                                });
                            }
                        }
                    }, requestProperties);
                }
            }
        });
        display.getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                destroyTemplateForm();
            }
        });
    }


    public void setup() {
        super.setup();
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageDS", new PageDataSourceFactory(), new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{"locked", "fullUrl", "description", "pageTemplate_Grid"});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTemplateSearchDS", new PageTemplateSearchListDataSourceFactory(), new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource result) {
                ListGridDataSource pageTemplateDataSource = (ListGridDataSource) result;
                pageTemplateDataSource.resetPermanentFieldVisibility(
                    "templateName",
                    "templatePath"
                );
                EntitySearchDialog pageTemplateSearchView = new EntitySearchDialog(pageTemplateDataSource, true);
                pageTemplateDialogView = pageTemplateSearchView;
                getPresenterSequenceSetupManager().getDataSource("pageDS").
                getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                        "pageTemplate",
                        pageTemplateSearchView,
                        "Page Template Search",
                        getDisplay().getDynamicFormDisplay(),
                        new FormItemCallback() {
                            @Override
                            public void execute(FormItem formItem) {
                                if (currentPageRecord != null && BLCMain.ENTITY_ADD.getHidden()) {
                                    destroyTemplateForm();
                                    loadTemplateForm(currentPageRecord);
                                }
                            }
                        }
                );
            }
        }));
    }

    @Override
    public PagesDisplay getDisplay() {
        return (PagesDisplay) display;
    }

}
