/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.openadmin.client.dto.visitor.PersistencePerspectiveItemVisitor;

/**
 * 
 * @author jfischer
 *
 */
public class SimpleValueMapStructure extends MapStructure {

	private static final long serialVersionUID = 1L;
	
	private String valuePropertyName;
	private String valuePropertyFriendlyName;
	
	public SimpleValueMapStructure() {
		super();
	}
	
	/**
	 * @param keyClassName
	 * @param keyPropertyName
	 * @param keyPropertyFriendlyName
	 * @param valueClassName
	 * @param mapProperty
	 */
	public SimpleValueMapStructure(String keyClassName, String keyPropertyName, String keyPropertyFriendlyName, String valueClassName, String valuePropertyName, String valuePropertyFriendlyName, String mapProperty) {
		super(keyClassName, keyPropertyName, keyPropertyFriendlyName, valueClassName, mapProperty, false);
		this.valuePropertyFriendlyName = valuePropertyFriendlyName;
		this.valuePropertyName = valuePropertyName;
	}

	public String getValuePropertyName() {
		return valuePropertyName;
	}
	
	public void setValuePropertyName(String valuePropertyName) {
		this.valuePropertyName = valuePropertyName;
	}
	
	public String getValuePropertyFriendlyName() {
		return valuePropertyFriendlyName;
	}
	
	public void setValuePropertyFriendlyName(String valuePropertyFriendlyName) {
		this.valuePropertyFriendlyName = valuePropertyFriendlyName;
	}
	
	public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public PersistencePerspectiveItem clonePersistencePerspectiveItem() {
        SimpleValueMapStructure mapStructure = new SimpleValueMapStructure();
        mapStructure.setKeyClassName(getKeyClassName());
        mapStructure.setKeyPropertyName(getKeyPropertyName());
        mapStructure.setValuePropertyFriendlyName(getKeyPropertyFriendlyName());
        mapStructure.setValueClassName(getValueClassName());
        mapStructure.setMapProperty(getMapProperty());
        mapStructure.setDeleteValueEntity(getDeleteValueEntity());
        mapStructure.valuePropertyName = valuePropertyName;
        mapStructure.valuePropertyFriendlyName = valuePropertyFriendlyName;

        return mapStructure;
    }
}
