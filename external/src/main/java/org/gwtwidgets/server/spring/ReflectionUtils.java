/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtwidgets.server.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Utilities for internal use
 * 
 * @author Dmitri Shestakov, dvshestakov[at]gmail.com
 * @author Daniel Spangler
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class ReflectionUtils {

	/**
	 * Return array of all interfaces that are implemented by clazz and extend
	 * {@link RemoteService}.
	 * 
	 * @param clazz
	 * @return Array of interfaces. May be empty but never null.
	 */
	@SuppressWarnings("unchecked")
	public static Class<RemoteService>[] getExposedInterfaces(Class<?> clazz) {
		Set<Class<?>> interfaces = getInterfaces(clazz);
		for (Iterator<Class<?>> ite = interfaces.iterator(); ite.hasNext();) {
			Class<?> c = ite.next();
			if (!isExposed(c))
				ite.remove();
		}
		return interfaces.toArray(new Class[interfaces.size()]);
	}

	/**
	 * Adds elements of an array to a set. The JRE 1.5 does include a similar
	 * method in the Collections class, but that breaks GWT-SL 1.4
	 * compatibility.
	 * 
	 * @param set
	 * @param elements
	 */
	public static void addAll(Set<Class<?>> set, Class<?>[] elements) {
		for (Class<?> element : elements)
			set.add(element);
	}

	/**
	 * Return all interfaces that are implemented by this class, traversing
	 * super classes and super interfaces.
	 * 
	 * @param c
	 * @return Set of classes. May be empty but not null.
	 */
	public static Set<Class<?>> getInterfaces(Class<?> c) {
		Class<?> interfaces[] = c.getInterfaces();
		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (interfaces == null)
			return classes;
		addAll(classes, interfaces);
		for (Class<?> cl : interfaces) {
			classes.addAll(getInterfaces(cl));
		}
		Class<?> superClass = c.getSuperclass();
		if (superClass != null) {
			classes.addAll(getInterfaces(superClass));
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	private static boolean isExposed(Class c) {
		return RemoteService.class.isAssignableFrom(c);
	}

	/**
	 * Will try to find method in 'serviceInterfaces' and if found, will attempt
	 * to return a method with the same signature from 'service', otherwise an
	 * exception is thrown. If 'serviceInterfaces' is a zero-sized array, the
	 * interface check is omitted and the method is looked up directly on the
	 * object.
	 * 
	 * @param target
	 *            Object to search method on
	 * @param serviceInterfaces
	 *            The requested method must exist on at least one of the
	 *            interfaces
	 * @param method
	 * @return Method on 'service' or else a {@link NoSuchMethodException} is
	 *         thrown
	 */
	@SuppressWarnings("unchecked")
	public static Method getRPCMethod(Object target, Class[] serviceInterfaces, Method method) throws NoSuchMethodException {
		if (serviceInterfaces.length == 0)
			return target.getClass().getMethod(method.getName(), method.getParameterTypes());
		for (Class serviceInterface : serviceInterfaces)
			try {
				Method template = serviceInterface.getMethod(method.getName(), method.getParameterTypes());
				return target.getClass().getMethod(template.getName(), template.getParameterTypes());
			} catch (NoSuchMethodException e) {
			}
		throw new NoSuchMethodException(method.toString());
	}
	
	/*
	 * This is from the AnnotationUtils class of the Springframework (2.5+) in
	 * the core packag. This code is licensed under the Apache v2 license.
	 */
	public static <A extends Annotation> A findAnnotation(Class<?> clazz,
			Class<A> annotationType) {
		A annotation = clazz.getAnnotation(annotationType);
		if (annotation != null) {
			return annotation;
		}
		for (Class<?> ifc : clazz.getInterfaces()) {
			annotation = findAnnotation(ifc, annotationType);
			if (annotation != null) {
				return annotation;
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null || superClass == Object.class) {
			return null;
		}
		return findAnnotation(superClass, annotationType);
	}


}