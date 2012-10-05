/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.reflection;

import com.google.gwt.core.client.GWT;

import java.util.HashMap;

/**
 * 
 * @author jfischer
 *
 */
public class ModuleFactory extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;

	private static ModuleFactory viewList = null;

	public static ModuleFactory getInstance() {
		if (viewList == null) {
			ModuleFactory.viewList = new ModuleFactory();
		}
		return ModuleFactory.viewList;
	}
	
	private Factory factory = GWT.create(ReflectiveFactory.class);
	
	private ModuleFactory() {
		//do nothing
	}
	
	public String put(String key, String fullyQualifiedClassName) {
		return super.put(key, fullyQualifiedClassName);
	}

    public Object newInstance(String name) {
		if (!containsKey(name)) {
			throw new RuntimeException(name + " is not a member of the view factory's library. A key and a fully qualified view class name must be added before createItem is called.");
		}
		Object response = factory.newInstance(get(name));
		if (response == null) {
			throw new RuntimeException("Unable to instantiate the item from the Factory using classname: (" + get(name) + "). Are you sure this classname is correct?");
		}
		return response;
	}
	
	public void createAsync(String name, AsyncClient entityPresenterClient) {
		if (!containsKey(name)) {
			throw new RuntimeException(name + " is not a member of the view factory's library. A key and a fully qualified view class name must be added before createItem is called.");
		}
		factory.createAsync(get(name), entityPresenterClient);
		/*if (response == null) {
			throw new RuntimeException("Unable to instantiate the item from the Factory using classname: (" + get(name) + "). Are you sure this classname is correct?");
		}
		return response;*/
	}
}
