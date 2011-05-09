package org.broadleafcommerce.gwt.client.presenter.catalog;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.catalog.CategoryListDataSource;
import org.broadleafcommerce.gwt.client.datasource.catalog.CategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.CategoryTreeDataSource;
import org.broadleafcommerce.gwt.client.datasource.catalog.CategoryTreeDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.dynamic.DynamicListPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.catalog.CategoryDisplay;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.LoadState;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.DataArrivedEvent;
import com.smartgwt.client.widgets.tree.events.DataArrivedHandler;

public class CategoryPresenter extends DynamicListPresenter implements Instantiable {
	
	CategoryListDataSource categorySearchDataSource = null;

	@Override
	protected ClickHandler getAddHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					Map<String, String> initialValues = new HashMap<String, String>();
					initialValues.put("defaultParentCategory", display.getGrid().getSelectedRecord().getAttribute("id"));
					initialValues.put("name", "Untitled");
					initialValues.put("type", ((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname());
					Main.ENTITY_ADD.editNewRecord((DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues);
				}
			}
        };
	}

	@Override
	protected void changeSelection() {
		((CategoryDisplay) display).getAllParentCategoryGrid().enable();
		((CategoryDisplay) display).getAllParentCategoryToolBar().enable();
		
		CategoryListDataSource allPDS = (CategoryListDataSource)((CategoryDisplay) getDisplay()).getAllParentCategoryGrid().getDataSource();
		allPDS.setLinkedValue(lastSelectedRecord.getAttribute("id"));
		Criteria criteria = new Criteria();
		criteria.addCriteria("allParentCategories", lastSelectedRecord.getAttribute("id"));
		((CategoryDisplay) getDisplay()).getAllParentCategoryGrid().fetchData(criteria);
		((CategoryDisplay) display).getRemoveParentCategoryButton().disable();
	}

	@Override
	public void bind() {
		super.bind();
		((CategoryDisplay) display).getAddParentCategoryButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (categorySearchDataSource == null) {
					CategoryListDataSourceFactory.createDataSource("categorySearch", RemoveType.REGULAR, new AsyncCallback<DataSource>() {
						public void onFailure(Throwable caught) {
							//do nothing - let the framework handle the exception
						}

						public void onSuccess(DataSource result) {
							categorySearchDataSource = (CategoryListDataSource) result;
							categorySearchDataSource.resetFieldVisibility("name", "urlKey", "activeStartDate", "activeEndDate");
							Main.SEARCH_VIEW.search(categorySearchDataSource, "Category Search", new SearchItemSelectedEventHandler() {
								public void onSearchItemSelected(SearchItemSelectedEvent event) {
									((CategoryDisplay) display).getAllParentCategoryGrid().addData(event.getRecord(), new DSCallback() {
										public void execute(DSResponse response, Object rawData, DSRequest request) {
											if (response.getErrors().isEmpty()) {
												TreeNode parentRecord = ((CategoryDisplay) display).getGrid().getTree().findById(((CategoryDisplay) display).getGrid().getSelectedRecord().getAttribute("id"));
												LoadState loaded = ((CategoryDisplay) display).getGrid().getTree().getLoadState(parentRecord);
												if (loaded != null) {
													((CategoryDisplay) display).getGrid().getTree().reloadChildren(parentRecord); 
												} 
												((CategoryDisplay) display).getRemoveParentCategoryButton().disable();
											}
										}
									}); 
								}
							});
						}
					});
				} else {
					Main.SEARCH_VIEW.search(categorySearchDataSource, "Category Search", new SearchItemSelectedEventHandler() {
						public void onSearchItemSelected(SearchItemSelectedEvent event) {
							((CategoryDisplay) display).getAllParentCategoryGrid().addData(event.getRecord(), new DSCallback() {
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if (response.getErrors().isEmpty()) {
										TreeNode parentRecord = ((CategoryDisplay) display).getGrid().getTree().findById(((CategoryDisplay) display).getGrid().getSelectedRecord().getAttribute("id"));
										LoadState loaded = ((CategoryDisplay) display).getGrid().getTree().getLoadState(parentRecord);
										if (loaded != null) {
											((CategoryDisplay) display).getGrid().getTree().reloadChildren(parentRecord); 
										} 
										((CategoryDisplay) display).getRemoveParentCategoryButton().disable();
									}
								}
							});
						}
					});
				}
			}
		});
		((CategoryDisplay) display).getRemoveParentCategoryButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				((CategoryDisplay) display).getAllParentCategoryGrid().removeData(((CategoryDisplay) display).getAllParentCategoryGrid().getSelectedRecord());
				TreeNode parentRecord = ((CategoryDisplay) display).getGrid().getTree().findById(((CategoryDisplay) display).getGrid().getSelectedRecord().getAttribute("id"));
				LoadState loaded = ((CategoryDisplay) display).getGrid().getTree().getLoadState(parentRecord);
				if (loaded != null) {
					((CategoryDisplay) display).getGrid().getTree().reloadChildren(parentRecord); 
				}
				((CategoryDisplay) display).getRemoveParentCategoryButton().disable();
			}
		});
		((CategoryDisplay) display).getGrid().addDataArrivedHandler(new DataArrivedHandler() {
			public void onDataArrived(DataArrivedEvent event) {
				Record[] records = event.getParentNode().getAttributeAsRecordArray("children");
				for (Record record : records) {
					String hasChildren = ((TreeNode) record).getAttribute(CategoryTreeDataSource.hasChildrenProperty);
					if (hasChildren != null && !Boolean.parseBoolean(hasChildren)) {
						((CategoryDisplay) display).getGrid().getTree().loadChildren((TreeNode) record);
					}
				}
			}
        });
		((CategoryDisplay) display).getAllParentCategoryGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					((CategoryDisplay) display).getRemoveParentCategoryButton().enable();
				} else {
					((CategoryDisplay) display).getRemoveParentCategoryButton().disable();
				}
			}
		});
		((CategoryDisplay) display).getAllParentCategoryGrid().addRecordDropHandler(new RecordDropHandler() {
			public void onRecordDrop(RecordDropEvent event) {
				
			}
		});
	}

	@Override
	public void go(final Canvas container) {
		CategoryTreeDataSourceFactory.createDataSource("categoryTreeDS", RemoveType.COLLECTION, new AsyncCallback<DataSource>() {
			public void onFailure(Throwable caught) {
				//do nothing - let the framework handle the exception
			}

			public void onSuccess(DataSource top) {
				((CategoryDisplay) getDisplay()).build(top);
				((TreeGridDataSource) top).setAssociatedGrid(((CategoryDisplay) getDisplay()).getGrid());
				((TreeGridDataSource) top).setupFields();
				CategoryListDataSourceFactory.createDataSource("allParentCategoriesDS", RemoveType.COLLECTION, new AsyncCallback<DataSource>() {
					public void onFailure(Throwable caught) {
						//do nothing - let the framework handle the exception
					}

					public void onSuccess(DataSource result) {
						((DynamicEntityDataSource) result).resetFieldVisibility("urlKey", "name");
						((CategoryDisplay) getDisplay()).getAllParentCategoryGrid().setDataSource(result);
						((ListGridDataSource) result).setAssociatedGrid(((CategoryDisplay) getDisplay()).getAllParentCategoryGrid());
						((ListGridDataSource) result).setupFields();
						CategoryPresenter.super.go(container);
					}
				});
			}
		});
	}
	
	
}
