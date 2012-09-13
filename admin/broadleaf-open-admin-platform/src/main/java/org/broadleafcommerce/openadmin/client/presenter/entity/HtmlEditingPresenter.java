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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import java.util.HashMap;

import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelected;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.CeilingEntities;
import org.broadleafcommerce.openadmin.client.datasource.StaticAssetsTileGridDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.AssetSearchDialog;

import com.google.gwt.user.client.Command;
import com.smartgwt.client.data.DataSource;

/**
 * 
 */
public class HtmlEditingPresenter extends DynamicEntityPresenter {

    private AssetSearchDialog assetSearchDialogView;
    private static HtmlEditingPresenter instance = new HtmlEditingPresenter();
    private DataSource staticAssetsDataSouce=null;
    public String getTemplatePath() {
        return null;
    }

    private  HtmlEditingPresenter() {
       setup();
    }

    public void displayAssetSearchDialog(final RichTextToolbar item) {
        HashMap<String, Object> initialValues = new HashMap<String, Object>(10);
        initialValues.put("operation", "add");
        initialValues.put("customCriteria", "assetListUi");
        initialValues.put("ceilingEntityFullyQualifiedClassname",
                CeilingEntities.STATICASSETS);
        // initialValues.put("_type", new String[]{((DynamicEntityDataSource)
        // display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
        initialValues.put("_type", CeilingEntities.STATICASSETS);
        initialValues.put("csrfToken", BLCMain.csrfToken);
       // compileDefaultValuesFromCurrentFilter(initialValues);
        getAssetSearchDialogView().setInitialValues(initialValues);
        getAssetSearchDialogView().search("Asset Search",
                new TileGridItemSelectedHandler() {
                    @Override
                    public void onSearchItemSelected(TileGridItemSelected event) {

                        String staticAssetFullUrl = BLCMain.assetServerUrlPrefix
                                + event.getRecord().getAttribute("fullUrl");
                        String name = event.getRecord().getAttribute("name");
                        String fileExtension = event.getRecord().getAttribute(
                                "fileExtension");
                        String richContent;

                        if (fileExtension.equals("gif")
                                || fileExtension.equals("jpg")
                                || fileExtension.equals("png")) {
                            richContent = "<img title='" + name + "' src='"
                                    + staticAssetFullUrl + "' alt='" + name
                                    + "'/>";
                        } else {
                            richContent = "<a href='" + staticAssetFullUrl
                                    + "'>" + name + "</a>";
                        }
                        LogFactory.getLog(this.getClass())
                                .debug("inserting from dialog...."
                                        + fileExtension + " " + name + " "
                                        + staticAssetFullUrl);
                        item.insertAsset(fileExtension, name,
                                staticAssetFullUrl);

                    }
                });
    }

    protected String getAdminContext() {
        return BLCMain.adminContext;
    }

    protected String getPreviewUrlPrefix() {
        return BLCMain.storeFrontWebAppPrefix;
    }

    @Override
    public void setup() {
      if(staticAssetsDataSouce==null) {
            StaticAssetsTileGridDataSourceFactory factory=new StaticAssetsTileGridDataSourceFactory();
            factory.createDataSource("staticAssetTreeDS", null,null, new  AsyncCallbackAdapter() {
                
                @Override
                public void onSetupSuccess(DataSource dataSource) {
                    TileGridDataSource staticAssetTreeDS = (TileGridDataSource) dataSource;
                    setAssetSearchDialogView(new AssetSearchDialog(
                            staticAssetTreeDS));
                    staticAssetsDataSouce=dataSource;
                }
            });
      }
    }

    @Override
    public void bind() {
        super.bind();

    }

    /*
     * Add a handler to the HTMLTextItem's "add BLC asset" button so that if
     * clicked, we show them the asset search dialog
     */
    public void addListenerToFormItem(final RichTextToolbar formItem) {

        (formItem).addAssetHandler(new Command() {
            @Override
            public void execute() {
                displayAssetSearchDialog(formItem);
            };
        });

    }

    protected AssetSearchDialog getAssetSearchDialogView() {
        if (assetSearchDialogView == null) {
            setup();
        }
        return assetSearchDialogView;
    }

    protected void setAssetSearchDialogView(
            AssetSearchDialog assetSearchDialogView) {
        this.assetSearchDialogView = assetSearchDialogView;
    }

    public static HtmlEditingPresenter getInstance() {
        return instance;
    }

}
