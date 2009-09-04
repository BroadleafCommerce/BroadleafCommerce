package org.broadleafcommerce.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Cache;

public class HydrationFieldScanner {
	
	@SuppressWarnings("unchecked")
	public String retrieveCacheRegion(Class clazz) {
		if (!clazz.isAnnotationPresent(Cache.class)) {
			throw new RuntimeException("Unable to final an @Cache annotation for this entity. Broadleaf Commerce Hydrated Cache requires the usage of the @Cache annotation.("+clazz.getName()+")");
		}
		return ((Cache) clazz.getAnnotation(Cache.class)).region();
	}
	
	@SuppressWarnings("unchecked")
	public Map<Field, Method[]> retrieveMutators(Class clazz, Class targetAnnotation) {
		Map<Field, Method[]> mutators = new HashMap<Field, Method[]>();
		/*
		 * TODO this doesn't work (reflection will only return the public fields). Will need to change
		 * to something like ASM for the scan.
		 */
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.getClass().isAssignableFrom(targetAnnotation)) {
					String fieldName = field.getName();
					fieldName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1, fieldName.length());
					Method getMethod = null;
					try {
						getMethod = clazz.getMethod("get"+fieldName, new Class[]{});
					} catch (Exception e) {
						//do nothing
					}
					if (getMethod == null) {
						try {
							getMethod = clazz.getMethod("is"+fieldName, new Class[]{});
						} catch (Exception e) {
							//do nothing
						}
					}
					if (getMethod == null) {
						try {
							getMethod = clazz.getMethod(field.getName(), new Class[]{});
						} catch (Exception e) {
							//do nothing
						}
					}
					Method setMethod = null;
					try {
						setMethod = clazz.getMethod("set"+fieldName, new Class[]{field.getClass()});
					} catch (Exception e) {
						//do nothing
					}
					if (getMethod == null || setMethod == null) {
						throw new RuntimeException("Unable to find a getter and setter method for the Hydrated field: " + field.getName());
					}
					mutators.put(field, new Method[]{getMethod, setMethod});
				}
			}
		}
		return mutators;
	}

}
