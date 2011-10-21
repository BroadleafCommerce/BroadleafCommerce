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
import com.smartgwt.client.data.*;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.cms.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.cms.admin.client.datasource.pages.*;
import org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter;
import org.broadleafcommerce.cms.admin.client.view.pages.PagesDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
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
public class PagesPresenter extends HtmlEditingPresenter implements Instantiable {

	protected String rootId = null;
	protected String rootName = "Root";
    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration refreshButtonHandlerRegistration;
    protected Record currentPageRecord;
    protected TreeNode currentFolderRecord;
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
        Canvas legacyForm = ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).getMember("pageTemplateForm");
        if (legacyForm != null) {
            legacyForm.destroy();
        }
    }

    @Override
	protected void changeSelection(final Record selectedRecord) {
        String pageType = selectedRecord.getAttributeAsStringArray("_type")==null?null:selectedRecord.getAttributeAsStringArray("_type")[0];
        if (pageType!=null && pageType.equals("org.broadleafcommerce.cms.page.domain.PageImpl")) {
            getDisplay().getAddPageButton().disable();
            getDisplay().getAddPageFolderButton().disable();
            if (!selectedRecord.getAttributeAsBoolean("lockedFlag")) {
                getDisplay().getListDisplay().getRemoveButton().enable();
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().enable();
            } else {
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
                getDisplay().getListDisplay().getRemoveButton().disable();
            }
            currentPageRecord = selectedRecord;
            currentPageId = getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(currentPageRecord);
            loadTemplateForm(selectedRecord);
        } else {
            getDisplay().getAddPageButton().enable();
            getDisplay().getAddPageFolderButton().enable();
            getDisplay().getListDisplay().getRemoveButton().enable();
            destroyTemplateForm();
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
            currentPageRecord = null;
            currentPageId = null;
        }
        currentFolderRecord = (TreeNode) selectedRecord;
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
                    }
                });
                formOnlyView.setID("pageTemplateForm");
                formOnlyView.setOverflow(Overflow.VISIBLE);
                ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).addMember(formOnlyView);
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
        formPresenter.getSaveButtonHandlerRegistration().removeHandler();
        formPresenter.getRefreshButtonHandlerRegistration().removeHandler();
        refreshButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getRefreshButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().reset();
                    FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).getMember("pageTemplateForm");
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
                            if (response.getStatus()!= RPCResponse.STATUS_FAILURE) {
                                final Record newRecord = response.getData()[0];
                                final String newId = getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(newRecord);
                                FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).getMember("pageTemplateForm");
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
                                        }
                                    }
                                });
                                if (!currentPageId.equals(newId)) {
                                    ((TreeGrid) display.getListDisplay().getGrid()).getTree().remove(((TreeGrid) display.getListDisplay().getGrid()).getTree().findById(currentPageId));
                                    currentPageRecord = newRecord;
                                    currentPageId = newId;
                                    currentFolderRecord = (TreeNode) newRecord;
                                }
							}
                        }
                    }, requestProperties);
                }
            }
        });
        getDisplay().getAddPageButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    getPresenterSequenceSetupManager().getDataSource("pageTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.PAGEIMPL);
                    Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
                    initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
                    initialValues.put("_type", new String[]{EntityImplementations.PAGEIMPL});
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), getPresenterSequenceSetupManager().getDataSource("pageTreeDS"), initialValues, new ItemEditedHandler() {
                        public void onItemEdited(ItemEdited event) {
                            if (!((TreeGrid) getDisplay().getListDisplay().getGrid()).getTree().isOpen(currentFolderRecord)) {
                               ((TreeGrid) getDisplay().getListDisplay().getGrid()).getTree().openFolder(currentFolderRecord);
                            }
		                    reloadAllChildRecordsForId(getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(currentFolderRecord));
                        }
                    }, "90%", null, null);
                }
            }
        });
        getDisplay().getAddPageFolderButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    getPresenterSequenceSetupManager().getDataSource("pageTreeDS").setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.PAGEFOLDERIMPL);
                    Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("parentFolder", getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
                    initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
                    initialValues.put("_type", new String[]{EntityImplementations.PAGEFOLDERIMPL});
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), getPresenterSequenceSetupManager().getDataSource("pageTreeDS"), initialValues, new ItemEditedHandler() {
                        public void onItemEdited(ItemEdited event) {
                            if (!((TreeGrid) getDisplay().getListDisplay().getGrid()).getTree().isOpen(currentFolderRecord)) {
                               ((TreeGrid) getDisplay().getListDisplay().getGrid()).getTree().openFolder(currentFolderRecord);
                            }
		                    reloadAllChildRecordsForId(getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(currentFolderRecord));
                        }
                    }, "90%", null, null);
                }
            }
        });
        getDisplay().getCurrentLocale().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String newLocaleCode = (String) event.getValue();
                ((PagesTreeDataSource) getPresenterSequenceSetupManager().getDataSource("pageTreeDS")).setPermanentCriteria(new Criteria("pageTemplate.locale.localeCode", newLocaleCode));
                getDisplay().getListDisplay().getGrid().invalidateCache();
                ((PageTemplateSearchListDataSource) getPresenterSequenceSetupManager().getDataSource("pageTemplateSearchDS")).setPermanentCriteria(new Criteria("locale.localeCode", newLocaleCode));
                ((PageTemplateSearchListDataSource) getPresenterSequenceSetupManager().getDataSource("pageTemplateSearchDS")).getAssociatedGrid().invalidateCache();
            }
        });
	}

    public void reloadAllChildRecordsForId(String id) {
        if (id == null) {
            getDisplay().getListDisplay().getGrid().invalidateCache();
        } else {
            RecordList resultSet = display.getListDisplay().getGrid().getRecordList();
            if (resultSet != null) {
                Record[] myRecords = resultSet.toArray();
                for (Record myRecord : myRecords) {
                    String myId = getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(myRecord);
                    if (id.equals(myId)) {
                        ((TreeGrid) display.getListDisplay().getGrid()).getTree().reloadChildren((TreeNode) myRecord);
                    }
                }
            }
        }
	}

	public void setup() {
        super.setup();
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTreeDS", new PagesTreeDataSourceFactory(), null, new Object[]{rootId, rootName}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("localeDS", new LocaleListDataSourceFactory(), new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems(getPresenterSequenceSetupManager().getDataSource("pageTreeDS"), top);
				((TreeGridDataSource) getPresenterSequenceSetupManager().getDataSource("pageTreeDS")).setupGridFields(new String[]{}, new Boolean[]{}, "250", "100");
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
				getPresenterSequenceSetupManager().getDataSource("pageTreeDS").
				getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                        "pageTemplate",
                        pageTemplateSearchView,
                        "Page Template Search",
                        getDisplay().getDynamicFormDisplay(),
                        new FormItemCallback() {
                            @Override
                            public void execute(FormItem formItem) {
                                if (currentPageRecord != null) {
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
    public String getTemplatePath() {
		return (String) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getValue("pageTemplate.templatePath");
	}

	@Override
	public PagesDisplay getDisplay() {
		return (PagesDisplay) display;
	}

}
