package org.broadleafcommerce.gwt.client.datasource.catalog.category;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class CategoryTreeDataSource extends TreeGridDataSource {

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 * @param rootId
	 * @param rootName
	 */
	public CategoryTreeDataSource(String ceilingEntityFullyQualifiedClassname, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, String rootId, String rootName) {
		super(ceilingEntityFullyQualifiedClassname, name, persistencePerspective, service, modules, rootId, rootName);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		CriteriaTransferObject criteriaTransferObject = getCompatibleModule(OperationType.ENTITY).getCto(request);
		String parentCategoryId = criteriaTransferObject.get(CategoryTreeDataSourceFactory.foreignKeyName).getFilterValues()[0];
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
        	node.setAttribute("id", getRootId());
        	node.setAttribute("name", getRootName());
        	node.setAttribute(CategoryTreeDataSourceFactory.hasChildrenProperty, String.valueOf(hasChildren));
        	node.setAttribute("type", new String[] {EntityImplementations.CATEGORY});
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
		Main.NON_MODAL_PROGRESS.startProgress();
		setLinkedValue(getAssociatedGrid().getSelectedRecord().getAttribute("id"));
		JavaScriptObject data = request.getData();
        final TreeNode newRecord = new TreeNode(data);
        persistencePerspective.getOperationTypes().setAddType(OperationType.ENTITY);
    	Entity entity = getCompatibleModule(OperationType.ENTITY).buildEntity(newRecord);
    	//Add the new category entity
		service.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, null, new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response, this) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				TreeNode record = (TreeNode) getCompatibleModule(OperationType.ENTITY).buildRecord(result, true);
				TreeNode[] recordList = new TreeNode[]{record};
				response.setData(recordList);
				
				persistencePerspective.getOperationTypes().setAddType(OperationType.JOINSTRUCTURE);
				Entity entity = getCompatibleModule(OperationType.JOINSTRUCTURE).buildEntity(record);
				//Add the join table entry for the new category as well
	        	service.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, null, new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, "temp" + requestId, request, response, CategoryTreeDataSource.this) {
					public void onSuccess(Entity result) {
						super.onSuccess(result);
						processResponse(requestId, response);
					}
				});
			}
		});
	}

}
