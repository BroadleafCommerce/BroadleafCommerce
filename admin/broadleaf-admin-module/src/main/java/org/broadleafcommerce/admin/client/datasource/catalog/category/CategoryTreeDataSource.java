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

package org.broadleafcommerce.admin.client.datasource.catalog.category;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryTreeDataSource extends TreeGridDataSource {

	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 * @param rootId
	 * @param rootName
	 */
	public CategoryTreeDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, String rootId, String rootName) {
		super(name, persistencePerspective, service, modules, rootId, rootName);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		CriteriaTransferObject criteriaTransferObject = getCompatibleModule(OperationType.BASIC).getCto(request);
        String[] filterValues = criteriaTransferObject.get(CategoryTreeDataSourceFactory.foreignKeyName).getFilterValues();
		String parentCategoryId = filterValues.length>0?filterValues[0]:null;
		boolean hasChildren = true;
		if (parentCategoryId != null) {
			TreeNode parentNode = ((TreeGrid) associatedGrid).getTree().findById(parentCategoryId);
			if (parentNode != null) {
				hasChildren = Boolean.parseBoolean(parentNode.getAttribute(CategoryTreeDataSourceFactory.hasChildrenProperty));
			}
		}
		/*
		 * Allows us to do a quick fetch that does not go back to the server. This is for
		 * cosmetic purposes in the tree view. By quickly retrieving a zero record list for
		 * tree nodes, we can immediately update the tree display for those nodes and cause the removal
		 * of their expand GUI element. 
		 */
        if (hasChildren && parentCategoryId != null) {
        	super.executeFetch(requestId, request, response);
        } else if (parentCategoryId == null) {
        	TreeNode node = new TreeNode();
        	node.setAttribute(getPrimaryKeyFieldName(), getRootId());
        	node.setAttribute("name", getRootName());
        	node.setAttribute(CategoryTreeDataSourceFactory.hasChildrenProperty, String.valueOf(hasChildren));
        	node.setAttribute("_type", new String[] {EntityImplementations.CATEGORY});
        	TreeNode[] recordList = new TreeNode[]{node};
        	response.setData(recordList);
        	response.setTotalRows(0);
        	processResponse(requestId, response);
        } else {
        	TreeNode[] recordList = new TreeNode[]{};
			response.setData(recordList);
			response.setTotalRows(0);
			processResponse(requestId, response);
        }
	}
	
	@Override
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		BLCMain.NON_MODAL_PROGRESS.startProgress();
		setLinkedValue(getPrimaryKeyValue(((ListGrid) getAssociatedGrid()).getSelectedRecord()));
		JavaScriptObject data = request.getData();
        final TreeNode newRecord = new TreeNode(data);
        final DataSourceModule entityModule = getCompatibleModule(OperationType.BASIC);
    	Entity entity = entityModule.buildEntity(newRecord, request);
    	//Add the new category entity
		service.add(new PersistencePackage(entityModule.getCeilingEntityFullyQualifiedClassname(), entity, persistencePerspective, new String[]{"addNewCategory"}, BLCMain.csrfToken), new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response, this) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
                if (processResult(result, requestId, response, CategoryTreeDataSource.this)) {
                    TreeNode record = (TreeNode) entityModule.buildRecord(result, true);
                    TreeNode[] recordList = new TreeNode[]{record};
                    response.setData(recordList);
                    processResponse(requestId, response);
                    setLinkedValue(null);
                }
			}
		});
	}

}
