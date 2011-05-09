package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.dynamic.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
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
		persistencePerspective.getOperationTypes().setMiscType(OperationType.ENTITY);
		CriteriaTransferObject criteriaTransferObject = getCto(request);
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
	protected void executeUpdate(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        final TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
		service.update(entity, persistencePerspective, null, new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, this) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				/*
				 * Since we've hacked the tree to be able to display duplicate entries, we must iterate
				 * through the currently loaded records to see if there are any other instances of our
				 * entity and update them as well.
				 */
				String startingId = stripDuplicateAllowSpecialCharacters(record.getAttribute("id"));
				RecordList resultSet = CategoryTreeDataSource.this.getAssociatedGrid().getRecordList();
				if (resultSet != null) {
					Record[] myRecords = resultSet.toArray();
					int count = 1;
					for (Record myRecord : myRecords) {
						String myId = stripDuplicateAllowSpecialCharacters(myRecord.getAttribute("id"));
						if (startingId.equals(myId) && !record.getAttribute("id").equals(myId)) {
							updateRecord(result, (TreeNode) myRecord, false);
							CategoryTreeDataSource.this.getAssociatedGrid().refreshRow(count);
						}
						count++;
					}
				}
				TreeNode myRecord = (TreeNode) updateRecord(result, (Record) record, false);
				TreeNode[] recordList = new TreeNode[]{myRecord};
				response.setData(recordList);
				processResponse(requestId, response);
			}
		});
	}
	
	@Override
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		persistencePerspective.getOperationTypes().setMiscType(OperationType.ENTITY);
		setLinkedValue(getAssociatedGrid().getSelectedRecord().getAttribute("id"));
		JavaScriptObject data = request.getData();
        final TreeNode newRecord = new TreeNode(data);
        persistencePerspective.getOperationTypes().setAddType(OperationType.ENTITY);
    	Entity entity = buildEntity(newRecord);
    	//Add the new category entity
		service.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, null, new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response, this) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				TreeNode record = (TreeNode) buildRecord(result);
				TreeNode[] recordList = new TreeNode[]{record};
				response.setData(recordList);
				
				persistencePerspective.getOperationTypes().setAddType(OperationType.JOINTABLE);
				Entity entity = buildEntity(record);
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
