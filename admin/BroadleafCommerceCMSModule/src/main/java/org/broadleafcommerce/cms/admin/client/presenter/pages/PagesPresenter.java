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

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.cms.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsFolderTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsTileGridDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.LocaleListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateFormListDataSource;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateFormListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateSearchListDataSource;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PageTemplateSearchListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PagesTreeDataSource;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PagesTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.pages.PagesDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.event.TileGridItemSelectedEvent;
import org.broadleafcommerce.openadmin.client.event.TileGridItemSelectedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.AssetSearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextCanvasItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.RichTextHTMLPane;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCResponse;
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

/**
 * 
 * @author jfischer
 *
 */
public class PagesPresenter extends DynamicEntityPresenter implements Instantiable {

	protected String rootId = null;
	protected String rootName = "Root";
    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration refreshButtonHandlerRegistration;
    protected Record currentPageRecord;
	protected AssetSearchDialog assetSearchDialogView;
	protected EntitySearchDialog pageTemplateDialogView;

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
            getDisplay().getListDisplay().getRemoveButton().enable();
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().enable();
            currentPageRecord = selectedRecord;
            loadTemplateForm(selectedRecord);
        } else {
            getDisplay().getAddPageButton().enable();
            getDisplay().getAddPageFolderButton().enable();
            getDisplay().getListDisplay().getRemoveButton().disable();
            destroyTemplateForm();
            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().disable();
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
                                if (!getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(currentPageRecord).equals(newId)) {
                                    ((TreeGrid) display.getListDisplay().getGrid()).getTree().remove(((TreeGrid) display.getListDisplay().getGrid()).getTree().findById(newId));
                                    currentPageRecord.setAttribute(getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyFieldName(), newId);
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
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), getPresenterSequenceSetupManager().getDataSource("pageTreeDS"), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            TreeNode parentRecord = (TreeNode) display.getListDisplay().getGrid().getSelectedRecord();
		                    reloadAllChildRecordsForId(getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(parentRecord));
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
                    BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), getPresenterSequenceSetupManager().getDataSource("pageTreeDS"), initialValues, new NewItemCreatedEventHandler() {
                        public void onNewItemCreated(NewItemCreatedEvent event) {
                            TreeNode parentRecord = (TreeNode) display.getListDisplay().getGrid().getSelectedRecord();
		                    reloadAllChildRecordsForId(getPresenterSequenceSetupManager().getDataSource("pageTreeDS").getPrimaryKeyValue(parentRecord));
                        }
                    }, "90%", null, null);
                }
            }
        });
        getDisplay().getCurrentLocale().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String newLocaleName = (String) event.getValue();
                ((PagesTreeDataSource) getPresenterSequenceSetupManager().getDataSource("pageTreeDS")).setPermanentCriteria(new Criteria("pageTemplate.locale.localeName", newLocaleName));
                getDisplay().getListDisplay().getGrid().invalidateCache();
                ((PageTemplateSearchListDataSource) getPresenterSequenceSetupManager().getDataSource("pageTemplateSearchDS")).setPermanentCriteria(new Criteria("locale.localeName", newLocaleName));
                ((PageTemplateSearchListDataSource) getPresenterSequenceSetupManager().getDataSource("pageTemplateSearchDS")).getAssociatedGrid().invalidateCache();
            }
        });
        
        exposeNativeGetTemplatePath();
        exposeNativeDisplayAssetSearchDialog();
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
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetFolderTreeDS", new StaticAssetsFolderTreeDataSourceFactory(), null, new Object[]{}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTileGridDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
            	TileGridDataSource staticAssetTreeDS = (TileGridDataSource) dataSource;
            	PresentationLayerAssociatedDataSource staticAssetFolderTreeDS = (PresentationLayerAssociatedDataSource) getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS");
             	assetSearchDialogView = new AssetSearchDialog(staticAssetTreeDS, staticAssetFolderTreeDS);
            }
        }));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pageTreeDS", new PagesTreeDataSourceFactory(), null, new Object[]{rootId, rootName}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("localeDS", new LocaleListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
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
				EntitySearchDialog pageTemplateSearchView = new EntitySearchDialog(pageTemplateDataSource);
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
	public PagesDisplay getDisplay() {
		return (PagesDisplay) display;
	}
	
	public String getTemplatePath() {
		return (String) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getValue("pageTemplate.templatePath");
	}
	
	public void displayAssetSearchDialog(final JavaScriptObject editor) {
		assetSearchDialogView.search("Asset Search", new TileGridItemSelectedEventHandler() {
			@Override
			public void onSearchItemSelected(TileGridItemSelectedEvent event) {
				String staticAssetFullUrl = "/broadleafdemo/cms/staticasset" + event.getRecord().getAttribute("fullUrl");
				String title = event.getRecord().getAttribute("name");
				String alt = event.getRecord().getAttribute("name");
				String imgTag = "<img title='" + title + "' src='" + staticAssetFullUrl + "' alt='" + alt + "'/>";
				insertRichTextContent(editor, imgTag);
			}
		});
	}
	
	private native void exposeNativeGetTemplatePath() /*-{
		var currentPagesPresenter = this;
		$wnd.getTemplatePath = function() {
			return currentPagesPresenter.@org.broadleafcommerce.cms.admin.client.presenter.pages.PagesPresenter::getTemplatePath()();
		}
	}-*/;
	
	private native void exposeNativeDisplayAssetSearchDialog() /*-{
		var currentPagesPresenter = this;
		$wnd.displayAssetSearchDialog = function(editor) {
			return currentPagesPresenter.@org.broadleafcommerce.cms.admin.client.presenter.pages.PagesPresenter::displayAssetSearchDialog(Lcom/google/gwt/core/client/JavaScriptObject;)(editor);
		}
	}-*/;
	
	private native void insertRichTextContent(JavaScriptObject tinyMCEEditor, String content) /*-{
		tinyMCEEditor.selection.setContent(content); 
	}-*/;
}
