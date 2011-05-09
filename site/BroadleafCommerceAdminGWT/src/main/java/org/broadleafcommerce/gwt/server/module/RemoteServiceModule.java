package org.broadleafcommerce.gwt.server.module;

import java.util.List;
import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.CustomPersistenceHandler;
import org.broadleafcommerce.gwt.server.DynamicEntityRemoteService;
import org.broadleafcommerce.gwt.server.dao.FieldMetadata;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

public interface RemoteServiceModule {

	public boolean isCompatible(OperationType operationType);
	
	public Entity add(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public void updateMergedProperties(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException;
	
	public void setCustomPersistenceHandlers(List<CustomPersistenceHandler> customPersistenceHandlers);
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException;
	
	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, PersistencePerspective persistencePerspective, String[] customCriteria) throws ServiceException;
	
	public void setDynamicEntityRemoteService(DynamicEntityRemoteService dynamicEntityRemoteService);
	
}
