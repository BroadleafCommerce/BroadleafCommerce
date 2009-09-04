package org.broadleafcommerce.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public interface HydratedAnnotationManager {

	public Map<Field, Method[]> getHydratedMutators(Object entity);
	
	public Method[] getIdMutators(Object entity);

	public String getCacheRegion(Object entity);
	
}