package org.broadleafcommerce.gwt.client.datasource;

import org.broadleafcommerce.gwt.client.service.AbstractCallback;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.CriteriaPolicy;
import com.smartgwt.client.util.SC;


public abstract class EntityServiceDataSource extends GwtRpcDataSource {
	
	public EntityServiceDataSource(String name) {
		super(name);
		setCriteriaPolicy(CriteriaPolicy.DROPONCHANGE);
		setCacheMaxAge(0);
	}
    
    
    
    
    
}
