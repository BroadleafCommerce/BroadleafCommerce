/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.events.FilterChangedEvent;
import com.smartgwt.client.widgets.form.events.FilterChangedHandler;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateFormListDataSource;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateFormListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateSearchListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.structure.CustomerListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.structure.OrderItemListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.structure.ProductListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.structure.RequestDTOListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.structure.TimeDTOListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.pages.PagesDisplay;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.AdditionalFilterEventManager;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterBuilderAdditionalEventHandler;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterRestartCallback;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterStateRunnable;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;

/**
 * @author jfischer
 */
public class PagesPresenter extends DynamicEntityPresenter implements Instantiable {

    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration refreshButtonHandlerRegistration;
    protected HandlerRegistration ruleSaveButtonHandlerRegistration;
    protected HandlerRegistration ruleRefreshButtonHandlerRegistration;
    protected HandlerRegistration extendedFetchDataHandlerRegistration;
    protected Record currentPageRecord;
    protected String currentPageId;
    protected Integer currentPagePos;
    protected EntitySearchDialog pageTemplateDialogView;
    protected PagesRuleBasedPresenterInitializer initializer;
    protected PagesPresenterExtractor extractor;
    protected AdditionalFilterEventManager additionalFilterEventManager = new AdditionalFilterEventManager();

    @Override
    protected void removeClicked() {
        Record selectedRecord = display.getListDisplay().getGrid().getSelectedRecord();
        final String primaryKey = display.getListDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
        final String id = selectedRecord.getAttribute(primaryKey);
        display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
            @Override
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                if (getDisplay().getListDisplay().getGrid().getResultSet() == null) {
                    getDisplay().getListDisplay().getGrid().setData(new Record[]{});
                }
                destroyTemplateForm();
                formPresenter.disable();
                display.getListDisplay().getRemoveButton().disable();
            }
        }, null);
    }

    @Override
    protected void addClicked() {
        initialValues.put("priority", 5);
        super.addClicked();
    }

    protected void destroyTemplateForm() {
        Canvas legacyForm = ((FormOnlyView) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay()).getMember("pageTemplateForm");
        if (legacyForm != null) {
            legacyForm.destroy();
        }
    }

    @Override
    protected void changeSelection(final Record selectedRecord) {
        additionalFilterEventManager.resetFilterState(new FilterStateRunnable() {
            @Override
            public void run(FilterRestartCallback cb) {
                extractor.getRemovedItemQualifiers().clear();
                extractor.resetButtonState();
                if (!selectedRecord.getAttributeAsBoolean("lockedFlag")) {
                    getDisplay().getListDisplay().getRemoveButton().enable();
                    getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().enable();
                    getDisplay().enableRules();
                    initializer.initSection(selectedRecord, false);
                } else {
                    getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
                    getDisplay().getListDisplay().getRemoveButton().disable();
                    getDisplay().disableRules();
                    initializer.initSection(selectedRecord, true);
                }

                currentPageRecord = selectedRecord;
                currentPageId = getPresenterSequenceSetupManager().getDataSource("pageDS").getPrimaryKeyValue(currentPageRecord);
                currentPagePos = getDisplay().getListDisplay().getGrid().getRecordIndex(currentPageRecord);
                loadTemplateForm(selectedRecord, cb);
            }
        });
    }

    protected void loadTemplateForm(final Record selectedRecord, final FilterRestartCallback cb) {
        //load the page template form
        BLCMain.NON_MODAL_PROGRESS.startProgress();

        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTemplateFormDS", new PageTemplateFormListDataSourceFactory(), null, new String[]{"constructForm", selectedRecord.getAttribute("pageTemplate")}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                destroyTemplateForm();
                final FormOnlyView formOnlyView = new FormOnlyView(dataSource, true, true, false);
                formOnlyView.getForm().addItemChangedHandler(new ItemChangedHandler() {
                    @Override
                    public void onItemChanged(ItemChangedEvent event) {
                        resetButtons();
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

                        if (cb != null) {
                            cb.processComplete();
                        }

                    }
                });
            }
        }));
    }

    protected void refresh() {
        getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().reset();
        FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay()).getMember("pageTemplateForm");
        if (legacyForm != null) {
            legacyForm.getForm().reset();
        }


        resetButtons();
    }

    @Override
    public void bind() {
        super.bind();

        getSaveButtonHandlerRegistration().removeHandler();
        formPresenter.getRefreshButtonHandlerRegistration().removeHandler();
        refreshButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getRefreshButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                //refresh();
                if (event.isLeftButtonDown()) {
                    //    extractor.getRemovedItemQualifiers().clear();
                    changeSelection(currentPageRecord);
                }
            }
        });
        ruleRefreshButtonHandlerRegistration = getDisplay().getRulesRefreshButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    //	refresh();
                    //  extractor.getRemovedItemQualifiers().clear();
                    changeSelection(currentPageRecord);
                }
            }
        });
        saveButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getSaveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    extractor.applyData(currentPageRecord);
                }
//                //save the regular entity form and the page template form
//                if (event.isLeftButtonDown()) { 
//                    DSRequest requestProperties = new DSRequest();
//                    try {
//             		       requestProperties.setAttribute("dirtyValues", getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getChangedValues());
//             		} catch (Exception e) {
//             				Logger.getLogger(this.getClass().toString()).log(Level.WARNING,"ignore, usually thown in gwt-run mode",e);
//             		}
//                    getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().saveData(new DSCallback() {
//                        @Override
//                        public void execute(DSResponse response, Object rawData, DSRequest request) {
//                            if (response.getStatus()!= RPCResponse.STATUS_FAILURE) {
//                                final String newId = response.getAttribute("newId");
//                                FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay()).getMember("pageTemplateForm");
//                                final DynamicForm form = legacyForm.getForm();
//                                for (FormItem formItem : form.getFields()) {
//
//                                	 if (formItem instanceof HTMLTextItem) { 
//                                		 form.setValue(formItem.getFieldName(), ((HTMLTextItem) formItem).getHTMLValue());
//                                	 }
//                                }
//                                PageTemplateFormListDataSource dataSource = (PageTemplateFormListDataSource) form.getDataSource();
//                                dataSource.setCustomCriteria(new String[]{"constructForm", newId});
//                                form.saveData(new DSCallback() {
//                                    @Override
//                                    public void execute(DSResponse response, Object rawData, DSRequest request) {
//                                        if (response.getStatus()!=RPCResponse.STATUS_FAILURE) {
//                                            getDisplay().getDynamicFormDisplay().getSaveButton().disable();
//                                            getDisplay().getDynamicFormDisplay().getRefreshButton().disable();
//                                            if (!currentPageId.equals(newId)) {
//                                                Record myRecord = getDisplay().getListDisplay().getGrid().getResultSet().find("id", currentPageId);
//                                                if (myRecord != null) {
//                                                    myRecord.setAttribute("id", newId);
//                                                    currentPageRecord = myRecord;
//                                                    currentPageId = newId;
//                                                }  else {
//                                                    String primaryKey = getDisplay().getListDisplay().getGrid().getDataSource().getPrimaryKeyFieldName();
//                                                    getDisplay().getListDisplay().getGrid().getDataSource().
//                                                        fetchData(new Criteria(primaryKey, newId), new DSCallback() {
//                                                            @Override
//                                                            public void execute(DSResponse response, Object rawData, DSRequest request) {
//                                                                getDisplay().getListDisplay().getGrid().clearCriteria();
//                                                                getDisplay().getListDisplay().getGrid().setData(response.getData());
//                                                                getDisplay().getListDisplay().getGrid().selectRecord(0);
//                                                            }
//                                                        });
//                                                    SC.say(BLCMain.getMessageManager().getString("criteriaDoesNotMatch"));
//                                                }
//                                            }
//
//
//                                            getDisplay().getListDisplay().getGrid().selectRecord(getDisplay().getListDisplay().getGrid().getRecordIndex(currentPageRecord));
//                                        }
//                                    }
//                                });
//							}
//                        }
//                    }, requestProperties);
//                }
            }
        });
        ruleRefreshButtonHandlerRegistration = getDisplay().getRulesRefreshButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    extractor.getRemovedItemQualifiers().clear();
                }
            }
        });
        ruleSaveButtonHandlerRegistration = getDisplay().getRulesSaveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    extractor.applyData(currentPageRecord);
                }
            }
        });

        extendedFetchDataHandlerRegistration = display.getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                destroyTemplateForm();
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().reset();
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
                getDisplay().disableRules();
                getDisplay().getRulesSaveButton().disable();
                getDisplay().getRulesRefreshButton().disable();
            }
        });

        getDisplay().getAddItemButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    final ItemBuilderDisplay display = getDisplay().addItemBuilder(getPresenterSequenceSetupManager().getDataSource("pageOrderItemDS"));
                    bindItemBuilderEvents(display);
                    display.setDirty(true);
                    resetButtons();
                }
            }
        });
        for (ItemBuilderDisplay itemBuilder : getDisplay().getItemBuilderViews()) {
            bindItemBuilderEvents(itemBuilder);
        }
        getDisplay().getCustomerFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                resetButtons();
            }
        });
        getDisplay().getProductFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                resetButtons();
            }
        });
        getDisplay().getRequestFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                resetButtons();
            }
        });
        getDisplay().getTimeFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                resetButtons();
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getCustomerFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                resetButtons();
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getProductFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                resetButtons();
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getRequestFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                resetButtons();
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getTimeFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                resetButtons();
            }
        });
    }


    @Override
    public void setup() {
//        super.setup();

        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageCustomerDS", new CustomerListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((DynamicEntityDataSource) result).permanentlyShowFields("id");
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageProductDS", new ProductListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTimeDTODS", new TimeDTOListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageRequestDTODS", new RequestDTOListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageOrderItemDS", new OrderItemListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((DynamicEntityDataSource) result).permanentlyShowFields("product.id", "category.id", "sku.id");

            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageDS", new PageDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top,
                        getPresenterSequenceSetupManager().getDataSource("pageCustomerDS"),
                        getPresenterSequenceSetupManager().getDataSource("pageTimeDTODS"),
                        getPresenterSequenceSetupManager().getDataSource("pageRequestDTODS"),
                        getPresenterSequenceSetupManager().getDataSource("pageOrderItemDS"),
                        getPresenterSequenceSetupManager().getDataSource("pageProductDS"));
                initializer = new PagesRuleBasedPresenterInitializer(PagesPresenter.this, getPresenterSequenceSetupManager().getDataSource("pageOrderItemDS"), getPresenterSequenceSetupManager().getDataSource("pageOrderItemDS"));
                extractor = new PagesPresenterExtractor(PagesPresenter.this);
                ((ListGridDataSource) top).setupGridFields(new String[]{"locked", "fullUrl", "description", "pageTemplate_Grid"});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTemplateSearchDS", new PageTemplateSearchListDataSourceFactory(), new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC), new Object[]{}, new AsyncCallbackAdapter() {
            @Override
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
                                    loadTemplateForm(currentPageRecord, null);
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

    protected void resetButtons() {
        getDisplay().getDynamicFormDisplay().getSaveButton().enable();
        getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
        getDisplay().getRulesSaveButton().enable();
        getDisplay().getRulesRefreshButton().enable();
    }

    public void bindItemBuilderEvents(final ItemBuilderDisplay display) {
        display.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                extractor.getRemovedItemQualifiers().add(display);
                additionalFilterEventManager.removeFilterBuilderAdditionalEventHandler(display.getItemFilterBuilder());
                resetButtons();
                display.setDirty(true);
            }
        });
        display.getRawItemForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                resetButtons();
                display.setDirty(true);
            }
        });
        display.getItemForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                resetButtons();
                display.setDirty(true);
            }
        });
        display.getItemFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                resetButtons();
                display.setDirty(true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(display.getItemFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                resetButtons();
                display.setDirty(true);
            }
        });
    }

}
