package org.broadleafcommerce.gwt.server;

import java.io.Serializable;

import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

public interface CustomPersistenceHandler {
	
	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname);
	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname);
	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname);
	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname);

	public CustomFetchResponse fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException;
	
	public Serializable add(Serializable instance, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException;
	
	public void remove(Serializable instance, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException;
	
	public Serializable update(Serializable instance, String[] customCriteria, DynamicEntityDao dynamicEntityDao) throws ServiceException;
	
}
