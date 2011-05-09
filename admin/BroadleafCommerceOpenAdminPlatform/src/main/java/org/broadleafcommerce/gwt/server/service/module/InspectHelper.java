package org.broadleafcommerce.gwt.server.service.module;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;

public interface InspectHelper {

	public ClassMetadata getMergedClassMetadata(final Class<?>[] entities, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties) throws ClassNotFoundException, IllegalArgumentException;
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
	
}
