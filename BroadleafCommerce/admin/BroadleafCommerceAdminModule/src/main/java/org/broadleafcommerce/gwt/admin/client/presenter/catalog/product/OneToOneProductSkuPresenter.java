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
package org.broadleafcommerce.gwt.admin.client.presenter.catalog.product;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.category.CategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.category.MediaMapDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.product.CrossSaleProductListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.product.OneToOneProductSkuDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.product.ParentCategoryListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.product.ProductAttributeDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.product.ProductListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.product.ProductMediaMapDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.catalog.product.UpSaleProductListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.view.catalog.product.OneToOneProductSkuDisplay;
import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.gwt.client.presenter.structure.CreateBasedListStructurePresenter;
import org.broadleafcommerce.gwt.client.presenter.structure.EditableJoinStructurePresenter;
import org.broadleafcommerce.gwt.client.presenter.structure.MapStructurePresenter;
import org.broadleafcommerce.gwt.client.presenter.structure.SimpleSearchJoinStructurePresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.setup.PresenterSetupItem;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.MapStructureEntityEditDialog;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

/**
 * 
 * @author jfischer
 *
 */
public class OneToOneProductSkuPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected MapStructureEntityEditDialog mapEntityAdd = null;
	protected EntitySearchDialog productSearchView;
	protected SubPresentable crossSalePresenter;
	protected SubPresentable upSalePresenter;
	protected SubPresentable mediaPresenter;
	protected SubPresentable productAttributePresenter;
	protected SubPresentable parentCategoriesPresenter;
	protected HashMap<String, Object> library = new HashMap<String, Object>();

	@Override
	protected void changeSelection(final Record selectedRecord) {
		AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();
		display.getListDisplay().getAddButton().disable();
		crossSalePresenter.load(selectedRecord, dataSource, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				display.getListDisplay().getAddButton().enable();
			}
		});
		upSalePresenter.load(selectedRecord, dataSource, null);
		mediaPresenter.load(selectedRecord, dataSource, null);
		productAttributePresenter.load(selectedRecord, dataSource, null);
		parentCategoriesPresenter.load(selectedRecord, dataSource, null);
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
		initialValues.put("name", AdminModule.ADMINMESSAGES.defaultProductName());
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord(AdminModule.ADMINMESSAGES.newProductTitle(), (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				Criteria myCriteria = new Criteria();
				myCriteria.addCriteria("name", event.getRecord().getAttribute("name"));
				display.getListDisplay().getGrid().fetchData(myCriteria);
			}
		}, "90%", null, null);
	}

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productDS", new OneToOneProductSkuDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems(top);
				((ListGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("categorySearch", new CategoryListDataSourceFactory(), new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.JOINSTRUCTURE, OperationType.ENTITY, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				ListGridDataSource categorySearchDataSource = (ListGridDataSource) result;
				categorySearchDataSource.resetPermanentFieldVisibility(
					"name",
					"urlKey",
					"activeStartDate",
					"activeEndDate"
				);
				EntitySearchDialog categorySearchView = new EntitySearchDialog(categorySearchDataSource);
				library.put("categorySearchView", categorySearchView);
				((DynamicEntityDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).
				getFormItemCallbackHandlerManager().addSearchFormItemCallback(
					"defaultCategory", 
					categorySearchView, 
					AdminModule.ADMINMESSAGES.categorySearchTitle(), 
					getDisplay().getDynamicFormDisplay()
				);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("oneToOneProductSearchDS", new ProductListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				final ListGridDataSource productSearchDataSource = (ListGridDataSource) result;
				productSearchDataSource.resetPermanentFieldVisibility(
					"name",
					"description",
					"model",
					"manufacturer",
					"activeStartDate",
					"activeEndDate"
				);
				productSearchView = new EntitySearchDialog(productSearchDataSource);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("crossSaleProductsDS", new CrossSaleProductListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				crossSalePresenter = new EditableJoinStructurePresenter(getDisplay().getCrossSaleDisplay(), productSearchView, AdminModule.ADMINMESSAGES.productSearchTitle(), AdminModule.ADMINMESSAGES.setPromotionMessageTitle(), "promotionMessage");
				crossSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("upSaleProductsDS", new UpSaleProductListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				upSalePresenter = new EditableJoinStructurePresenter(getDisplay().getUpSaleDisplay(), productSearchView, AdminModule.ADMINMESSAGES.productSearchTitle(), AdminModule.ADMINMESSAGES.setPromotionMessageTitle(), "promotionMessage");
				upSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productMediaMapDS", new ProductMediaMapDataSourceFactory(this), null, new Object[]{getMediaMapKeys()}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				Map<String, Object> initialValues = new HashMap<String, Object>();
				initialValues.put("name", AdminModule.ADMINMESSAGES.mediaNameDefault());
				initialValues.put("label", AdminModule.ADMINMESSAGES.mediaLabelDefault());
				mediaPresenter = new MapStructurePresenter(getDisplay().getMediaDisplay(), getMediaEntityView(), AdminModule.ADMINMESSAGES.newMediaTitle(), initialValues);
				mediaPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "name", "url", "label"}, new Boolean[]{true, true, true, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productAttributeDS", new ProductAttributeDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				Map<String, Object> initialValues = new HashMap<String, Object>();
				initialValues.put("name", "Untitled");
				productAttributePresenter = new CreateBasedListStructurePresenter(getDisplay().getAttributesDisplay(), AdminModule.ADMINMESSAGES.newAttributeTitle(), initialValues);
				productAttributePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "value", "searchable"}, new Boolean[]{true, true, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("parentCategoriesDS", new ParentCategoryListDataSourceFactory(), new OperationTypes(OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				parentCategoriesPresenter = new SimpleSearchJoinStructurePresenter(getDisplay().getAllCategoriesDisplay(), (EntitySearchDialog) library.get("categorySearchView"), AdminModule.ADMINMESSAGES.categorySearchPrompt());
				parentCategoriesPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "urlKey"}, new Boolean[]{false, false});
			}
		}));
	}
	
	protected LinkedHashMap<String, String> getMediaMapKeys() {
		LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>();
		keys.put("small", AdminModule.ADMINMESSAGES.mediaSizeSmall());
		keys.put("medium", AdminModule.ADMINMESSAGES.mediaSizeMedium());
		keys.put("large", AdminModule.ADMINMESSAGES.mediaSizeLarge());
		
		return keys;
	}
	
	protected MapStructureEntityEditDialog getMediaEntityView() {
		 if (mapEntityAdd == null) {
			 mapEntityAdd = new MapStructureEntityEditDialog(MediaMapDataSourceFactory.MAPSTRUCTURE, getMediaMapKeys());
		 }
		 return mapEntityAdd;
	}

	@Override
	public OneToOneProductSkuDisplay getDisplay() {
		return (OneToOneProductSkuDisplay) display;
	}
}
