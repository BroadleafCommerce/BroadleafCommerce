package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous version of {@link EntityService}.
 */
public interface DynamicEntityServiceAsync {
    
	void inspect(String ceilingEntityFullyQualifiedClassname, String[] optionalFields, AsyncCallback<DynamicResultSet> cb);
	
    void fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, String[] optionalFields, AsyncCallback<DynamicResultSet> cb);
    
    void create(String targetEntityFullyQualifiedClassname, String[] optionalFields, AsyncCallback<DynamicResultSet> cb);
    
    void add(Entity entity, String[] optionalFields, AsyncCallback<Entity> cb);
    
    void update(Entity entity, String[] optionalFields, AsyncCallback<Entity> cb);
    
    void remove(Entity entity, AsyncCallback<Void> cb);
    
}
