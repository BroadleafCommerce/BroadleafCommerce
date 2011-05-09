package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.datasource.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous version of {@link EntityService}.
 */
public interface DynamicEntityServiceAsync {
    
	void inspect(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties, AsyncCallback<DynamicResultSet> cb);
	
    void fetch(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, CriteriaTransferObject cto, String[] additionalNonPersistentProperties, AsyncCallback<DynamicResultSet> cb);
    
    void fetch(String ceilingEntityFullyQualifiedClassname, JoinTable associationKey, CriteriaTransferObject cto, String[] additionalNonPersistentProperties, AsyncCallback<DynamicResultSet> cb);
    
    void create(String targetEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties, AsyncCallback<DynamicResultSet> cb);
    
    void add(Entity entity, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties, AsyncCallback<Entity> cb);
    
    void update(Entity entity, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties, AsyncCallback<Entity> cb);
    
    void remove(Entity entity, ForeignKey[] foreignFields, RemoveType removeType, String[] additionalNonPersistentProperties, AsyncCallback<Void> cb);
    
}
