/*
 * Copyright 2012 the original author or authors.
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

/**
 * 
 */
package org.broadleafcommerce.admin.client.presenter.catalog.product;

import org.broadleafcommerce.admin.client.datasource.catalog.category.MediaMapDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductOptionListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductOptionValueDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.pricelist.PriceListDataSourceFactory;
import org.broadleafcommerce.admin.client.view.catalog.product.ProductOptionDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.presenter.structure.MapStructurePresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.MapStructureEntityEditDialog;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

/**
 * @author Phillip Verheyden
 *
 */
public class ProductOptionPresenter extends DynamicEntityPresenter implements Instantiable {
    
  //  protected SubPresentable productOptionValuesPresenter;
    protected SubPresentable productOptionValuePresenter;
    protected MapStructurePresenter priceListPresenter;
    protected SubPresentable translationsPresenter;
    @Override
    protected void changeSelection(final Record selectedRecord) {
        AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();
     //   productOptionValuesPresenter.load(selectedRecord, dataSource);
        productOptionValuePresenter.load(selectedRecord, dataSource);
    }
    
    @Override
    public void bind() {
        super.bind();
     //   productOptionValuesPresenter.bind();
        productOptionValuePresenter.bind();
        translationsPresenter.bind();
        getDisplay().getProductOptionValueDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                if (event.getState()) {
                    translationsPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("productOptionValueDS"), null);
                    priceListPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("productOptionValueDS"), null);
              }
            }
        });
    }
    
    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionDS", new ProductOptionListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{});
            }
        }));
//        
//        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionValuesDS", new ProductOptionValueDataSourceFactory(), new AsyncCallbackAdapter() {
//            @Override
//            public void onSetupSuccess(DataSource result) {
//                productOptionValuesPresenter = new CreateBasedListStructurePresenter(getDisplay().getProductOptionValuesDisplay(), BLCMain.getMessageManager().getString("newProductOptionValue"));
//                productOptionValuesPresenter.setDataSource((ListGridDataSource)result, new String[]{}, new Boolean[]{});
//            }
//        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionPriceListDS", new PriceListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
public void onSetupSuccess(DataSource top) {
               
            
            }
    }));
          getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionPriceListMapDS", new ProductOptionPriceListMapDataSourceFactory(this), null, null, new AsyncCallbackAdapter() {
              

          @Override
  public void onSetupSuccess(DataSource result) {
                      priceListPresenter = new MapStructurePresenter(getDisplay().getPriceAdjustmentDisplay(), getMediaEntityView(), BLCMain.getMessageManager().getString("newMediaTitle"));
                      priceListPresenter.setDataSource((ListGridDataSource) result, new String[]{}, new Boolean[]{});
                      
          }
            
      }));

          getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionValueTranslationMapDS", new ProductOptionTranslationMapDataSourceFactory(this), new AsyncCallbackAdapter() {
              @Override
              public void onSetupSuccess(DataSource result) {
                  translationsPresenter = new MapStructurePresenter(getDisplay().getTranslationsDisplay(), getMediaEntityView(), BLCMain.getMessageManager().getString("newMediaTitle"));
                  translationsPresenter.setDataSource((ListGridDataSource) result, new String[]{}, new Boolean[]{});
              }
          }));
        
        
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionValueDS", new ProductOptionValueDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                productOptionValuePresenter = new ProductOptionValueSubPresenter(getDisplay().getProductOptionValueDisplay(), "newProductOptionValue", null, false, true, false);
                productOptionValuePresenter.setDataSource((ListGridDataSource) result, new String[]{}, new Boolean[]{});
            }
        }));
 
    }
    @Override
    public void postSetup(Canvas container) {
        gridHelper.traverseTreeAndAddHandlers(display.getListDisplay().getGrid());
        gridHelper.addSubPresentableHandlers(display.getListDisplay().getGrid(),productOptionValuePresenter ,translationsPresenter,priceListPresenter);
        
        super.postSetup(container);
    }
    @Override
    public ProductOptionDisplay getDisplay() {
        return (ProductOptionDisplay)display;
    }
    private MapStructureEntityEditDialog mapEntityAdd;

    protected MapStructureEntityEditDialog getMediaEntityView() {
        if (mapEntityAdd == null) {
            mapEntityAdd = new MapStructureEntityEditDialog(MediaMapDataSourceFactory.MAPSTRUCTURE,getPresenterSequenceSetupManager().getDataSource("productOptionPriceListDS"),"friendlyName","priceKey");
                 mapEntityAdd.setShowMedia(true);
                 mapEntityAdd.setMediaField("url");
       };
    return mapEntityAdd;
     }
    private MapStructureEntityEditDialog mapEntityAdd2;

    protected MapStructureEntityEditDialog getMediaEntityView2() {
        if (mapEntityAdd2== null) {
            mapEntityAdd2 = new MapStructureEntityEditDialog(MediaMapDataSourceFactory.MAPSTRUCTURE,getPresenterSequenceSetupManager().getDataSource("productOptionPriceListDS"),"friendlyName","priceKey");
                 mapEntityAdd2.setShowMedia(true);
                 mapEntityAdd2.setMediaField("url");
       };
    return mapEntityAdd2;
     }
}
