package org.broadleafcommerce.gwt.server.changeset;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.event.AuditEventListener;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.event.Initializable;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;

public class ChangeSetListener implements PostLoadEventListener, Initializable {

	private static final long serialVersionUID = 1L;

	public void onPostLoad(PostLoadEvent event) {
		if (ChangeSetThreadLocal.getChangeSetDao() != null && ChangeSetThreadLocal.getChangeSet() != null && !Map.class.isAssignableFrom(event.getEntity().getClass())) {
			AuditReader reader = ChangeSetThreadLocal.getChangeSetDao().getAuditReader(event.getSession());
			@SuppressWarnings("unchecked")
			List<Number> revisions = (List<Number>) reader.createQuery()
		    .forRevisionsOfEntity(event.getEntity().getClass(), false, true)
		    .addProjection(AuditEntity.revisionNumber().max())
		    .add(AuditEntity.id().eq(event.getId()))
		    .add(AuditEntity.revisionProperty("changeset").eq(ChangeSetThreadLocal.getChangeSet()))
		    .getResultList();
			if (revisions.size() > 1 || (revisions.size() == 1 && revisions.get(0) != null)) {
				for (Number revision : revisions) {
					Serializable changeSetEntity = (Serializable) reader.createQuery()
				    .forEntitiesAtRevision(event.getEntity().getClass(), revision)
				    .getSingleResult();
					copyFieldState(changeSetEntity, event.getEntity());
				}
			}
		}
	}
	
	public void initialize(Configuration cfg) {
		AuditEventListener listener = new AuditEventListener();
		listener.initialize(cfg);
	}
	
	protected boolean isEntity(Object object) {
		return object.getClass().isAnnotationPresent(javax.persistence.Entity.class) || object.getClass().isAnnotationPresent(org.hibernate.annotations.Entity.class);
	}

	protected void copyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
		if (!src.getClass().isAssignableFrom(dest.getClass())) {
			throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src.getClass().getName() + "]");
		}
		org.springframework.util.ReflectionUtils.doWithFields(src.getClass(), new org.springframework.util.ReflectionUtils.FieldCallback() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					return;
				}
				org.springframework.util.ReflectionUtils.makeAccessible(field);
				final Object srcValue = field.get(src);
				final Object destValue = field.get(dest);
				if (srcValue != null && destValue != null) {
					if (srcValue.equals(destValue) && isEntity(srcValue)) {
						copyFieldState(srcValue, destValue);
						return;
					} else if (Collection.class.isAssignableFrom(srcValue.getClass())) {
						Object[] srcArray = ((Collection) srcValue).toArray();
						Object[] destArray = ((Collection) destValue).toArray();
						for (Object newSrcValue : srcArray) {
							int index = Arrays.binarySearch(destArray, newSrcValue, new Comparator() {
								public int compare(Object o1, Object o2) {
									if (o1.equals(o2)) {
										return 0;
									}
									return -1;
								}
							});
							if (index >= 0) {
								if (isEntity(newSrcValue)) {
									copyFieldState(newSrcValue, destArray[index]);
								} else {
									((Collection) destValue).remove(newSrcValue);
									((Collection) destValue).add(newSrcValue);
								}
							} else {
								((Collection) destValue).add(newSrcValue);
							}
						}
						return;
					} else if (Map.class.isAssignableFrom(srcValue.getClass())) {
						Map srcMap = (Map) srcValue;
						Map destMap = (Map) destValue;
						
						for (Object key : srcMap.keySet()) {
							if (destMap.containsKey(key)) {
								Object newSrcValue = srcMap.get(key);
								Object newDestValue = destMap.get(key);
								if (newSrcValue.equals(newDestValue)) {
									if (isEntity(newSrcValue)) {
										copyFieldState(newSrcValue, newDestValue);
									} else {
										destMap.remove(key);
										destMap.put(key, newDestValue);
									}
									continue;
								}
							}
							/*
							 * Either the destination map does not contain the key, or
							 * the values in both maps are not equal for the same key.
							 */
							destMap.put(key, destMap.get(key));
						}
						return;
					}
				}
				if(srcValue != null) {
					field.set(dest, srcValue);
				}
			}
		});
	}
}
