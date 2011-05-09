package org.broadleafcommerce.gwt.client.presenter.catalog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.catalog.CategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.CategoryTreeDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.FeaturedProductListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.MediaMapDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.OrphanedCategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.ProductListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.relations.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.dynamic.DynamicListPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.catalog.CategoryDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.ComplexValueMapStructureEntityView;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntitySearchView;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.TreeNode;

public class CategoryPresenter extends DynamicListPresenter implements Instantiable {
	
	protected ComplexValueMapStructureEntityView mapEntityAdd = null;
	protected DynamicEntitySearchView categorySearchView;
	protected DynamicEntitySearchView productSearchView;
	protected ListGridDataSource categorySearchDataSource = null;
	protected ListGridDataSource productSearchDataSource = null;
	protected String rootId = "1";
	protected String rootName = "Store";

	@Override
	protected void addClicked() {
		Map<String, String> initialValues = new HashMap<String, String>();
		initialValues.put("defaultParentCategory", display.getGrid().getSelectedRecord().getAttribute("id"));
		initialValues.put("name", "Untitled");
		initialValues.put("type", ((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname());
		Main.ENTITY_ADD.editNewRecord((DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				reloadParentTreeNodeRecords(false);
				((CategoryDisplay) display).getAllChildCategoryGrid().invalidateCache();
			}
		});
	}

	@Override
	protected void removeClicked() {
		display.getGrid().removeSelectedData(new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					((CategoryDisplay) display).getOrphanedCategoryGrid().invalidateCache();
					((CategoryDisplay) display).getRemoveOrphanedButton().disable();
					((CategoryDisplay) display).getInsertOrphanButton().disable();
				}
			}
		}, null);
		display.getDynamicForm().disable();
		display.getFormToolBar().disable();
		display.getDynamicForm().reset();
		display.getRemoveButton().disable();
		((CategoryDisplay) display).getAllChildCategoryGrid().disable();
		((CategoryDisplay) display).getAddChildCategoryButton().disable();
		((CategoryDisplay) display).getRemoveChildCategoryButton().disable();
	}

	@Override
	protected void changeSelection(final Record selectedRecord) {
		if (categorySearchDataSource.stripDuplicateAllowSpecialCharacters(selectedRecord.getAttribute("id")).equals(rootId)){
			display.getDynamicForm().disable();
			display.getFormToolBar().disable();
			display.getRemoveButton().disable();
		}
		((PresentationLayerAssociatedDataSource) ((CategoryDisplay) display).getAllChildCategoryGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(selectedRecord.getAttribute("id"), new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					if (((CategoryDisplay) display).getOrphanedCategoryGrid().getSelectedRecord() != null) {
						((CategoryDisplay) display).getInsertOrphanButton().enable();
					}
					((CategoryDisplay) display).getAllChildCategoryGrid().enable();
					((CategoryDisplay) display).getAllChildCategoryToolBar().enable();
					
					if (!categorySearchDataSource.stripDuplicateAllowSpecialCharacters(selectedRecord.getAttribute("id")).equals(rootId)) {
						Criteria myCriteria = new Criteria();
						myCriteria.addCriteria("id", categorySearchDataSource.stripDuplicateAllowSpecialCharacters(selectedRecord.getAttribute(CategoryTreeDataSourceFactory.defaultParentCategoryForeignKey)));
						categorySearchDataSource.fetchData(myCriteria, new DSCallback(){
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								String name = response.getData()[0].getAttribute("name");
								((CategoryDisplay) display).getDefaultParentCategoryTextItem().setValue(name);
								((CategoryDisplay) display).getAddDefaultParentCategoryButton().enable();
							}
						});
					}
					
					((PresentationLayerAssociatedDataSource) ((CategoryDisplay) display).getMediaGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(selectedRecord.getAttribute("id"), new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							if (response.getErrors().isEmpty()) {
								((CategoryDisplay) display).getMediaGrid().enable();
								((CategoryDisplay) display).getAddMediaButton().enable();
							}
						}
					});
				} else {
					((CategoryDisplay) display).getInsertOrphanButton().disable();
				}
			}
		});
		display.getAddButton().disable();
		((PresentationLayerAssociatedDataSource) ((CategoryDisplay) display).getFeaturedProductGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(selectedRecord.getAttribute("id"), new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					display.getAddButton().enable();
				}
			}
		});
		((CategoryDisplay) display).getRemoveChildCategoryButton().disable();
		((CategoryDisplay) display).getAddFeaturedProductButton().enable();
		((CategoryDisplay) display).getFeaturedProductGrid().enable();
		((CategoryDisplay) display).getRemoveFeaturedProductButton().disable();
	}

	@Override
	public void bind() {
		super.bind();
		display.getSaveFormButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					display.getDynamicForm().saveData();
					display.getSaveFormButton().disable();
				}
			}
        });
		((CategoryDisplay) display).getAddChildCategoryButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				categorySearchView.search("Category Search", new SearchItemSelectedEventHandler() {
					public void onSearchItemSelected(SearchItemSelectedEvent event) {
						((CategoryDisplay) display).getAllChildCategoryGrid().addData(event.getRecord(), new DSCallback() {
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								if (response.getErrors().isEmpty()) {
									reloadParentTreeNodeRecords(true);
								}
							}
						}); 
					}
				});
			}
		});
		((CategoryDisplay) display).getRemoveChildCategoryButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				((CategoryDisplay) display).getAllChildCategoryGrid().removeData(((CategoryDisplay) display).getAllChildCategoryGrid().getSelectedRecord(), new DSCallback() {
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						if (response.getErrors().isEmpty()) {
							reloadParentTreeNodeRecords(true);
							((CategoryDisplay) display).getOrphanedCategoryGrid().invalidateCache();
							((CategoryDisplay) display).getRemoveOrphanedButton().disable();
							((CategoryDisplay) display).getInsertOrphanButton().disable();
						}
					}
				});
			}
		});
		((CategoryDisplay) display).getGrid().addDataArrivedHandler(new com.smartgwt.client.widgets.tree.events.DataArrivedHandler() {
			public void onDataArrived(com.smartgwt.client.widgets.tree.events.DataArrivedEvent event) {
				Record[] records = event.getParentNode().getAttributeAsRecordArray("children");
				for (Record record : records) {
					String hasChildren = ((TreeNode) record).getAttribute(CategoryTreeDataSourceFactory.hasChildrenProperty);
					if (hasChildren != null && !Boolean.parseBoolean(hasChildren)) {
						((CategoryDisplay) display).getGrid().getTree().loadChildren((TreeNode) record);
					}
				}
			}
        });
		((CategoryDisplay) display).getAllChildCategoryGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					((CategoryDisplay) display).getRemoveChildCategoryButton().enable();
				} else {
					((CategoryDisplay) display).getRemoveChildCategoryButton().disable();
				}
			}
		});
		((CategoryDisplay) display).getAllChildCategoryGrid().addRecordDropHandler(new RecordDropHandler() {
			public void onRecordDrop(RecordDropEvent event) {
				ListGridRecord record = event.getDropRecords()[0];
				int originalIndex = ((ListGrid) event.getSource()).getRecordIndex(record);
				int newIndex = event.getIndex();
				if (newIndex > originalIndex) {
					newIndex--;
				}
				JoinTable joinTable = (JoinTable) ((DynamicEntityDataSource) ((CategoryDisplay) display).getAllChildCategoryGrid().getDataSource()).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
				record.setAttribute(joinTable.getSortField(), newIndex);
				((CategoryDisplay) display).getAllChildCategoryGrid().updateData(record, new DSCallback() {
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						if (response.getErrors().isEmpty()) {
							reloadParentTreeNodeRecords(false);
						}
					}
				});
			}
		});
		((CategoryDisplay) display).getRemoveOrphanedButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					SC.confirm("Are your sure you want to delete this entity ("+((CategoryDisplay) display).getOrphanedCategoryGrid().getSelectedRecord().getAttribute("name")+")?", new BooleanCallback() {
						public void execute(Boolean value) {
							if (value) {
								((CategoryDisplay) display).getOrphanedCategoryGrid().removeSelectedData();
								((CategoryDisplay) display).getRemoveOrphanedButton().disable();
								((CategoryDisplay) display).getInsertOrphanButton().disable();
							}
						}
					});
				}
			}
		});
		((CategoryDisplay) display).getInsertOrphanButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					((CategoryDisplay) display).getAllChildCategoryGrid().addData(((CategoryDisplay) display).getOrphanedCategoryGrid().getSelectedRecord(), new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							if (response.getErrors().isEmpty()) {
								reloadParentTreeNodeRecords(true);
								((CategoryDisplay) display).getOrphanedCategoryGrid().invalidateCache();
								((CategoryDisplay) display).getRemoveOrphanedButton().disable();
								((CategoryDisplay) display).getInsertOrphanButton().disable();
							}
						}
					});
				}
			}
		});
		((CategoryDisplay) display).getOrphanedCategoryGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					((CategoryDisplay) display).getRemoveOrphanedButton().enable();
					if (display.getGrid().getSelectedRecord() != null && !((CategoryDisplay) display).getAllChildCategoryGrid().isDisabled()) {
						((CategoryDisplay) display).getInsertOrphanButton().enable();
					}
				} else {
					((CategoryDisplay) display).getRemoveOrphanedButton().disable();
					((CategoryDisplay) display).getInsertOrphanButton().disable();
				}
			}
		});
		((CategoryDisplay) display).getAddDefaultParentCategoryButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				categorySearchView.search("Category Search", new SearchItemSelectedEventHandler() {
					public void onSearchItemSelected(final SearchItemSelectedEvent event) {
						Criteria myCriteria = new Criteria();
						myCriteria.addCriteria("id", categorySearchDataSource.stripDuplicateAllowSpecialCharacters(display.getGrid().getSelectedRecord().getAttribute("id")));
						categorySearchDataSource.fetchData(myCriteria, new DSCallback(){
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								final Record myRecord = response.getData()[0];
								myRecord.setAttribute(CategoryTreeDataSourceFactory.defaultParentCategoryForeignKey, event.getRecord().getAttribute("id"));
								categorySearchDataSource.updateData(myRecord, new DSCallback() {
									public void execute(DSResponse response, Object rawData, DSRequest request) {
										categorySearchDataSource.setLinkedValue(event.getRecord().getAttribute("id"));
										categorySearchDataSource.addData(myRecord, new DSCallback() {
											public void execute(DSResponse response, Object rawData, DSRequest request) {
												((CategoryDisplay) display).getDefaultParentCategoryTextItem().setValue(event.getRecord().getAttribute("name"));
												display.getGrid().getSelectedRecord().setAttribute(CategoryTreeDataSourceFactory.defaultParentCategoryForeignKey, event.getRecord().getAttribute("id"));
												reloadAllChildRecordsForId(event.getRecord().getAttribute("id"));
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
		((CategoryDisplay) display).getAddFeaturedProductButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					productSearchView.search("Product Search", new SearchItemSelectedEventHandler() {
						@SuppressWarnings({ "rawtypes" })
						public void onSearchItemSelected(SearchItemSelectedEvent event) {
							Map initialValues = ((DynamicEntityDataSource) ((CategoryDisplay) display).getFeaturedProductGrid().getDataSource()).extractRecordValues((TreeNode) event.getRecord());
							Main.ENTITY_ADD.editNewRecord((DynamicEntityDataSource) ((CategoryDisplay) display).getFeaturedProductGrid().getDataSource(), initialValues, null, "promotionMessage");
						}
					});
				}
			}
		});
		((CategoryDisplay) display).getFeaturedProductGrid().addRecordDropHandler(new RecordDropHandler() {
			public void onRecordDrop(RecordDropEvent event) {
				ListGridRecord record = event.getDropRecords()[0];
				int originalIndex = ((ListGrid) event.getSource()).getRecordIndex(record);
				int newIndex = event.getIndex();
				if (newIndex > originalIndex) {
					newIndex--;
				}
				JoinTable joinTable = (JoinTable) ((DynamicEntityDataSource) ((CategoryDisplay) display).getAllChildCategoryGrid().getDataSource()).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
				record.setAttribute(joinTable.getSortField(), newIndex);
				((CategoryDisplay) display).getFeaturedProductGrid().updateData(record);
			}
		});
		((CategoryDisplay) display).getFeaturedProductGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					((CategoryDisplay) display).getRemoveFeaturedProductButton().enable();
				} else {
					((CategoryDisplay) display).getRemoveFeaturedProductButton().disable();
				}
			}
		});
		((CategoryDisplay) display).getRemoveFeaturedProductButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					((CategoryDisplay) display).getFeaturedProductGrid().removeData(((CategoryDisplay) display).getFeaturedProductGrid().getSelectedRecord(), new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							if (response.getErrors().isEmpty()) {
								((CategoryDisplay) display).getRemoveFeaturedProductButton().disable();
							}
						}
					});
				}
			}
		});
		((CategoryDisplay) display).getMediaGrid().addDataArrivedHandler(new DataArrivedHandler() {
			public void onDataArrived(DataArrivedEvent event) {
				((CategoryDisplay) display).getRemoveMediaButton().disable();
			}
		});
		((CategoryDisplay) display).getMediaGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					((CategoryDisplay) display).getRemoveMediaButton().enable();
				} else {
					((CategoryDisplay) display).getRemoveMediaButton().disable();
				}
			}
		});
		((CategoryDisplay) display).getRemoveMediaButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					((CategoryDisplay) display).getMediaGrid().removeData(((CategoryDisplay) display).getMediaGrid().getSelectedRecord(), new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							if (response.getErrors().isEmpty()) {
								((CategoryDisplay) display).getRemoveMediaButton().disable();
							}
						}
					});
				}
			}
		});
		((CategoryDisplay) display).getAddMediaButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					Map<String, String> initialValues = new HashMap<String, String>();
					initialValues.put("name", "Untitled");
					initialValues.put("label", "untitled");
					initialValues.put("symbolicId", ((DynamicEntityDataSource) ((CategoryDisplay) display).getMediaGrid().getDataSource()).getLinkedValue());
					String type = display.getGrid().getSelectedRecord().getAttribute("type");
					if (type == null) {
						type = ((DynamicEntityDataSource) ((CategoryDisplay) display).getMediaGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname();
					}
					initialValues.put("type", type);
					getMediaEntityView().editNewRecord("Add new Category Media", (DynamicEntityDataSource) ((CategoryDisplay) display).getMediaGrid().getDataSource(), initialValues, null);
				}
			}
		});
	}

	@Override
	public void go(final Canvas container) {
		CategoryTreeDataSourceFactory.createDataSource("categoryTreeDS", rootId, rootName, new AsyncCallback<DataSource>() {
			public void onFailure(Throwable caught) {
				//do nothing - let the framework handle the exception
			}

			public void onSuccess(DataSource top) {
				((CategoryDisplay) getDisplay()).build(top);
				((TreeGridDataSource) top).setAssociatedGrid(((CategoryDisplay) getDisplay()).getGrid());
				((TreeGridDataSource) top).setupFields(new String[]{}, new Boolean[]{}, "250", "100");
				CategoryListDataSourceFactory.createDataSource("allParentCategoriesDS", new AsyncCallback<DataSource>() {
					public void onFailure(Throwable caught) {
						//do nothing - let the framework handle the exception
					}

					public void onSuccess(DataSource result) {
						((CategoryDisplay) getDisplay()).getAllChildCategoryGrid().setDataSource(result);
						((ListGridDataSource) result).setAssociatedGrid(((CategoryDisplay) getDisplay()).getAllChildCategoryGrid());
						((ListGridDataSource) result).setupFields(new String[]{"name", "urlKey"}, new Boolean[]{false, false});
						
						OrphanedCategoryListDataSourceFactory.createDataSource("orphanedCategoriesDS", rootId, new AsyncCallback<DataSource>() {
							public void onFailure(Throwable caught) {
								//do nothing - let the framework handle the exception
							}

							public void onSuccess(DataSource result) {
								((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid().setDataSource(result);
								((ListGridDataSource) result).setAssociatedGrid(((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid());
								((ListGridDataSource) result).setupFields(new String[]{"name", "urlKey"}, new Boolean[]{false, false});
								
								Criteria myCriteria = new Criteria();
								myCriteria.addCriteria(OrphanedCategoryListDataSourceFactory.foreignKeyName, "0");
								
								((CategoryDisplay) getDisplay()).getOrphanedCategoryGrid().fetchData(myCriteria);
								
								OperationTypes operationTypes = new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.JOINTABLE, OperationType.ENTITY, OperationType.ENTITY);
								CategoryListDataSourceFactory.createDataSource("categorySearch", operationTypes, new AsyncCallback<DataSource>() {
									public void onFailure(Throwable caught) {
										//do nothing - let the framework handle the exception
									}

									public void onSuccess(DataSource result) {
										categorySearchDataSource = (ListGridDataSource) result;
										categorySearchDataSource.resetFieldVisibility(
											"name",
											"urlKey",
											"activeStartDate",
											"activeEndDate"
										);
										categorySearchView = new DynamicEntitySearchView(categorySearchDataSource);
										
										FeaturedProductListDataSourceFactory.createDataSource("featuredProductsDS", new AsyncCallback<DataSource>() {
											public void onFailure(Throwable caught) {
												//do nothing - let the framework handle the exception
											}
		
											public void onSuccess(DataSource result) {
												((CategoryDisplay) getDisplay()).getFeaturedProductGrid().setDataSource(result);
												((ListGridDataSource) result).setAssociatedGrid(((CategoryDisplay) getDisplay()).getFeaturedProductGrid());
												((ListGridDataSource) result).setupFields(new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
												
												ProductListDataSourceFactory.createDataSource("productSearch", new AsyncCallback<DataSource>() {
													public void onFailure(Throwable caught) {
														//do nothing - let the framework handle the exception
													}

													public void onSuccess(DataSource result) {
														productSearchDataSource = (ListGridDataSource) result;
														productSearchDataSource.resetFieldVisibility(
															"name",
															"description",
															"model",
															"manufacturer",
															"activeStartDate",
															"activeEndDate"
														);
														productSearchView = new DynamicEntitySearchView(productSearchDataSource);
														
														MediaMapDataSourceFactory.createDataSource("mediaMapDS", getMediaMapKeys(), ((CategoryDisplay) getDisplay()).getMediaGrid(), new AsyncCallback<DataSource>() {
															public void onFailure(Throwable caught) {
																//do nothing - let the framework handle the exception
															}

															public void onSuccess(DataSource result) {
																((CategoryDisplay) getDisplay()).getMediaGrid().setDataSource(result);
																((ListGridDataSource) result).setAssociatedGrid(((CategoryDisplay) getDisplay()).getMediaGrid());
																((ListGridDataSource) result).setupFields(new String[]{"key", "name", "url", "label"}, new Boolean[]{true, true, true, true});
																
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
	
	public void reloadAllChildRecordsForId(String id) {
		String startingId = categorySearchDataSource.stripDuplicateAllowSpecialCharacters(id);
		RecordList resultSet = display.getGrid().getRecordList();
		if (resultSet != null) {
			Record[] myRecords = resultSet.toArray();
			for (Record myRecord : myRecords) {
				String myId = categorySearchDataSource.stripDuplicateAllowSpecialCharacters(myRecord.getAttribute("id"));
				if (startingId.equals(myId)) {
					((CategoryDisplay) display).getGrid().getTree().reloadChildren((TreeNode) myRecord);
				}
			}
		}
	}

	public void reloadParentTreeNodeRecords(boolean disableCategoryButton) {
		TreeNode parentRecord = (TreeNode) display.getGrid().getSelectedRecord();
		reloadAllChildRecordsForId(parentRecord.getAttribute("id"));
		if (disableCategoryButton) {
			((CategoryDisplay) display).getRemoveChildCategoryButton().disable();
		}
	}
	
	protected LinkedHashMap<String, String> getMediaMapKeys() {
		LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>();
		keys.put("small", "Small");
		keys.put("medium", "Medium");
		keys.put("large", "Large");
		
		return keys;
	}
	
	protected ComplexValueMapStructureEntityView getMediaEntityView() {
		 if (mapEntityAdd == null) {
			 mapEntityAdd = new ComplexValueMapStructureEntityView(MediaMapDataSourceFactory.MAPSTRUCTURE, getMediaMapKeys());
		 }
		 return mapEntityAdd;
	}
}
