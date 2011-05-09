package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class CategoryTreeDataSource extends TreeGridDataSource {
	
	public static final String hasChildrenProperty = "hasAllChildCategories";
	public static final String foreignKeyName = "allParentCategories";
	
	protected String updatedParentId;

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param service
	 * @param foreignFields
	 * @param removeType
	 * @param additionalNonPersistentProperties
	 */
	public CategoryTreeDataSource(String ceilingEntityFullyQualifiedClassname, String name, RemoveType removeType, DynamicEntityServiceAsync service) {
		super(ceilingEntityFullyQualifiedClassname, new ForeignKey[]{new ForeignKey(foreignKeyName, EntityImplementations.CATEGORY, null)}, name, service, removeType, new String[] {hasChildrenProperty});
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		final CriteriaTransferObject criteriaTransferObject = getCto(request);
		String parentCategoryId = criteriaTransferObject.get(foreignKeyName).getFilterValues()[0];
		boolean hasChildren = true;
		TreeNode parentNode = ((TreeGrid) associatedGrid).getTree().findById(parentCategoryId);
		if (parentNode != null) {
			hasChildren = Boolean.parseBoolean(parentNode.getAttribute(hasChildrenProperty));
		}
		/*
		 * Allows us to do a quick fetch that does not go back to the server. This is for
		 * cosmetic purposes in the tree view. By quickly retrieving a zero record list for
		 * tree nodes, we can immediately update the tree display for those nodes and cause the removal
		 * of their expand GUI element. 
		 */
        if (hasChildren) {
        	Main.NON_MODAL_PROGRESS.startProgress();
			service.fetch(ceilingEntityFullyQualifiedClassname, foreignFields, criteriaTransferObject, additionalNonPersistentProperties, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response) {
				public void onSuccess(DynamicResultSet result) {
					super.onSuccess(result);
					TreeNode[] recordList = buildRecords(result);
					response.setData(recordList);
					response.setTotalRows(result.getTotalRecords());
					processResponse(requestId, response);
				}
			});
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
        String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttribute("type");
            	entity.setType(type);
            }
        }
		service.update(entity, foreignFields, additionalNonPersistentProperties, new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				/*
				 * Since we've hacked the tree to be able to display duplicate entries, we must iterate
				 * through the currently loaded records to see if there are any other instances of our
				 * entity and update them as well.
				 */
				String startingId = stripDuplicateAllowSpecialCharacters(record.getAttribute("id"));
				Record[] myRecords = CategoryTreeDataSource.this.getAssociatedGrid().getResultSet().toArray();
				int count = 1;
				for (Record myRecord : myRecords) {
					String myId = stripDuplicateAllowSpecialCharacters(myRecord.getAttribute("id"));
					if (startingId.equals(myId)) {
						updateRecord(result, (TreeNode) myRecord, false);
						CategoryTreeDataSource.this.getAssociatedGrid().refreshRow(count);
					}
					count++;
				}
				TreeNode myRecord = updateRecord(result, record, false);
				TreeNode[] recordList = new TreeNode[]{myRecord};
				response.setData(recordList);
				processResponse(requestId, response);
			}
		});
	}

	
}
