package org.broadleafcommerce.gwt.client.reflection;

import java.util.HashMap;


import com.google.gwt.core.client.GWT;

public class ModuleFactory extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;

	private static ModuleFactory viewList = null;

	public static ModuleFactory getInstance() {
		if (viewList == null) {
			ModuleFactory.viewList = new ModuleFactory();
		}
		return ModuleFactory.viewList;
	}
	
	private Factory factory = (Factory) GWT.create(ReflectiveFactory.class);
	
	private ModuleFactory() {
		//do nothing
	}
	
	public String put(String key, String fullyQualifiedClassName) {
		return super.put(key, fullyQualifiedClassName);
	}
	
	public Instantiable createItem(String name) {
		if (!containsKey(name)) {
			throw new RuntimeException(name + " is not a member of the view factory's library. A key and a fully qualified view class name must be added before createItem is called.");
		}
		return factory.newInstance(get(name));
	}
}
