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
package org.broadleafcommerce.admin.client.datasource.catalog.product.module;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormHiddenEnum;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Phillip Verheyden
 *
 */
public class SkuBasicClientEntityModule extends BasicClientEntityModule {

    /**
     *
     * @param ceilingEntityFullyQualifiedClassname
     * @param persistencePerspective
     * @param service
     */
    public SkuBasicClientEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
    }

    /**
     * On a server-side inspect, the fields for all of the ProductOptions are returned with no bearing on options a Product has
     * nor on what values the Skus have for that Product (because nothing has been selected at the time of inspect). This information
     * is only available to us when the Skus are actually fetched. The fetch will return the correct ProductOptions in the list
     * of Properties in the Entities that were returned so that we can show/hide the ProductOption fields accordingly
     *
     * @param requestId
     * @param request
     * @param response
     * @param customCriteria
     * @param cb
     */
    @Override
    public void executeFetch(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
        if (request.getCriteria() != null && request.getCriteria().getAttribute("blc.fetch.from.cache") != null) {
            super.executeFetch(requestId, request, response, customCriteria, cb);
        } else {
            /*** BEGIN COPY FROM BasicClientEntityModule ***/
            BLCMain.NON_MODAL_PROGRESS.startProgress();
            CriteriaTransferObject cto = getCto(request);
            service.fetch(new PersistencePackage(ceilingEntityFullyQualifiedClassname, fetchTypeFullyQualifiedClassname, null, persistencePerspective, customCriteria, BLCMain.csrfToken), cto, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, dataSource) {
                @Override
                public void onSuccess(DynamicResultSet result) {
                    super.onSuccess(result);
                    TreeNode[] recordList = buildRecords(result, null);
                    response.setData(recordList);
                    response.setTotalRows(result.getTotalRecords());
                    /*** END COPY FROM BasicClientEntityModule ***/
                    
                    //only execute the below code if there are actual results returned
                    if (result.getTotalRecords() > 0) {
                        // First, hide all of the productOption fields becauase they may not be applicable to this particular 
                        // product
                        for (DataSourceField field : dataSource.getFields()) {
                            if (field.getName().startsWith("productOption")) {
                                field.setAttribute("formHidden", FormHiddenEnum.HIDDEN);
                            }
                        }

                        // Hide the consolidated product options field. If someone is using this entity module, then the
                        // intention would be to utilize each product option as a grid field
                        dataSource.getField("consolidatedProductOptions").setAttribute("permanentlyHidden", true);

                        //In order to make the form display show up properly for creating a new single Sku, make all the product
                        //options for the Product visible on the form
                        if (result.getClassMetaData().getProperties() != null) {
                            for (Property property : result.getClassMetaData().getProperties()) {
                                DataSourceField field = ((ListGridDataSource) dataSource).getField(property.getName());
                                field.setAttribute("formHidden", FormHiddenEnum.VISIBLE);
                            }
                        }

                        //Build up a list of the product options that are relevant for this list of Skus
                        List<String> productOptionFields = new ArrayList<String>();
                        for (Entity entity : result.getRecords()) {
                            for (Property property : entity.getProperties()) {
                                if (property.getName().startsWith("productOption")) {
                                    if (!productOptionFields.contains(property.getName())) {
                                        productOptionFields.add(property.getName());
                                    }
                                }
                            }
                        }
                        //Now hide/show all the product option fields based on the current list of Skus
                        for (DataSourceField field : dataSource.getFields()) {
                            if (field.getName().startsWith("productOption")) {
                                if (productOptionFields.contains(field.getName())) {
                                    ((ListGrid) ((ListGridDataSource) dataSource).getAssociatedGrid()).showField(field.getName());
                                } else {
                                    ((ListGrid) ((ListGridDataSource) dataSource).getAssociatedGrid()).hideField(field.getName());
                                }
                            }
                        }
                    }
                    
                    /*** BEGIN COPY FROM BasicClientEntityModule ***/
                    if (cb != null) {
                        cb.onSuccess(dataSource);
                    }
                    dataSource.processResponse(requestId, response);
                }

                @Override
                protected void onSecurityException(ApplicationSecurityException exception) {
                    super.onSecurityException(exception);
                    if (cb != null) {
                        cb.onFailure(exception);
                    }
                }

                @Override
                protected void onOtherException(Throwable exception) {
                    super.onOtherException(exception);
                    if (cb != null) {
                        cb.onFailure(exception);
                    }
                }

                @Override
                protected void onError(EntityOperationType opType, String requestId, DSRequest request, DSResponse response, Throwable caught) {
                    super.onError(opType, requestId, request, response, caught);
                    if (cb != null) {
                        cb.onFailure(caught);
                    }
                }
            });
            /*** END COPY FROM BasicClientEntityModule ***/
        }
    }
}
