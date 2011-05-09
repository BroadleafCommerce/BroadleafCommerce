package org.broadleafcommerce.gwt.client.presenter.catalog.category;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.AllProductsDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.CategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.CategorySearchDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.CategoryTreeDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.FeaturedProductListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.MediaMapDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.OrphanedCategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.ProductListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.gwt.client.presenter.structure.EditableJoinStructurePresenter;
import org.broadleafcommerce.gwt.client.presenter.structure.MapStructurePresenter;
import org.broadleafcommerce.gwt.client.presenter.structure.SimpleSearchJoinStructurePresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.catalog.category.CategoryDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.MapStructureEntityEditDialog;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class CategoryPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected MapStructureEntityEditDialog mapEntityAdd = null;
	protected ListGridDataSource categorySearchDataSource = null;
	protected String rootId = "1";
	protected String rootName = "Store";
	
	protected SubPresentable featuredPresenter;
	protected SubPresentable mediaPresenter;
	protected AllChildCategoriesPresenter allChildCategoriesPresenter;
	protected SubPresentable childProductsPresenter;

	@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("defaultParentCategory", ((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(display.getListDisplay().getGrid().getSelectedRecord()));
		initialValues.put("name", "Untitled");
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord("Create New Category", (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				reloadParentTreeNodeRecords(false);
				getDisplay().getAllCategoriesDisplay().getGrid().clearCriteria();
				allChildCategoriesPresenter.load(display.getListDisplay().getGrid().getSelectedRecord(), (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource(), null);
			}
		}, "90%", null, null);
	}

	@Override
	protected void removeClicked() {
		display.getListDisplay().getGrid().removeSelectedData(new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					getDisplay().getOrphanedCategoryGrid().invalidateCache();
					getDisplay().getRemoveOrphanedButton().disable();
					getDisplay().getInsertOrphanButton().disable();
				}
			}
		}, null);
		formPresenter.disable();
		display.getListDisplay().getRemoveButton().disable();
		allChildCategoriesPresenter.disable();
	}

	@Override
	protected void changeSelection(final Record selectedRecord) {
		final AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();
		if (categorySearchDataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(selectedRecord)).equals(rootId)){
			formPresenter.disable();
			display.getListDisplay().getRemoveButton().disable();
		}
		allChildCategoriesPresenter.load(selectedRecord, dataSource, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					if (getDisplay().getOrphanedCategoryGrid().getSelectedRecord() != null) {
						getDisplay().getInsertOrphanButton().enable();
					}
					allChildCategoriesPresenter.setStartState();
					mediaPresenter.load(selectedRecord, dataSource, null);
				} else {
					getDisplay().getInsertOrphanButton().disable();
				}
			}
		});
		display.getListDisplay().getAddButton().disable();
		featuredPresenter.load(selectedRecord, dataSource, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					display.getListDisplay().getAddButton().enable();
				}
			}
		});
		childProductsPresenter.load(selectedRecord, dataSource, null);
		getDisplay().getAllCategoriesDisplay().getRemoveButton().disable();
	}

	@Override
	public void bind() {
		super.bind();
		featuredPresenter.bind();
		mediaPresenter.bind();
		allChildCategoriesPresenter.bind();
		childProductsPresenter.bind();
		((TreeGrid) display.getListDisplay().getGrid()).addDataArrivedHandler(new com.smartgwt.client.widgets.tree.events.DataArrivedHandler() {
			public void onDataArrived(com.smartgwt.client.widgets.tree.events.DataArrivedEvent event) {
				Record[] records = event.getParentNode().getAttributeAsRecordArray("children");
				for (Record record : records) {
					String hasChildren = ((TreeNode) record).getAttribute(CategoryTreeDataSourceFactory.hasChildrenProperty);
					if (hasChildren != null && !Boolean.parseBoolean(hasChildren)) {
						((TreeGrid) display.getListDisplay().getGrid()).getTree().loadChildren((TreeNode) record);
					}
				}
			}
        });
		getDisplay().getRemoveOrphanedButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					SC.confirm("Are your sure you want to delete this entity ("+getDisplay().getOrphanedCategoryGrid().getSelectedRecord().getAttribute("name")+")?", new BooleanCallback() {
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
							if (response.getErrors().isEmpty()) {
								reloadParentTreeNodeRecords(true);
								getDisplay().getOrphanedCategoryGrid().invalidateCache();
								getDisplay().getRemoveOrphanedButton().disable();
								getDisplay().getInsertOrphanButton().disable();
							}
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
		});
	}

	@Override
	public void go(final Canvas container) {
		BLCMain.MODAL_PROGRESS.startProgress(new Timer() {
			public void run() {
				if (loaded) {
					CategoryPresenter.super.go(container);
					return;
				}
				CategoryTreeDataSourceFactory.createDataSource("categoryTreeDS", rootId, rootName, new AsyncCallbackAdapter() {
					public void onSuccess(DataSource top) {
						setupDisplayItems(top);
						((TreeGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{}, "250", "100");
						
						CategorySearchDataSourceFactory.createDataSource("categorySearch", new AsyncCallbackAdapter() {
							public void onSuccess(DataSource result) {
								categorySearchDataSource = (ListGridDataSource) result;
								categorySearchDataSource.resetProminenceOnly(
									"name",
									"urlKey",
									"activeStartDate",
									"activeEndDate"
								);
								final EntitySearchDialog categorySearchView = new EntitySearchDialog(categorySearchDataSource);
								((DynamicEntityDataSource) ((CategoryDisplay) getDisplay()).getListDisplay().getGrid().getDataSource()).
								getFormItemCallbackHandlerManager().addSearchFormItemCallback(
									"defaultParentCategory", 
									categorySearchView, 
									"Category Search", 
									((CategoryDisplay) getDisplay()).getDynamicFormDisplay()
								);
								/*((DynamicEntityDataSource) ((CategoryDisplay) getDisplay()).getListDisplay().getGrid().getDataSource()).
								getFormItemCallbackHandlerManager().addSearchFormItemCallback(
									"defaultParentCategory", 
									new FormItemCallback() {
										public void execute(final FormItem formItem) {
											//search for a parent category
											categorySearchView.search("Category Search", new SearchItemSelectedEventHandler() {
												public void onSearchItemSelected(final SearchItemSelectedEvent event) {
													final String myId = event.getRecord().getAttribute("id");
													final String myName = event.getRecord().getAttribute("name");
													final Record associatedRecord = display.getListDisplay().getGrid().getSelectedRecord();
													final String associatedId = associatedRecord.getAttribute("id");
													//retrieve and instance of the currently selected category from the tree grid
													Criteria myCriteria = new Criteria();
													myCriteria.addCriteria("id", categorySearchDataSource.stripDuplicateAllowSpecialCharacters(associatedId));
													categorySearchDataSource.fetchData(myCriteria, new DSCallback(){
														public void execute(DSResponse response, Object rawData, DSRequest request) {
															final Record myRecord = response.getData()[0];
															myRecord.setAttribute(CategoryTreeDataSourceFactory.defaultParentCategoryForeignKey, myId);
															//update the currently selected category with the parent category
															categorySearchDataSource.updateData(myRecord, new DSCallback() {
																public void execute(DSResponse response, Object rawData, DSRequest request) {
																	String parentRecordName = ((TreeGrid) ((DynamicEditDisplay) getDisplay()).getListDisplay().getGrid()).getTree().getParent((TreeNode) associatedRecord).getAttribute("name");
																	if (!parentRecordName.equals(myName)) {
																		categorySearchDataSource.setLinkedValue(myId);
																		//add the currently selected category as a child of the parent category
																		categorySearchDataSource.addData(myRecord, new DSCallback() {
																			public void execute(DSResponse response, Object rawData, DSRequest request) {
																				//update the display information
																				formItem.getForm().getField("__display_"+formItem.getName()).setValue(myName);
																				Timer timer = new Timer() {  
																		            public void run() {  
																		            	formItem.setValue(myId);
																		            }  
																		        };
																		        timer.schedule(100);
																				reloadAllChildRecordsForId(myId);
																			}
																		});
																	} else {
																		//update the display information
																		formItem.getForm().getField("__display_"+formItem.getName()).setValue(myName);
																		Timer timer = new Timer() {  
																            public void run() {  
																            	formItem.setValue(myId);
																            }  
																        };
																        timer.schedule(100);
																	}
																}
															});
														}
													});
												}
											});
										}
									}
								);*/
								
								CategoryListDataSourceFactory.createDataSource("allChildCategoriesDS", new AsyncCallbackAdapter() {
									public void onSuccess(DataSource result) {
										allChildCategoriesPresenter = new AllChildCategoriesPresenter(CategoryPresenter.this, ((CategoryDisplay) getDisplay()).getAllCategoriesDisplay(), categorySearchView, "Category Search");
										allChildCategoriesPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "urlKey"}, new Boolean[]{false, false});
										
										OrphanedCategoryListDataSourceFactory.createDataSource("orphanedCategoriesDS", rootId, new AsyncCallbackAdapter() {
											public void onSuccess(DataSource result) {
												((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid().setDataSource(result);
												((ListGridDataSource) result).setAssociatedGrid(((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid());
												((ListGridDataSource) result).setupGridFields(new String[]{"name", "urlKey"}, new Boolean[]{false, false});
												
												Criteria myCriteria = new Criteria();
												myCriteria.addCriteria(OrphanedCategoryListDataSourceFactory.foreignKeyName, "0");
												
												((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid().fetchData(myCriteria);
												
												ProductListDataSourceFactory.createDataSource("productSearchDS", new AsyncCallbackAdapter() {
													public void onSuccess(DataSource result) {
														ListGridDataSource productSearchDataSource = (ListGridDataSource) result;
														productSearchDataSource.resetPermanentFieldVisibility(
															"name",
															"description",
															"model",
															"manufacturer",
															"activeStartDate",
															"activeEndDate"
														);
														final EntitySearchDialog productSearchView = new EntitySearchDialog(productSearchDataSource);
														
														FeaturedProductListDataSourceFactory.createDataSource("featuredProductsDS", new AsyncCallbackAdapter() {
															public void onSuccess(DataSource result) {
																featuredPresenter = new EditableJoinStructurePresenter(((CategoryDisplay) getDisplay()).getFeaturedDisplay(), productSearchView, "Product Search", "Set Promotion Message", "promotionMessage");
																featuredPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
																
																AllProductsDataSourceFactory.createDataSource("allChildProductsDS", new AsyncCallbackAdapter() {
																	public void onSuccess(DataSource result) {
																		childProductsPresenter = new SimpleSearchJoinStructurePresenter(((CategoryDisplay) getDisplay()).getAllProductsDisplay(), productSearchView, "Search For a Product");
																		childProductsPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "model", "manufacturer"}, new Boolean[]{false, false, false});
																
																		MediaMapDataSourceFactory.createDataSource("mediaMapDS", getMediaMapKeys(), ((CategoryDisplay) getDisplay()).getMediaDisplay().getGrid(), new AsyncCallbackAdapter() {
																			public void onSuccess(DataSource result) {
																				Map<String, Object> initialValues = new HashMap<String, Object>();
																				initialValues.put("name", "Untitled");
																				initialValues.put("label", "untitled");
																				mediaPresenter = new MapStructurePresenter(((CategoryDisplay) getDisplay()).getMediaDisplay(), getMediaEntityView(), "Add New Media", initialValues);
																				mediaPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "name", "url", "label"}, new Boolean[]{true, true, true, true});
																				
																				CategoryPresenter.super.go(container);
																			}
																		});
																	}
																});
															}
														});
													}
												});
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
	}
	
	public void reloadAllChildRecordsForId(String id) {
		String startingId = categorySearchDataSource.stripDuplicateAllowSpecialCharacters(id);
		RecordList resultSet = display.getListDisplay().getGrid().getRecordList();
		if (resultSet != null) {
			Record[] myRecords = resultSet.toArray();
			for (Record myRecord : myRecords) {
				String myId = categorySearchDataSource.stripDuplicateAllowSpecialCharacters(((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(myRecord));
				if (startingId.equals(myId)) {
					((TreeGrid) display.getListDisplay().getGrid()).getTree().reloadChildren((TreeNode) myRecord);
				}
			}
		}
	}

	public void reloadParentTreeNodeRecords(boolean disableCategoryButton) {
		TreeNode parentRecord = (TreeNode) display.getListDisplay().getGrid().getSelectedRecord();
		reloadAllChildRecordsForId(((AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource()).getPrimaryKeyValue(parentRecord));
		if (disableCategoryButton) {
			getDisplay().getAllCategoriesDisplay().getRemoveButton().disable();
		}
	}
	
	protected LinkedHashMap<String, String> getMediaMapKeys() {
		LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>();
		keys.put("small", "Small");
		keys.put("medium", "Medium");
		keys.put("large", "Large");
		
		return keys;
	}
	
	protected MapStructureEntityEditDialog getMediaEntityView() {
		 if (mapEntityAdd == null) {
			 mapEntityAdd = new MapStructureEntityEditDialog(MediaMapDataSourceFactory.MAPSTRUCTURE, getMediaMapKeys());
		 }
		 return mapEntityAdd;
	}

	@Override
	public CategoryDisplay getDisplay() {
		return (CategoryDisplay) display;
	}
	
}
