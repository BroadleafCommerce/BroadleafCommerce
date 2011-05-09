package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dynamic.entity.service")
public interface DynamicEntityService extends RemoteService {
    
	DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, String[] optionalFields) throws ServiceException;
	
    DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, String[] optionalFields) throws ServiceException;
    
    DynamicResultSet create(String targetEntityFullyQualifiedClassname, String[] optionalFields) throws ServiceException;
    
    Entity add(Entity entity, String[] optionalFields) throws ServiceException;
    
    Entity update(Entity entity, String[] optionalFields) throws ServiceException;
    
    void remove(Entity entity) throws ServiceException;
    
}
