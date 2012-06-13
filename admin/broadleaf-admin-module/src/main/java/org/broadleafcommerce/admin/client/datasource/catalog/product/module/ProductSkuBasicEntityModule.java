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

package org.broadleafcommerce.admin.client.datasource.catalog.product.module;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class ProductSkuBasicEntityModule extends BasicClientEntityModule {

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param persistencePerspective
	 * @param service
	 */
	public ProductSkuBasicEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
	}

	@Override
	public void executeAdd(final String requestId, DSRequest request, final DSResponse response, String[] customCriteria, final AsyncCallback<DataSource> cb) {
		BLCMain.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record, request);
        
        List<Property> newPropList = new ArrayList<Property>(5 + entity.getProperties().length);
        {
	        Property myProp = entity.findProperty("name");
	        if (myProp != null) {
	        	Property temp = new Property();
	        	temp.setName("sku.name");
	        	temp.setValue(myProp.getValue());
	        	newPropList.add(temp);
	        }
        }
        {
	        Property myProp = entity.findProperty("activeStartDate");
	        if (myProp != null) {
	        	Property temp = new Property();
	        	temp.setName("sku.activeStartDate");
	        	temp.setValue(myProp.getValue());
	        	newPropList.add(temp);
	        }
        }
        {
	        Property myProp = entity.findProperty("activeEndDate");
	        if (myProp != null) {
	        	Property temp = new Property();
	        	temp.setName("sku.activeEndDate");
	        	temp.setValue(myProp.getValue());
	        	newPropList.add(temp);
	        }
        }
        {
	        Property myProp = entity.findProperty("description");
	        if (myProp != null) {
	        	Property temp = new Property();
	        	temp.setName("sku.description");
	        	temp.setValue(myProp.getValue());
	        	newPropList.add(temp);
	        }
        }
        {
	        Property myProp = entity.findProperty("longDescription");
	        if (myProp != null) {
	        	Property temp = new Property();
	        	temp.setName("sku.longDescription");
	        	temp.setValue(myProp.getValue());
	        	newPropList.add(temp);
	        }
        }
        newPropList.addAll(Arrays.asList(entity.getProperties()));
        Property[] newProps = new Property[newPropList.size()];
        newProps = newPropList.toArray(newProps);
        entity.setProperties(newProps);
        
        service.add(new PersistencePackage(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria, BLCMain.csrfToken), new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
                if (processResult(result, requestId, response, dataSource)) {
                    TreeNode record = (TreeNode) buildRecord(result, false);
                    TreeNode[] recordList = new TreeNode[]{record};
                    response.setData(recordList);
                    if (cb != null) {
                        cb.onSuccess(dataSource);
                    }

                    dataSource.processResponse(requestId, response);
                }
			}
		});
	}

	@Override
	public void buildFields(final String[] customCriteria, final Boolean overrideFieldSort, final AsyncCallback<DataSource> cb) {
        AppServices.DYNAMIC_ENTITY.inspect(new PersistencePackage(ceilingEntityFullyQualifiedClassname, null, persistencePerspective, customCriteria, BLCMain.csrfToken), new AbstractCallback<DynamicResultSet>() {
            public void onSuccess(DynamicResultSet result) {
                super.onSuccess(result);
                ClassMetadata metadata = result.getClassMetaData();
                filterProperties(metadata, new MergedPropertyType[]{MergedPropertyType.PRIMARY, MergedPropertyType.JOINSTRUCTURE}, overrideFieldSort);

                //Add a hidden field to store the polymorphic type for this entity
                DataSourceField typeField = new DataSourceTextField("_type");
                typeField.setCanEdit(false);
                typeField.setHidden(true);
                typeField.setAttribute("permanentlyHidden", true);
                dataSource.addField(typeField);

                dataSource.getField("sku.name").setHidden(true);
                dataSource.getField("sku.name").setAttribute("permanentlyHidden", true);
                dataSource.getField("sku.activeStartDate").setHidden(true);
                dataSource.getField("sku.activeStartDate").setAttribute("permanentlyHidden", true);
                dataSource.getField("sku.activeEndDate").setHidden(true);
                dataSource.getField("sku.activeEndDate").setAttribute("permanentlyHidden", true);
                dataSource.getField("sku.description").setHidden(true);
                dataSource.getField("sku.description").setAttribute("permanentlyHidden", true);
                dataSource.getField("sku.longDescription").setHidden(true);
                dataSource.getField("sku.longDescription").setAttribute("permanentlyHidden", true);
                dataSource.setPolymorphicEntityTree(metadata.getPolymorphicEntities());
                dataSource.setDefaultNewEntityFullyQualifiedClassname(dataSource.getPolymorphicEntities().keySet().iterator().next());

                cb.onSuccess(dataSource);
            }

        });
	}

	
}
