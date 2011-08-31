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
import com.smartgwt.client.types.Overflow;
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
import org.broadleafcommerce.cms.admin.client.view.pages.PagesDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class PagesPresenter extends DynamicEntityPresenter implements Instantiable {

	protected String rootId = null;
	protected String rootName = "Root";
	protected HashMap<String, Object> library = new HashMap<String, Object>();
    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration itemChangedHandlerRegistration;
    protected Record currentPageRecord;

	@Override
	protected void removeClicked() {
		display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
            @Override
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                destroyTemplateForm();
                formPresenter.disable();
		        display.getListDisplay().getRemoveButton().disable();
            }
        }, null);
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
            currentPageRecord = selectedRecord;
            loadTemplateForm(selectedRecord);
        } else {
            getDisplay().getAddPageButton().enable();
            getDisplay().getAddPageFolderButton().enable();
            destroyTemplateForm();
            currentPageRecord = null;
        }
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
                        formOnlyView.getForm().enable();
                    }
                });
            }
        });
    }

    @Override
	public void bind() {
		super.bind();
        formPresenter.getSaveButtonHandlerRegistration().removeHandler();
        saveButtonHandlerRegistration = getDisplay().getDynamicFormDisplay().getSaveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //save the regular entity form and the page template form
                if (event.isLeftButtonDown()) {
                    DSRequest requestProperties = new DSRequest();
                    requestProperties.setAttribute("dirtyValues", getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getChangedValues());
                    getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().saveData(new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            try {
								if (!response.getErrors().isEmpty()) {
									//do nothing
								}
							} catch (Exception e) {
                                FormOnlyView legacyForm = (FormOnlyView) ((FormOnlyView) ((DynamicFormView) getDisplay().getDynamicFormDisplay()).getFormOnlyDisplay()).getMember("pageTemplateForm");
                                DynamicForm form = legacyForm.getForm();
                                PageTemplateFormListDataSource dataSource = (PageTemplateFormListDataSource) form.getDataSource();
                                Record selectedRecord = form.getValuesAsRecord();
                                dataSource.setCustomCriteria(new String[]{"constructForm", selectedRecord.getAttribute("id")});
                                form.saveData(new DSCallback() {
                                    @Override
                                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                                        try {
                                            if (!response.getErrors().isEmpty()) {
                                                //do nothing
                                            }
                                        } catch (Exception e) {
                                            getDisplay().getDynamicFormDisplay().getSaveButton().disable();
                                        }
                                    }
                                });
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
                    ((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.PAGEIMPL);
                    Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("parentFolder", ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
                    initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
                    initialValues.put("_type", new String[]{EntityImplementations.PAGEIMPL});
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            TreeNode parentRecord = (TreeNode) display.getListDisplay().getGrid().getSelectedRecord();
		                    reloadAllChildRecordsForId(((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(parentRecord));
                        }
                    }, "90%", null, null);
                }
            }
        });
        getDisplay().getAddPageFolderButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).setDefaultNewEntityFullyQualifiedClassname(EntityImplementations.PAGES);
                    Map<String, Object> initialValues = new HashMap<String, Object>();
                    initialValues.put("parentFolder", ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
                    initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
                    initialValues.put("_type", new String[]{EntityImplementations.PAGES});
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            TreeNode parentRecord = (TreeNode) display.getListDisplay().getGrid().getSelectedRecord();
		                    reloadAllChildRecordsForId(((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(parentRecord));
                        }
                    }, "90%", null, null);
                }
            }
        });
        getDisplay().getCurrentLocale().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String newLocaleName = (String) event.getValue();
                ((PagesTreeDataSource) library.get("pageTreeDS")).setPermanentCriteria(new Criteria("pageTemplate.locale.localeName", newLocaleName));
                getDisplay().getListDisplay().getGrid().invalidateCache();
                ((PageTemplateSearchListDataSource) library.get("pageTemplateSearchDS")).setPermanentCriteria(new Criteria("locale.localeName", newLocaleName));
                ((PageTemplateSearchListDataSource) library.get("pageTemplateSearchDS")).getAssociatedGrid().invalidateCache();
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
                    String myId = ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(myRecord);
                    if (id.equals(myId)) {
                        ((TreeGrid) display.getListDisplay().getGrid()).getTree().reloadChildren((TreeNode) myRecord);
                    }
                }
            }
        }
	}

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pagesTreeDS", new PagesTreeDataSourceFactory(), null, new Object[]{rootId, rootName}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
                library.put("pageTreeDS", top);
			}
		}));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("localeDS", new LocaleListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems((DataSource) library.get("pageTreeDS"), top);
				((TreeGridDataSource) library.get("pageTreeDS")).setupGridFields(new String[]{}, new Boolean[]{}, "250", "100");
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTemplateSearch", new PageTemplateSearchListDataSourceFactory(), new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				ListGridDataSource pageTemplateDataSource = (ListGridDataSource) result;
				pageTemplateDataSource.resetPermanentFieldVisibility(
					"templateName",
					"templatePath"
				);
				EntitySearchDialog pageTemplateSearchView = new EntitySearchDialog(pageTemplateDataSource);
				((DynamicEntityDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).
				getFormItemCallbackHandlerManager().addSearchFormItemCallback(
					"pageTemplate",
					pageTemplateSearchView,
					"Page Template Search",
					getDisplay().getDynamicFormDisplay(),
                    new FormItemCallback() {
                        @Override
                        public void execute(FormItem formItem) {
                            if (currentPageRecord != null) {
                                loadTemplateForm(currentPageRecord);
                            }
                        }
                    }
				);
                library.put("pageTemplateSearchDS", result);
			}
		}));
	}

	@Override
	public PagesDisplay getDisplay() {
		return (PagesDisplay) display;
	}
	
}
