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

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PagesTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.pages.PagesDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class PagesPresenter extends DynamicEntityPresenter implements Instantiable {

	protected String rootId = "1";
	protected String rootName = "Root";

	protected HashMap<String, Object> library = new HashMap<String, Object>();

	@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("parentFolder", ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
		initialValues.put("name", BLCMain.getMessageManager().getString("defaultPageName"));
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newItemTitle"), (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				//do nothing
			}
		}, "90%", null, null);
	}

	@Override
	protected void removeClicked() {
		/*display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				getDisplay().getOrphanedCategoryGrid().invalidateCache();
				getDisplay().getRemoveOrphanedButton().disable();
				getDisplay().getInsertOrphanButton().disable();
			}
		}, null);
		formPresenter.disable();
		display.getListDisplay().getRemoveButton().disable();
		allChildCategoriesPresenter.disable();*/
	}

	@Override
	protected void changeSelection(final Record selectedRecord) {
		/*final AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();
		if (categorySearchDataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(selectedRecord)).equals(rootId)){
			formPresenter.disable();
			display.getListDisplay().getRemoveButton().disable();
		}
		allChildCategoriesPresenter.load(selectedRecord, dataSource, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				try {
					if (response.getErrors().size() > 0) {
						getDisplay().getInsertOrphanButton().disable();
					}
				} catch (Exception e) {
					if (getDisplay().getOrphanedCategoryGrid().getSelectedRecord() != null) {
						getDisplay().getInsertOrphanButton().enable();
					}
					allChildCategoriesPresenter.enable();
					allChildCategoriesPresenter.setStartState();
					mediaPresenter.load(selectedRecord, dataSource, null);
				}
			}
		});
		display.getListDisplay().getAddButton().disable();
		featuredPresenter.load(selectedRecord, dataSource, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				display.getListDisplay().getAddButton().enable();
			}
		});
		childProductsPresenter.load(selectedRecord, dataSource, null);
		getDisplay().getAllCategoriesDisplay().getRemoveButton().disable();*/
	}

	@Override
	public void bind() {
		super.bind();
		/*featuredPresenter.bind();
		mediaPresenter.bind();
		allChildCategoriesPresenter.bind();
		childProductsPresenter.bind();
		((TreeGrid) display.getListDisplay().getGrid()).addDataArrivedHandler(new com.smartgwt.client.widgets.tree.events.DataArrivedHandler() {
			public void onDataArrived(com.smartgwt.client.widgets.tree.events.DataArrivedEvent event) {
				Record[] records = event.getParentNode().getAttributeAsRecordArray("children");
				for (Record record : records) {
					String hasChildren = ((TreeNode) record).getAttribute(PagesTreeDataSourceFactory.hasChildrenProperty);
					if (hasChildren != null && !Boolean.parseBoolean(hasChildren)) {
						((TreeGrid) display.getListDisplay().getGrid()).getTree().loadChildren((TreeNode) record);
					}
				}
			}
        });
		getDisplay().getRemoveOrphanedButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					SC.confirm(BLCMain.getMessageManager().getString("confirmDelete"), new BooleanCallback() {
						public void execute(Boolean value) {
							if (value) {
								getDisplay().getOrphanedCategoryGrid().removeSelectedData();
								getDisplay().getRemoveOrphanedButton().disable();
								getDisplay().getInsertOrphanButton().disable();
							}
						}
					});
				}
			}
		});
		getDisplay().getInsertOrphanButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					getDisplay().getAllCategoriesDisplay().getGrid().addData(getDisplay().getOrphanedCategoryGrid().getSelectedRecord(), new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							reloadParentTreeNodeRecords(true);
							getDisplay().getOrphanedCategoryGrid().invalidateCache();
							getDisplay().getRemoveOrphanedButton().disable();
							getDisplay().getInsertOrphanButton().disable();
						}
					});
				}
			}
		});
		getDisplay().getOrphanedCategoryGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					getDisplay().getRemoveOrphanedButton().enable();
					if (display.getListDisplay().getGrid().getSelectedRecord() != null && !getDisplay().getAllCategoriesDisplay().getGrid().isDisabled()) {
						getDisplay().getInsertOrphanButton().enable();
					}
				} else {
					getDisplay().getRemoveOrphanedButton().disable();
					getDisplay().getInsertOrphanButton().disable();
				}
			}
		});*/
	}

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pagesTreeDS", new PagesTreeDataSourceFactory(), null, new Object[]{rootId, rootName}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems(top);
				((TreeGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{}, "250", "100");
			}
		}));
		/*getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("categorySearch", new CategorySearchDataSourceFactory(), new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.JOINSTRUCTURE, OperationType.ENTITY, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				categorySearchDataSource = (ListGridDataSource) result;
				categorySearchDataSource.resetProminenceOnly(
					"name",
					"urlKey",
					"activeStartDate",
					"activeEndDate"
				);
				EntitySearchDialog categorySearchView = new EntitySearchDialog(categorySearchDataSource);
				library.put("categorySearchView", categorySearchView);
				((DynamicEntityDataSource) ((CategoryDisplay) getDisplay()).getListDisplay().getGrid().getDataSource()).
				getFormItemCallbackHandlerManager().addSearchFormItemCallback(
					"defaultParentCategory", 
					categorySearchView, 
					"Category Search", 
					((CategoryDisplay) getDisplay()).getDynamicFormDisplay()
				);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("allChildCategoriesDS", new CategoryListDataSourceFactory(), new OperationTypes(OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				allChildCategoriesPresenter = new AllChildCategoriesPresenter(PagesPresenter.this, ((CategoryDisplay) getDisplay()).getAllCategoriesDisplay(), (EntitySearchDialog) library.get("categorySearchView"), BLCMain.getMessageManager().getString("categorySearchTitle"));
				allChildCategoriesPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "urlKey"}, new Boolean[]{false, false});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("orphanedCategoriesDS", new OrphanedCategoryListDataSourceFactory(), null, new Object[]{rootId}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid().setDataSource(result);
				((ListGridDataSource) result).setAssociatedGrid(((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid());
				((ListGridDataSource) result).setupGridFields(new String[]{"name", "urlKey"}, new Boolean[]{false, false});
				
				Criteria myCriteria = new Criteria();
				myCriteria.addCriteria(OrphanedCategoryListDataSourceFactory.foreignKeyName, "0");
				
				((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid().fetchData(myCriteria);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productSearchDS", new ProductListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				ListGridDataSource productSearchDataSource = (ListGridDataSource) result;
				productSearchDataSource.resetPermanentFieldVisibility(
					"name",
					"description",
					"model",
					"manufacturer",
					"activeStartDate",
					"activeEndDate"
				);
				EntitySearchDialog productSearchView = new EntitySearchDialog(productSearchDataSource);
				library.put("productSearchView", productSearchView);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("featuredProductsDS", new FeaturedProductListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				featuredPresenter = new EditableJoinStructurePresenter(((CategoryDisplay) getDisplay()).getFeaturedDisplay(), (EntitySearchDialog) library.get("productSearchView"), BLCMain.getMessageManager().getString("productSearchTitle"), BLCMain.getMessageManager().getString("setPromotionMessageTitle"), "promotionMessage");
				featuredPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("allChildProductsDS", new AllProductsDataSourceFactory(), new OperationTypes(OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				childProductsPresenter = new SimpleSearchJoinStructurePresenter(((CategoryDisplay) getDisplay()).getAllProductsDisplay(), (EntitySearchDialog) library.get("productSearchView"), BLCMain.getMessageManager().getString("productSearchPrompt"));
				childProductsPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "model", "manufacturer"}, new Boolean[]{false, false, false});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("mediaMapDS", new MediaMapDataSourceFactory(this), null, new Object[]{getMediaMapKeys()}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				Map<String, Object> initialValues = new HashMap<String, Object>();
				initialValues.put("name", BLCMain.getMessageManager().getString("mediaNameDefault"));
				initialValues.put("label", BLCMain.getMessageManager().getString("mediaLabelDefault"));
				mediaPresenter = new MapStructurePresenter(((CategoryDisplay) getDisplay()).getMediaDisplay(), getMediaEntityView(), BLCMain.getMessageManager().getString("newMediaTitle"), initialValues);
				mediaPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "name", "url", "label"}, new Boolean[]{true, true, true, true});
			}
		}));*/
	}

	@Override
	public PagesDisplay getDisplay() {
		return (PagesDisplay) display;
	}
	
}
