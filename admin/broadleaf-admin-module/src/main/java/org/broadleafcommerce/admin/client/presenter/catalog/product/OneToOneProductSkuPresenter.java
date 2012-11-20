/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.client.presenter.catalog.product;

import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.admin.client.datasource.catalog.category.CategoryListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.BundleSkuSearchDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.OneToOneProductSkuDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ParentCategoryListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductOptionDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductOptionListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductOptionValueDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductSkusDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.SkuBundleItemsDataSourceFactory;
import org.broadleafcommerce.admin.client.dto.AdminExporterDTO;
import org.broadleafcommerce.admin.client.dto.AdminExporterType;
import org.broadleafcommerce.admin.client.service.AppServices;
import org.broadleafcommerce.admin.client.view.catalog.product.OneToOneProductSkuDisplay;
import org.broadleafcommerce.admin.client.view.dialog.ExportListSelectionDialog;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelected;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.CeilingEntities;
import org.broadleafcommerce.openadmin.client.datasource.StaticAssetsTileGridDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallback;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.presenter.structure.EditableAdornedTargetListPresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.MapStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.SimpleSearchListPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.AssetSearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;

import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class OneToOneProductSkuPresenter extends DynamicEntityPresenter implements Instantiable {

	protected EntitySearchDialog productSearchView;
	protected EntitySearchDialog skuSearchView;
	protected SubPresentable parentCategoriesPresenter;
	protected AssociatedProductOptionPresenterBasic productOptionsPresenter;
	protected SubPresentable skusPresenter;
	protected SubPresentable bundleItemsPresenter;
	protected HashMap<String, Object> library = new HashMap<String, Object>(10);
    protected HandlerRegistration extendedFetchDataHandlerRegistration;

	@Override
	protected void changeSelection(final Record selectedRecord) {
		AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();
        parentCategoriesPresenter.load(selectedRecord, dataSource, null);
        productOptionsPresenter.load(selectedRecord, dataSource, null);
        skusPresenter.load(selectedRecord, dataSource, null);
        bundleItemsPresenter.load(selectedRecord, dataSource, null);
        getDisplay().getCloneProductButton().enable();

	}

    @Override
    protected void itemSaved(DSResponse response, Object rawData, DSRequest request) {
        super.itemSaved(response, rawData, request);
        getDisplay().getAllCategoriesDisplay().getGrid().invalidateCache();
    }
	
	@Override
	public void bind() {
		super.bind();
		parentCategoriesPresenter.bind();
		productOptionsPresenter.bind();
		skusPresenter.bind();
		bundleItemsPresenter.bind();
	    
		getDisplay().getGenerateSkusButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (getDisplay().getProductOptionsDisplay().getGrid().getTotalRows() <= 0) {
                    SC.say(BLCMain.getMessageManager().getString("skuGenerationInvalid"));
                } else {
                    SC.confirm(BLCMain.getMessageManager().getString("generateSkusConfirm"), new BooleanCallback() {
                        @Override
                        public void execute(Boolean value) {
                            if (value) {
                                Long productId = Long.parseLong(getDisplay().getListDisplay().getGrid().getSelectedRecord().getAttribute("id"));
                                AppServices.CATALOG.generateSkusFromProduct(productId, new AsyncCallback<Integer>() {
                                    @Override
                                    public void onSuccess(Integer result) {
                                        //we just finished creating a bunch of Skus, reload the grid
                                        getDisplay().getSkusDisplay().getGrid().invalidateCache();
                                        SC.say(result + " " + BLCMain.getMessageManager().getString("skuGenerationSuccess"));
                                    }

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        SC.say(BLCMain.getMessageManager().getString("skuGenerationFail"));
                                    }
                                });
                            } else {
                                SC.say(BLCMain.getMessageManager().getString("noSkusGenerated"));
                            }
                        }
                    });
                }
            }
        });
		
		getDisplay().getCloneProductButton().addClickHandler(new ClickHandler() {
		    @Override
		    public void onClick(ClickEvent event) {
                final Long productId = Long.parseLong(getDisplay().getListDisplay().getGrid().getSelectedRecord().getAttribute("id"));
                final String productName = getDisplay().getListDisplay().getGrid().getSelectedRecord().getAttribute("defaultSku.name");
                AppServices.CATALOG.cloneProduct(productId, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        SC.say("There was an error when cloning product " + productName);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            getDisplay().getListDisplay().getGrid().invalidateCache();
                            SC.say(productName + " has been cloned successfully");
                        } else {
                            SC.say("There was an error when cloning product " + productName);
                        }
                    }
                });
		    }
		});
		
		getDisplay().getExportProductsButton().addClickHandler(new ClickHandler() {
		    @Override
		    public void onClick(ClickEvent event) {
		        AppServices.EXPORT.getExporters(AdminExporterType.PRODUCT, new AsyncCallback<List<AdminExporterDTO>>() {
		            @Override
		            public void onSuccess(final List<AdminExporterDTO> result) {
		                if (result == null || result.size() == 0) {
		                    SC.say(BLCMain.getMessageManager().getString("noProductExporters"));
		                } else {
		                    ExportListSelectionDialog exportSelectionDialog = new ExportListSelectionDialog();
		                    exportSelectionDialog.search(BLCMain.getMessageManager().getString("selectExporterTitle"), result);
		                }
		            }

		            @Override
		            public void onFailure(Throwable caught) {
		                // TODO Auto-generated method stub
                
		            }
		        });
		    }
		});
		
        extendedFetchDataHandlerRegistration = display.getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                ((OneToOneProductSkuDisplay) display).getCloneProductButton().disable();
            }
        });

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
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("categorySearch", new CategoryListDataSourceFactory(), new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.ADORNEDTARGETLIST, OperationType.BASIC, OperationType.BASIC), new Object[]{}, new AsyncCallbackAdapter() {
			@Override
            public void onSetupSuccess(DataSource result) {
				ListGridDataSource categorySearchDataSource = (ListGridDataSource) result;
				EntitySearchDialog categorySearchView = new EntitySearchDialog(categorySearchDataSource);
				library.put("categorySearchView", categorySearchView);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("oneToOneProductSearchDS", new ProductListDataSourceFactory(), new AsyncCallbackAdapter() {
			@Override
            public void onSetupSuccess(DataSource result) {
				final ListGridDataSource productSearchDataSource = (ListGridDataSource) result;
				productSearchView = new EntitySearchDialog(productSearchDataSource);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("parentCategoriesDS", new ParentCategoryListDataSourceFactory(), new OperationTypes(OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.BASIC), new Object[]{}, new AsyncCallbackAdapter() {
			@Override
            public void onSetupSuccess(DataSource result) {
				parentCategoriesPresenter = new SimpleSearchListPresenter("",getDisplay().getAllCategoriesDisplay(), (EntitySearchDialog) library.get("categorySearchView"), BLCMain.getMessageManager().getString("categorySearchPrompt"));
				parentCategoriesPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "urlKey"}, new Boolean[]{false, false});
			}
		}));
		
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionSearchDS", new ProductOptionListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                EntitySearchDialog productOptionSearchView = new EntitySearchDialog((ListGridDataSource)result, true);
                library.put("productOptionSearchView", productOptionSearchView);
            }
        }));

		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionValuesDS", new ProductOptionValueDataSourceFactory(), new NullAsyncCallbackAdapter()));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionsDS", new ProductOptionDataSourceFactory(), new AsyncCallbackAdapter() {
		    @Override
            public void onSetupSuccess(DataSource result) {
		        productOptionsPresenter = new AssociatedProductOptionPresenterBasic(getDisplay().getProductOptionsDisplay(), (EntitySearchDialog)library.get("productOptionSearchView"), BLCMain.getMessageManager().getString("productOptionSearchPrompt"));
		        productOptionsPresenter.setDataSource((ListGridDataSource) result, new String[]{"label", "type", "required"}, new Boolean[]{true, true, true});
		        productOptionsPresenter.setExpansionDataSource((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("productOptionValuesDS"), new String[]{"displayOrder","attributeValue","priceAdjustment"}, new Boolean[]{false,false, false});
		    }
		}));
		
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("skusDS", new ProductSkusDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                skusPresenter = new SkusPresenter(getDisplay().getSkusDisplay(), "Add Sku", null, false, true, false);
                //grid fields are managed by declared prominence on the entity itself
                skusPresenter.setDataSource((ListGridDataSource) result, new String[]{}, new Boolean[]{});
            }
        }));
		
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("skuSearchDS", new BundleSkuSearchDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ListGridDataSource skuSearchDataSource = (ListGridDataSource)result;
                skuSearchDataSource.resetPermanentFieldVisibility("name", "retailPrice", "salePrice");
                skuSearchView = new EntitySearchDialog(skuSearchDataSource, true);
                skuSearchView.setWidth(800);
            }
        }));

        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("bundleSkusDS", new SkuBundleItemsDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                bundleItemsPresenter = new EditableAdornedTargetListPresenter("",getDisplay().getBundleItemsDisplay(), skuSearchView, new String[]{EntityImplementations.PRODUCT_BUNDLE}, BLCMain.getMessageManager().getString("skuSelect"), BLCMain.getMessageManager().getString("editBundleItem"), new String[]{"quantity", "itemSalePrice"});
                bundleItemsPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "quantity", "itemSalePrice"}, new Boolean[]{false, false, false});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTileGridDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
            	TileGridDataSource staticAssetTreeDS = (TileGridDataSource) dataSource;
            	final AssetSearchDialog dialog=new AssetSearchDialog(staticAssetTreeDS);
                HashMap<String, Object> initialValues = new HashMap<String, Object>(10);
                initialValues.put("operation", "add");
                initialValues.put("customCriteria", "assetListUi");
                initialValues.put("ceilingEntityFullyQualifiedClassname", CeilingEntities.STATICASSETS);
                initialValues.put("_type", CeilingEntities.STATICASSETS);
                initialValues.put("csrfToken", BLCMain.csrfToken);
                dialog.setInitialValues(initialValues);
                library.put("staticAssetDialog", dialog);
            }
        }));
	}

    @Override
    public void postSetup(Canvas container) {
        getPresenterSequenceSetupManager().getDataSource("productMediaMapDS").getFormItemCallbackHandlerManager().addFormItemCallback("url", new FormItemCallback() {
            @Override
            public void execute(final FormItem formItem) {
                ((AssetSearchDialog) library.get("staticAssetDialog")).search("Asset Search", new TileGridItemSelectedHandler() {
                    @Override
                    public void onSearchItemSelected(TileGridItemSelected event) {
                        String staticAssetFullUrl = BLCMain.assetServerUrlPrefix + event.getRecord().getAttribute("fullUrl");
                        formItem.setValue(staticAssetFullUrl);
                        ((MapStructurePresenter) subPresentables.get("productMediaMapDS")).getEntityEditDialog().updateMedia(staticAssetFullUrl);
                    }
                });
            }
        });
        
        gridHelper.addSubPresentableHandlers(display.getListDisplay().getGrid(),parentCategoriesPresenter,productOptionsPresenter,skusPresenter,bundleItemsPresenter,subPresentables.get("productMediaMapDS") );
        
        super.postSetup(container);
    }

	@Override
	public OneToOneProductSkuDisplay getDisplay() {
		return (OneToOneProductSkuDisplay) display;
	}
}
