package org.broadleafcommerce.gwt.server.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.broadleafcommerce.catalog.domain.CategoryImpl;

public class Test {

	public static void main(String[] items) {
		CategoryImpl parent1 = new CategoryImpl();
		parent1.setUrlKey("tester1");
		parent1.setId(1000L);

		CategoryImpl parent2 = new CategoryImpl();
		parent2.setId(2000L);

		CategoryImpl parent3 = new CategoryImpl();
		parent3.setId(3000L);

		CategoryImpl parent4 = new CategoryImpl();
		parent4.setId(4000L);
		
		CategoryImpl parent1_1 = new CategoryImpl();
		parent1_1.setId(1000L);
		parent1_1.setUrlKey("tester2");
		

		CategoryImpl cat1 = new CategoryImpl();
		cat1.setId(1L);
		cat1.setUrlKey("store");
		cat1.getAllParentCategories().add(parent1);
		cat1.getAllChildCategories().add(parent4);

		CategoryImpl cat2 = new CategoryImpl();
		cat2.setId(1L);
		cat2.setUrlKey("store_me");
		cat2.getAllParentCategories().add(parent1_1);
		cat2.getAllParentCategories().add(parent2);
		cat2.getAllParentCategories().add(parent3);

		copyFieldState(cat2, cat1);

		System.out.println("end");
	}
	
	public static boolean isEntity(Object object) {
		return object.getClass().isAnnotationPresent(javax.persistence.Entity.class) || object.getClass().isAnnotationPresent(org.hibernate.annotations.Entity.class);
	}

	public static void copyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
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
