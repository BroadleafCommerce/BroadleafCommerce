package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dynamic.entity.service")
public interface DynamicEntityService extends RemoteService {
    
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective) throws ServiceException;
	
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
    
	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
    
    public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
    
    public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
    
}
