package org.broadleafcommerce.gwt.client.datasource;

import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.gwt.client.service.AbstractCallback;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.CriteriaPolicy;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.util.SC;


public abstract class EntityServiceDataSource extends GwtRpcDataSource {
    
    public EntityServiceDataSource() {
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
    protected CriteriaTransferObject getCto(DSRequest request) {
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
        
        Criteria criteria = request.getCriteria();
        String jsObj = JSON.encode(criteria.getJsObj());
        // filter
        Map<String, String> filterData = criteria.getValues();
        Set<String> filterFieldNames = filterData.keySet();
        for (String fieldName : filterFieldNames) {
            FilterAndSortCriteria filterCriteria = cto.get(fieldName);
            filterCriteria.setFilterValue(filterData.get(fieldName));
        }
        
        return cto;
    }
    
    public static enum EntityOperationType {
        FETCH, ADD, UPDATE, REMOVE
    }
    
    protected void onError(EntityOperationType opType, String requestId,
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
    public abstract class EntityServiceAsyncCallback<T> extends AbstractCallback<T> {
        
        private final EntityOperationType opType;
        private final String requestId;
        private final DSRequest request;
        private final DSResponse response;
        
        public EntityServiceAsyncCallback(EntityOperationType opType, String requestId,
                DSRequest request, DSResponse response) {
            this.opType = opType;
            this.requestId = requestId;
            this.request = request;
            this.response = response;
        }
        
        @Override
		protected void onSecurityException(ApplicationSecurityException exception) {
        	super.onSecurityException(exception);
			onError(opType, requestId, request, response, exception);
		}

		@Override
		protected void onOtherException(Throwable exception) {
			super.onOtherException(exception);
			onError(opType, requestId, request, response, exception);
		}
    }
    
}
