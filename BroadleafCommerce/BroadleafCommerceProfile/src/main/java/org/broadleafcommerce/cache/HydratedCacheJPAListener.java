package org.broadleafcommerce.cache;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

public class HydratedCacheJPAListener {

	@PostLoad
	public void cacheGet(Object entity) {
		HydratedCacheManagerImpl manager = HydratedCacheManagerImpl.getInstance();
		Map<Field, Method[]> mutators = ((HydratedAnnotationManager) manager).getHydratedMutators(entity);
		if (mutators != null && mutators.size() > 0) {
			Method[] idMutators = ((HydratedAnnotationManager) manager).getIdMutators(entity);
			String cacheRegion = ((HydratedAnnotationManager) manager).getCacheRegion(entity);
			for (Field field : mutators.keySet()) {
				try {
					Serializable entityId = (Serializable) idMutators[0].invoke(entity, new Object[]{});
					Object hydratedItem = ((HydratedCacheManager) manager).getHydratedCacheElementItem(cacheRegion, entityId, field.getName());
					if (hydratedItem != null) {
						mutators.get(field)[1].invoke(entity, new Object[]{hydratedItem});
					}
				} catch (Exception e) {
					throw new RuntimeException("There was a problem while replacing a hydrated cache item - field("+field.getName()+") : entity("+entity.getClass().getName()+")", e);
				}
			}
		}
	}

	@PostPersist 
    @PostUpdate
	public void cacheSet(Object entity) {
		HydratedCacheManagerImpl manager = HydratedCacheManagerImpl.getInstance();
		Map<Field, Method[]> mutators = ((HydratedAnnotationManager) manager).getHydratedMutators(entity);
		if (mutators != null && mutators.size() > 0) {
			String cacheRegion = ((HydratedAnnotationManager) manager).getCacheRegion(entity);
			Method[] idMutators = ((HydratedAnnotationManager) manager).getIdMutators(entity);
			for (Field field : mutators.keySet()) {
				try {
					Object fieldVal = mutators.get(field)[0].invoke(entity, new Object[]{});
					Serializable entityId = (Serializable) idMutators[0].invoke(entity, new Object[]{});
					((HydratedCacheManager) manager).addHydratedCacheElementItem(cacheRegion, entityId, field.getName(), fieldVal);
				} catch (Exception e) {
					throw new RuntimeException("There was a problem while adding a hydrated cache item - field("+field.getName()+") : entity("+entity.getClass().getName()+")", e);
				}
			}
		}
	}
}
