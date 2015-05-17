/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.extensibility.jpa.copy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

/**
 * This provides a useful mechanism to pre-load/initialize classes that are required by a child class during class transformation, 
 * but that may not have been loaded or initialized by the JVM.
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class AbstractClassTransformer implements InitializingBean {

	protected static final Set<String> alreadyLoadedClasses = new HashSet<String>();
	protected List<String> preLoadClassNamePatterns = new ArrayList<String>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (preLoadClassNamePatterns != null && ! preLoadClassNamePatterns.isEmpty()) {
			synchronized (alreadyLoadedClasses) {
				for (String className : preLoadClassNamePatterns) {
					try {
						if (!alreadyLoadedClasses.contains(className)) {
							Class.forName(className);
							alreadyLoadedClasses.add(className);
						}
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("Unable to force load class with name " + className + " in the DirectCopyClassTransformer", e);
					}
				}
			}
		}
	}
	
	/**
	 * Fully qualified list of class names to pre-load
	 * 
	 * @param fullyQualifiedClassNames
	 */
	public void setPreLoadClassNamePatterns(List<String> fullyQualifiedClassNames) {
    	this.preLoadClassNamePatterns = fullyQualifiedClassNames;
    }
}
