package org.broadleafcommerce.gwt.client.datasource;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.gwt.client.service.GridService;
import org.broadleafcommerce.gwt.client.service.GridServiceAsync;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.client.SecuredAsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.CriteriaPolicy;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Generic GWT RPC data source that supports common
 * grid-like operations via {@link GridService}.
 */
public abstract class GridServiceDataSource<Entity extends Serializable, SERVICE extends GridServiceAsync<Entity>> extends GwtRpcDataSource {
    
    private final SERVICE service;
    
    public GridServiceDataSource(SERVICE service, DataSourceField... fields) {
        this.service = service;
        
        for (DataSourceField f : fields)
            addField(f);
        
        // record cache is dropped whenever grid criteria changes
        setCriteriaPolicy(CriteriaPolicy.DROPONCHANGE);
    }
    
    /**
     * Transforms the given <tt>request</tt> into
     * {@link CriteriaTransferObject} instance.
     * <p>
     * We are doing this because we can apply seamless
     * CTO-to-criteria conversions back on the server.
     */
    @SuppressWarnings("unchecked")
    private CriteriaTransferObject getCto(DSRequest request) {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        
        // paging
        if (request.getStartRow() != null) {
        	cto.setFirstResult(request.getStartRow());
        	if (request.getEndRow() != null) {
        		cto.setMaxResults(request.getEndRow() - request.getStartRow());
        	}
        }
        
        // sort
        SortSpecifier[] sortBy = request.getSortBy();
        if (sortBy != null && sortBy.length > 0) {
        	String sortPropertyId = sortBy[0].getField();
            boolean sortAscending = sortBy[0].getSortDirection().equals(SortDirection.ASCENDING);            
            FilterAndSortCriteria sortCriteria = cto.get(sortPropertyId);
            sortCriteria.setSortAscending(sortAscending);
        }
        
        // filter
        Map<String, String> filterData = request.getCriteria().getValues();
        Set<String> filterFieldNames = filterData.keySet();
        for (String fieldName : filterFieldNames) {
            FilterAndSortCriteria filterCriteria = cto.get(fieldName);
            filterCriteria.setFilterValue(filterData.get(fieldName));
        }
        
        return cto;
    }
    
    public static enum GridOperationType {
        FETCH, ADD, UPDATE, REMOVE
    }
    
    protected void onError(GridOperationType opType, String requestId,
            DSRequest request, DSResponse response, Throwable caught) {
        response.setStatus(RPCResponse.STATUS_FAILURE);
        processResponse(requestId, response);
        
        // show a dialog with error message
        SC.warn("<b>" + opType.name()
                + "</b><br/><br/>Error while processing RPC request:<br/><br/>"
                + caught.getMessage(), null);
    }
    
    /**
     * RPC callback adapter for common error handling.
     */
    private abstract class GridServiceAsyncCallback<T> extends SecuredAsyncCallback<T> {
        
        private final GridOperationType opType;
        private final String requestId;
        private final DSRequest request;
        private final DSResponse response;
        
        public GridServiceAsyncCallback(GridOperationType opType, String requestId,
                DSRequest request, DSResponse response) {
            this.opType = opType;
            this.requestId = requestId;
            this.request = request;
            this.response = response;
        }
        
        @Override
		protected void onSecurityException(ApplicationSecurityException exception) {
			SC.say("security exception");
		}

		@Override
		protected void onOtherException(Throwable exception) {
			SC.say("Other exception");
		}
    }
    
    @Override
    protected void executeFetch(final String requestId,
            final DSRequest request, final DSResponse response) {
        service.fetch(getCto(request), new GridServiceAsyncCallback<ResultSet<Entity>>(
                GridOperationType.FETCH, requestId, request, response) {
                    public void onSuccess(ResultSet<Entity> result) {
                        List<Entity> resultList = result.getResultList();
                        TreeNode[] recordList = new TreeNode[resultList.size()];
                        
                        for (int i = 0; i < recordList.length; i++) {
                        	TreeNode record = new TreeNode();
                            copyValues(resultList.get(i), record);
                            recordList[i] = record;
                        }
                        
                        response.setData(recordList);
                        response.setTotalRows(result.getTotalRecords());
                        //response.setTotalRows(resultList.size());
                        processResponse(requestId, response);
                    }
        });
    }
    
    @Override
    protected void executeAdd(final String requestId, final DSRequest request,
            final DSResponse response) {
        // retrieve record which should be added
        JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity dto = newEntityInstance();
        copyValues(record, dto);
        
        service.add(dto, new GridServiceAsyncCallback<Entity>(
                GridOperationType.ADD, requestId, request, response) {
                    public void onSuccess(Entity result) {
                    	TreeNode[] list = new TreeNode[1];
                    	TreeNode newRecord = new TreeNode();
                        copyValues(result, newRecord);
                        list[0] = newRecord;
                        
                        response.setData(list);
                        processResponse(requestId, response);
                    }
                });
    }
    
    @Override
    protected void executeUpdate(final String requestId,
            final DSRequest request, final DSResponse response) {
        // retrieve record which should be updated
        JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        
        // find grid
        ListGrid grid = (ListGrid) Canvas.getById(request.getComponentId());
        
        // get record with old and new values combined
        int index = grid.getRecordIndex(record);
        record = (TreeNode) grid.getEditedRecord(index);
        Entity entity = newEntityInstance();
        copyValues(record, entity);
        
        service.update(entity, new GridServiceAsyncCallback<Entity>(
                GridOperationType.UPDATE, requestId, request, response) {
                    public void onSuccess(Entity result) {
                        TreeNode[] list = new TreeNode[1];
                        TreeNode updRec = new TreeNode();
                        copyValues(result, updRec);
                        list[0] = updRec;
                        
                        response.setData(list);
                        processResponse(requestId, response);
                    }
                });
    }
    
    @Override
    protected void executeRemove(final String requestId,
            final DSRequest request, final DSResponse response) {
        // retrieve record which should be removed
        JavaScriptObject data = request.getData();
        final TreeNode record = new TreeNode(data);
        Entity entity = newEntityInstance();
        copyValues(record, entity);
        
        service.remove(entity, new GridServiceAsyncCallback<Void>(
                GridOperationType.REMOVE, requestId, request, response) {
                    public void onSuccess(Void result) {
                        ListGridRecord[] list = new ListGridRecord[1];
                        // we do not receive removed record from server,
                        // so we return record from the request
                        list[0] = record;
                        
                        response.setData(list);
                        processResponse(requestId, response);
                    }
                });
    }
    
    protected abstract Entity newEntityInstance();
    
    protected abstract void copyValues(TreeNode from, Entity to);
    
    protected abstract void copyValues(Entity from, TreeNode to);
    
}
