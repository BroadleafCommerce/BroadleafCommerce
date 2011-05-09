package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous version of {@link EntityService}.
 */
public interface DynamicEntityServiceAsync {
    
	void inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, String[] metadataOverrideKeys, FieldMetadata[] metadataOverrideValues, AsyncCallback<DynamicResultSet> cb);
	
	void fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria, AsyncCallback<DynamicResultSet> cb);
    
    void add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, AsyncCallback<Entity> cb);
    
    void update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, AsyncCallback<Entity> cb);
    
	void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, AsyncCallback<Void> cb);
    
}
