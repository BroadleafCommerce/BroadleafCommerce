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
package org.broadleafcommerce.openadmin.client.datasource.dynamic.operation;

import org.broadleafcommerce.openadmin.client.service.AbstractCallback;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author jfischer
 *
 * @param <T>
 */
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
