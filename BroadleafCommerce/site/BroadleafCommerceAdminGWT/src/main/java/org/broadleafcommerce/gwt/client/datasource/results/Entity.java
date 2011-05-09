package org.broadleafcommerce.gwt.client.datasource.results;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] type;
	private Property[] properties;
	
	public String[] getType() {
		return type;
	}

	public void setType(String[] type) {
		Arrays.sort(type);
		this.type = type;
	}

	public Property[] getProperties() {
		return properties;
	}
	
	public void setProperties(Property[] properties) {
		this.properties = properties;
	}
	
	public void mergeProperties(String prefix, Entity entity) {
		int j = 0;
		Property[] merged = new Property[properties.length + entity.getProperties().length];
		for (Property property : properties) {
			merged[j] = property;
			j++;
		}
		for (Property property : entity.getProperties()) {
			property.setName(prefix + "." + property.getName());
			merged[j] = property;
			j++;
		}
		properties = merged;
	}
	
	public Property findProperty(String name) {
		Arrays.sort(properties, new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		Property searchProperty = new Property();
		searchProperty.setName(name);
		int index = Arrays.binarySearch(properties, searchProperty, new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		if (index >= 0) {
			return properties[index];
		}
		return null;
	}
}
