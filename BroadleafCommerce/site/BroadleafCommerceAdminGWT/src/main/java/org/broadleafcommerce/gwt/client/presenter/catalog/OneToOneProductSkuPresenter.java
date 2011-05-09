package org.broadleafcommerce.gwt.client.presenter.catalog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.CategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.category.MediaMapDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.CrossSaleProductListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.OneToOneProductSkuDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.ParentCategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.ProductAttributeDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.ProductListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.ProductMediaMapDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.catalog.product.UpSaleProductListDataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.SubPresenter;
import org.broadleafcommerce.gwt.client.presenter.dynamic.structure.CreateBasedListStructurePresenter;
import org.broadleafcommerce.gwt.client.presenter.dynamic.structure.EditableJoinStructurePresenter;
import org.broadleafcommerce.gwt.client.presenter.dynamic.structure.MapStructurePresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.catalog.OneToOneProductSkuDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.ComplexValueMapStructureEntityEditDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

public class OneToOneProductSkuPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected ComplexValueMapStructureEntityEditDialog mapEntityAdd = null;
	protected EntitySearchDialog productSearchView;
	protected EditableJoinStructurePresenter crossSalePresenter;
	protected EditableJoinStructurePresenter upSalePresenter;
	protected MapStructurePresenter mediaPresenter;
	protected CreateBasedListStructurePresenter productAttributePresenter;
	protected SubPresenter parentCategoriesPresenter;

	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		display.getListDisplay().getAddButton().disable();
		crossSalePresenter.load(selectedRecord, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					display.getListDisplay().getAddButton().enable();
				}
			}
		});
		upSalePresenter.load(selectedRecord, null);
		mediaPresenter.load(selectedRecord, null);
		productAttributePresenter.load(selectedRecord, null);
		parentCategoriesPresenter.load(selectedRecord, null);
	}
	
	@Override
	public void bind() {
		super.bind();
		crossSalePresenter.bind();
		upSalePresenter.bind();
		mediaPresenter.bind();
		productAttributePresenter.bind();
		parentCategoriesPresenter.bind();
	}
	
	@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("name", "Untitled");
		initialValues.put("type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		Main.ENTITY_ADD.editNewRecord("Create New Product", (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				Criteria myCriteria = new Criteria();
				myCriteria.addCriteria("name", event.getRecord().getAttribute("name"));
				display.getListDisplay().getGrid().fetchData(myCriteria);
			}
		}, "90%", null);
	}

	@Override
	protected void removeClicked() {
		display.getListDisplay().getGrid().removeSelectedData();
		formPresenter.disable();
		display.getListDisplay().getRemoveButton().disable();
	}

	@Override
	public void go(final Canvas container) {
		Main.NON_MODAL_PROGRESS.startProgress();
		if (loaded) {
			OneToOneProductSkuPresenter.super.go(container);
			return;
		}
		OneToOneProductSkuDataSourceFactory.createDataSource("productDS", new AsyncCallbackAdapter() {
			public void onSuccess(DataSource top) {
				setupDisplayItems(top);
				((ListGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{});
				
				OperationTypes operationTypes = new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.JOINSTRUCTURE, OperationType.ENTITY, OperationType.ENTITY);
				CategoryListDataSourceFactory.createDataSource("categorySearch", operationTypes, new AsyncCallbackAdapter() {
					public void onSuccess(DataSource result) {
						ListGridDataSource categorySearchDataSource = (ListGridDataSource) result;
						categorySearchDataSource.resetFieldVisibility(
							"name",
							"urlKey",
							"activeStartDate",
							"activeEndDate"
						);
						EntitySearchDialog categorySearchView = new EntitySearchDialog(categorySearchDataSource);
						((DynamicEntityDataSource) ((OneToOneProductSkuDisplay) getDisplay()).getListDisplay().getGrid().getDataSource()).
						getFormItemCallbackHandlerManager().addSearchFormItemCallback(
							"defaultCategory", 
							categorySearchView, 
							"Category Search", 
							((OneToOneProductSkuDisplay) getDisplay()).getListDisplay().getGrid(), 
							((OneToOneProductSkuDisplay) getDisplay()).getDynamicFormDisplay()
						);
				
						ProductListDataSourceFactory.createDataSource("oneToOneProductSearchDS", new AsyncCallbackAdapter() {
							public void onSuccess(DataSource result) {
								final ListGridDataSource productSearchDataSource = (ListGridDataSource) result;
								productSearchDataSource.resetFieldVisibility(
									"name",
									"description",
									"model",
									"manufacturer",
									"activeStartDate",
									"activeEndDate"
								);
								productSearchView = new EntitySearchDialog(productSearchDataSource);
						
								CrossSaleProductListDataSourceFactory.createDataSource("crossSaleProductsDS", new AsyncCallbackAdapter() {
									public void onSuccess(DataSource result) {
										crossSalePresenter = new EditableJoinStructurePresenter(((OneToOneProductSkuDisplay) getDisplay()).getCrossSaleDisplay(), productSearchView, "Product Search", "Set Promotion Message", "promotionMessage");
										crossSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
										
										UpSaleProductListDataSourceFactory.createDataSource("upSaleProductsDS", new AsyncCallbackAdapter() {
											public void onSuccess(DataSource result) {
												upSalePresenter = new EditableJoinStructurePresenter(((OneToOneProductSkuDisplay) getDisplay()).getUpSaleDisplay(), productSearchView, "Product Search", "Set Promotion Message", "promotionMessage");
												upSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
												
												ProductMediaMapDataSourceFactory.createDataSource("productMediaMapDS", getMediaMapKeys(), ((OneToOneProductSkuDisplay) getDisplay()).getMediaDisplay().getGrid(), new AsyncCallbackAdapter() {
													public void onSuccess(DataSource result) {
														Map<String, Object> initialValues = new HashMap<String, Object>();
														initialValues.put("name", "Untitled");
														initialValues.put("label", "untitled");
														mediaPresenter = new MapStructurePresenter(((OneToOneProductSkuDisplay) getDisplay()).getMediaDisplay(), getMediaEntityView(), "Add New Media", initialValues);
														mediaPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "name", "url", "label"}, new Boolean[]{true, true, true, true});
														
														ProductAttributeDataSourceFactory.createDataSource("productAttributeDS", new AsyncCallbackAdapter() {
															public void onSuccess(DataSource result) {
																Map<String, Object> initialValues = new HashMap<String, Object>();
																initialValues.put("name", "Untitled");
																productAttributePresenter = new CreateBasedListStructurePresenter(((OneToOneProductSkuDisplay) getDisplay()).getAttributesDisplay(), "Add New Attribute", initialValues);
																productAttributePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "value", "searchable"}, new Boolean[]{true, true, true});
																		
																ParentCategoryListDataSourceFactory.createDataSource("parentCategoriesDS", new AsyncCallbackAdapter() {
																	public void onSuccess(DataSource result) {
																		parentCategoriesPresenter = new AllParentCategoriesPresenter(((OneToOneProductSkuDisplay) getDisplay()).getAllCategoriesDisplay());
																		((AllParentCategoriesPresenter) parentCategoriesPresenter).setDataSource((ListGridDataSource) result, new String[]{"name", "urlKey"}, new Boolean[]{false, false});
																		OneToOneProductSkuPresenter.super.go(container);
																		Main.NON_MODAL_PROGRESS.stopProgress();
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
	
	protected LinkedHashMap<String, String> getMediaMapKeys() {
		LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>();
		keys.put("small", "Small");
		keys.put("medium", "Medium");
		keys.put("large", "Large");
		
		return keys;
	}
	
	protected ComplexValueMapStructureEntityEditDialog getMediaEntityView() {
		 if (mapEntityAdd == null) {
			 mapEntityAdd = new ComplexValueMapStructureEntityEditDialog(MediaMapDataSourceFactory.MAPSTRUCTURE, getMediaMapKeys());
		 }
		 return mapEntityAdd;
	}
}
