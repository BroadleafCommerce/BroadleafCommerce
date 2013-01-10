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

import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.admin.client.datasource.catalog.StaticAssetsTileGridDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.category.CategoryListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.category.MediaMapDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.CrossSaleProductListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.OneToOneProductSkuDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ParentCategoryListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductAttributeDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductMediaMapDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.UpSaleProductListDataSourceFactory;
import org.broadleafcommerce.admin.client.view.catalog.product.OneToOneProductSkuDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelected;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.CeilingEntities;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.presenter.structure.CreateBasedListStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.EditableJoinStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.MapStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.SimpleSearchJoinStructurePresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.AssetSearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.MapStructureEntityEditDialog;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class OneToOneProductSkuPresenter extends DynamicEntityPresenter implements Instantiable {

    protected MapStructureEntityEditDialog mapEntityAdd;
    protected EntitySearchDialog productSearchView;
    protected SubPresentable crossSalePresenter;
    protected SubPresentable upSalePresenter;
    protected SubPresentable mediaPresenter;
    protected SubPresentable productAttributePresenter;
    protected SubPresentable parentCategoriesPresenter;
    protected HashMap<String, Object> library = new HashMap<String, Object>(10);

    @Override
    protected void changeSelection(final Record selectedRecord) {
        AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();
        //display.getListDisplay().getAddButton().disable();
        crossSalePresenter.load(selectedRecord, dataSource, new DSCallback() {
            @Override
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                //display.getListDisplay().getAddButton().enable();
            }
        });
        upSalePresenter.load(selectedRecord, dataSource, null);
        mediaPresenter.load(selectedRecord, dataSource, null);
        productAttributePresenter.load(selectedRecord, dataSource, null);
        parentCategoriesPresenter.load(selectedRecord, dataSource, null);
    }

    @Override
    protected void saveClicked() {
        DynamicForm form = display.getDynamicFormDisplay().getFormOnlyDisplay().getForm();
        form.setValue("sku.name", (String) form.getValue("name"));
        form.setValue("sku.description", (String) form.getValue("description"));
        form.setValue("sku.longDescription", (String) form.getValue("longDescription"));
        form.setValue("sku.activeStartDate", (Date) form.getValue("activeStartDate"));
        form.setValue("sku.activeEndDate", (Date) form.getValue("activeEndDate"));
        super.saveClicked();
    }

    @Override
    protected void itemSaved(DSResponse response, Object rawData, DSRequest request) {
        super.itemSaved(response, rawData, request);
        getDisplay().getAllCategoriesDisplay().getGrid().invalidateCache();
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
        initialValues.put("name", BLCMain.getMessageManager().getString("defaultProductName"));
        addClicked(BLCMain.getMessageManager().getString("newProductTitle"));
    }

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productDS", new OneToOneProductSkuDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("categorySearch", new CategoryListDataSourceFactory(), new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.JOINSTRUCTURE, OperationType.ENTITY, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
            @Override
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
            @Override
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
            @Override
            public void onSetupSuccess(DataSource result) {
                crossSalePresenter = new EditableJoinStructurePresenter(getDisplay().getCrossSaleDisplay(), productSearchView, new String[]{EntityImplementations.PRODUCT}, BLCMain.getMessageManager().getString("productSearchTitle"), BLCMain.getMessageManager().getString("setPromotionMessageTitle"), "promotionMessage");
                crossSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("upSaleProductsDS", new UpSaleProductListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                upSalePresenter = new EditableJoinStructurePresenter(getDisplay().getUpSaleDisplay(), productSearchView, new String[]{EntityImplementations.PRODUCT}, BLCMain.getMessageManager().getString("productSearchTitle"), BLCMain.getMessageManager().getString("setPromotionMessageTitle"), "promotionMessage");
                upSalePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "promotionMessage"}, new Boolean[]{false, true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productMediaMapDS", new ProductMediaMapDataSourceFactory(this), null, new Object[]{getMediaMapKeys()}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                Map<String, Object> initialValues = new HashMap<String, Object>(2);
                initialValues.put("name", BLCMain.getMessageManager().getString("mediaNameDefault"));
                initialValues.put("label", BLCMain.getMessageManager().getString("mediaLabelDefault"));
                mediaPresenter = new MapStructurePresenter(getDisplay().getMediaDisplay(), getMediaEntityView(), new String[]{EntityImplementations.PRODUCT}, BLCMain.getMessageManager().getString("newMediaTitle"), initialValues);
                mediaPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "name", "url", "label"}, new Boolean[]{true, true, true, true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productAttributeDS", new ProductAttributeDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                Map<String, Object> initialValues = new HashMap<String, Object>(1);
                initialValues.put("name", "Untitled");
                productAttributePresenter = new CreateBasedListStructurePresenter(getDisplay().getAttributesDisplay(), new String[]{EntityImplementations.PRODUCT}, BLCMain.getMessageManager().getString("newAttributeTitle"), initialValues);
                productAttributePresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "value", "searchable"}, new Boolean[]{true, true, true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("parentCategoriesDS", new ParentCategoryListDataSourceFactory(), new OperationTypes(OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.JOINSTRUCTURE, OperationType.ENTITY), new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                parentCategoriesPresenter = new SimpleSearchJoinStructurePresenter(getDisplay().getAllCategoriesDisplay(), (EntitySearchDialog) library.get("categorySearchView"), new String[]{EntityImplementations.PRODUCT}, BLCMain.getMessageManager().getString("categorySearchPrompt"));
                parentCategoriesPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "urlKey"}, new Boolean[]{false, false});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTileGridDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                TileGridDataSource staticAssetTreeDS = (TileGridDataSource) dataSource;
                final AssetSearchDialog dialog = new AssetSearchDialog(staticAssetTreeDS);
                HashMap<String, Object> initialValues = new HashMap<String, Object>(10);
                initialValues.put("operation", "add");
                initialValues.put("customCriteria", "assetListUi");
                initialValues.put("ceilingEntityFullyQualifiedClassname", CeilingEntities.STATICASSETS);
                initialValues.put("_type", CeilingEntities.STATICASSETS);
                initialValues.put("csrfToken", BLCMain.csrfToken);
                dialog.setInitialValues(initialValues);

                getPresenterSequenceSetupManager().getDataSource("productMediaMapDS").getFormItemCallbackHandlerManager().addFormItemCallback("url", new FormItemCallback() {
                    @Override
                    public void execute(final FormItem formItem) {
                        dialog.search("Asset Search", new TileGridItemSelectedHandler() {
                            @Override
                            public void onSearchItemSelected(TileGridItemSelected event) {
                                String staticAssetFullUrl = BLCMain.assetServerUrlPrefix + event.getRecord().getAttribute("fullUrl");
                                formItem.setValue(staticAssetFullUrl);
                                getMediaEntityView().updateMedia(staticAssetFullUrl);
                            }
                        });
                    }
                });
            }
        }));
    }

    protected LinkedHashMap<String, String> getMediaMapKeys() {
        LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>(3);
        keys.put("small", BLCMain.getMessageManager().getString("mediaSizeSmall"));
        keys.put("medium", BLCMain.getMessageManager().getString("mediaSizeMedium"));
        keys.put("large", BLCMain.getMessageManager().getString("mediaSizeLarge"));

        return keys;
    }
    
    protected MapStructureEntityEditDialog getMediaEntityView() {
         if (mapEntityAdd == null) {
             mapEntityAdd = new MapStructureEntityEditDialog(MediaMapDataSourceFactory.MAPSTRUCTURE, getMediaMapKeys());
             mapEntityAdd.setShowMedia(true);
             mapEntityAdd.setMediaField("url");
         }
         return mapEntityAdd;
    }

    @Override
    public OneToOneProductSkuDisplay getDisplay() {
        return (OneToOneProductSkuDisplay) display;
    }
}
