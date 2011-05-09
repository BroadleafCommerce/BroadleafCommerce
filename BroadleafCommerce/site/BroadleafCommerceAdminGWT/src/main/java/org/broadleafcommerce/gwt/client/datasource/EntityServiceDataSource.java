package org.broadleafcommerce.gwt.client.datasource;

import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.gwt.client.service.AbstractCallback;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
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
	
	protected ForeignKey currentForeignKey;
	
	public EntityServiceDataSource(String name) {
		super(name);
		setCriteriaPolicy(CriteriaPolicy.DROPONCHANGE);
		setCacheMaxAge(0);
	}
	
	public ForeignKey getCurrentForeignKey() {
		return currentForeignKey;
	}

	public void setCurrentForeignKey(ForeignKey currentForeignKey) {
		this.currentForeignKey = currentForeignKey;
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
        @SuppressWarnings("rawtypes")
		Map filterData = criteria.getValues();
        Set<String> filterFieldNames = filterData.keySet();
        for (String fieldName : filterFieldNames) {
        	if (!fieldName.equals("_constructor") && !fieldName.equals("operator")) {
        		if (!fieldName.equals("criteria")) {
        			FilterAndSortCriteria filterCriteria = cto.get(fieldName);
        			filterCriteria.setFilterValue(stripDuplicateAllowSpecialCharacters((String) filterData.get(fieldName)));
        		} else {
        			JSONValue value = JSONParser.parse(jsObj);
        			JSONObject criteriaObj = value.isObject();
        			JSONArray criteriaArray = criteriaObj.get("criteria").isArray();
        			buildCriteria(criteriaArray, cto);
        		}
        	}
        }
        if (getCurrentForeignKey() != null) {
        	FilterAndSortCriteria filterCriteria = cto.get(getCurrentForeignKey().getManyToField());
			filterCriteria.setFilterValue(getCurrentForeignKey().getCurrentValue());
        }
        
        return cto;
    }
    
    protected void buildCriteria(JSONArray criteriaArray, CriteriaTransferObject cto) {
    	if (criteriaArray != null) {
			for (int i=0; i<=criteriaArray.size()-1; i++) {
				JSONObject itemObj = criteriaArray.get(i).isObject();
				if (itemObj != null) {
					JSONValue val = itemObj.get("fieldName");
					if (val == null) {
						JSONArray array = itemObj.get("criteria").isArray();
						buildCriteria(array, cto);
					} else {
						FilterAndSortCriteria filterCriteria = cto.get(val.isString().stringValue());
						String[] items = filterCriteria.getFilterValues();
						String[] newItems = new String[items.length + 1];
						int j = 0;
						for (String item : items) {
							newItems[j] = item;
							j++;
						}
						JSONValue value = itemObj.get("value");
						JSONString strVal = value.isString();
						if (strVal != null) {
							newItems[j] = strVal.stringValue();
						} else {
							newItems[j] = value.isObject().get("value").isString().stringValue();
							/*
							 * TODO need to add special parsing for relative dates. Convert this relative
							 * value to an actual date string.
							 */
						}
						
						filterCriteria.setFilterValues(newItems);
					}
				}
			}
		}
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
    
    public String stripDuplicateAllowSpecialCharacters(String string) {
    	if (string != null) {
			int index = string.indexOf("_");
	        if (index >= 0) {
	        	string = string.substring(0,index);
	        }
    	}
        return string;
	}
    
}
