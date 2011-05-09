package org.broadleafcommerce.gwt.server.service.handler;

import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.service.module.InspectHelper;
import org.broadleafcommerce.gwt.server.service.module.RecordHelper;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

public interface CustomPersistenceHandler {
	
	public Boolean canHandleInspect(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname, String[] customCriteria);
	
	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException;

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
	public Entity add(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
	
}
