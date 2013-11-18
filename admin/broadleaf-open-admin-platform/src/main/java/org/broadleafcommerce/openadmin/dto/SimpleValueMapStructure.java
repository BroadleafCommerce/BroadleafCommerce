/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.openadmin.dto.visitor.PersistencePerspectiveItemVisitor;

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
    public SimpleValueMapStructure(String keyClassName, String keyPropertyName, String keyPropertyFriendlyName, String valueClassName, String valuePropertyName, String valuePropertyFriendlyName, String mapProperty, String mapKeyValueProperty) {
        super(keyClassName, keyPropertyName, keyPropertyFriendlyName, valueClassName, mapProperty, false, mapKeyValueProperty);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleValueMapStructure)) return false;
        if (!super.equals(o)) return false;

        SimpleValueMapStructure that = (SimpleValueMapStructure) o;

        if (valuePropertyFriendlyName != null ? !valuePropertyFriendlyName.equals(that.valuePropertyFriendlyName) : that.valuePropertyFriendlyName != null)
            return false;
        if (valuePropertyName != null ? !valuePropertyName.equals(that.valuePropertyName) : that.valuePropertyName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (valuePropertyName != null ? valuePropertyName.hashCode() : 0);
        result = 31 * result + (valuePropertyFriendlyName != null ? valuePropertyFriendlyName.hashCode() : 0);
        return result;
    }
}
