package org.broadleafcommerce.gwt.client.datasource.dynamic.operation;

import org.broadleafcommerce.gwt.client.service.AbstractCallback;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;

public abstract class EntityServiceAsyncCallback<T> extends AbstractCallback<T> {
    
    private final EntityOperationType opType;
    private final String requestId;
    private final DSRequest request;
    private final DSResponse response;
    private final DataSource dataSource;
    
    public EntityServiceAsyncCallback(EntityOperationType opType, String requestId,
            DSRequest request, DSResponse response, DataSource dataSource) {
        this.opType = opType;
        this.requestId = requestId;
        this.request = request;
        this.response = response;
        this.dataSource = dataSource;
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
	
	protected void onError(EntityOperationType opType, String requestId,
            DSRequest request, DSResponse response, Throwable caught) {
        response.setStatus(RPCResponse.STATUS_FAILURE);
        dataSource.processResponse(requestId, response);
        
        // show a dialog with error message
        SC.warn("<b>" + opType.name()
                + "</b><br/><br/>Error while processing RPC request:<br/><br/>"
                + caught.getMessage(), null);
    }
}
