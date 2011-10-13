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

package org.broadleafcommerce.admin.client.presenter.catalog.product;

import com.smartgwt.client.data.*;
import org.broadleafcommerce.admin.client.datasource.catalog.category.CategoryListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.category.MediaMapDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.*;
import org.broadleafcommerce.admin.client.view.catalog.product.OneToOneProductSkuDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.presenter.structure.CreateBasedListStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.EditableJoinStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.MapStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.SimpleSearchJoinStructurePresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.MapStructureEntityEditDialog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
		initialValues.put("name", BLCMain.getMessageManager().getString("defaultProductName"));
		initialValues.put("_type", new String[]{getPresenterSequenceSetupManager().getDataSource("productDS").getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord(BLCMain.getMessageManager().getString("newProductTitle"), getPresenterSequenceSetupManager().getDataSource("productDS"), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				Criteria myCriteria = new Criteria();
				myCriteria.addCriteria("name", event.getRecord().getAttribute("name"));
				display.getListDisplay().getGrid().fetchData(myCriteria);
			}
		}, "90%", null, null);
	}

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productDS", new OneToOneProductSkuDataSourceFactory(), new AsyncCallbackAdapter() {
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
				getPresenterSequenceSetupManager().getDataSource("productDS").
				getFormItemCallbackHandlerManager().addSearchFormItemCallback(
					"defaultCategory", 
					categorySearchView, 
					BLCMain.getMessageManager().getString("categorySearchTitle"),
					getDisplay().getDynamicFormDisplay()
				);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("oneToOneProductSearchDS", new ProductListDataSourceFactory(), new AsyncCallbackAdapter() {
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
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("crossSaleProductsDS", new CrossSaleProductListDataSourceFactory(), new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				crossSalePresenter = new EditableJoinStructurePresenter(getDisplay().getCrossSaleDisplay(), productSearchView, BLCMain.getMessageManager().getString("productSearchTitle"), BLCMain.getMessageManager().getString("setPromotionMessageTitle"), "promotionMessage");
				crossSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("upSaleProductsDS", new UpSaleProductListDataSourceFactory(), new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				upSalePresenter = new EditableJoinStructurePresenter(getDisplay().getUpSaleDisplay(), productSearchView, BLCMain.getMessageManager().getString("productSearchTitle"), BLCMain.getMessageManager().getString("setPromotionMessageTitle"), "promotionMessage");
				upSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productMediaMapDS", new ProductMediaMapDataSourceFactory(this), null, new Object[]{getMediaMapKeys()}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				Map<String, Object> initialValues = new HashMap<String, Object>();
				initialValues.put("name", BLCMain.getMessageManager().getString("mediaNameDefault"));
				initialValues.put("label", BLCMain.getMessageManager().getString("mediaLabelDefault"));
				mediaPresenter = new MapStructurePresenter(getDisplay().getMediaDisplay(), getMediaEntityView(), BLCMain.getMessageManager().getString("newMediaTitle"), initialValues);
				mediaPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "name", "url", "label"}, new Boolean[]{true, true, true, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productAttributeDS", new ProductAttributeDataSourceFactory(), new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				Map<String, Object> initialValues = new HashMap<String, Object>();
				initialValues.put("name", "Untitled");
				productAttributePresenter = new CreateBasedListStructurePresenter(getDisplay().getAttributesDisplay(), BLCMain.getMessageManager().getString("newAttributeTitle"), initialValues);
				productAttributePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "value", "searchable"}, new Boolean[]{true, true, true});
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("parentCategoriesDS", new ParentCategoryListDataSourceFactory(), new OperationTypes(OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				parentCategoriesPresenter = new SimpleSearchJoinStructurePresenter(getDisplay().getAllCategoriesDisplay(), (EntitySearchDialog) library.get("categorySearchView"), BLCMain.getMessageManager().getString("categorySearchPrompt"));
				parentCategoriesPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "urlKey"}, new Boolean[]{false, false});
			}
		}));
	}
	
	protected LinkedHashMap<String, String> getMediaMapKeys() {
		LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>();
		keys.put("small", BLCMain.getMessageManager().getString("mediaSizeSmall"));
		keys.put("medium", BLCMain.getMessageManager().getString("mediaSizeMedium"));
		keys.put("large", BLCMain.getMessageManager().getString("mediaSizeLarge"));
		
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
