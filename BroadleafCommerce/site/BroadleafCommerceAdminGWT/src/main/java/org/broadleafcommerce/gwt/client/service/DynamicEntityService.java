package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.datasource.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dynamic.entity.service")
public interface DynamicEntityService extends RemoteService {
    
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException;
	
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, CriteriaTransferObject cto, String[] additionalNonPersistentProperties) throws ServiceException;
    
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, JoinTable joinTable, CriteriaTransferObject cto, String[] additionalNonPersistentProperties) throws ServiceException;
    
    public DynamicResultSet create(String targetEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException;
    
    public Entity add(Entity entity, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException;
    
    public Entity update(Entity entity, ForeignKey[] foreignFields, String[] additionalNonPersistentProperties) throws ServiceException;
    
    public void remove(Entity entity, ForeignKey[] foreignFields, RemoveType removeType, String[] additionalNonPersistentProperties) throws ServiceException;
    
}
