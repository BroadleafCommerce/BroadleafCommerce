/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.openadmin.web.controller.modal;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of modal header types.
 * 
 * @author ckittrell
 */
public class ModalHeaderType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, ModalHeaderType> TYPES = new LinkedHashMap<String, ModalHeaderType>();

    public static final ModalHeaderType VIEW_ENTITY  = new ModalHeaderType("viewEntity", "viewEntity");
    public static final ModalHeaderType ADD_ENTITY  = new ModalHeaderType("addEntity", "addEntity");
    public static final ModalHeaderType ADD_COLLECTION_ITEM  = new ModalHeaderType("addCollectionItem", "addCollectionItem");
    public static final ModalHeaderType SELECT_COLLECTION_ITEM  = new ModalHeaderType("selectCollectionItem", "selectCollectionItem");
    public static final ModalHeaderType UPDATE_COLLECTION_ITEM  = new ModalHeaderType("updateCollectionItem", "updateCollectionItem");
    public static final ModalHeaderType VIEW_COLLECTION_ITEM  = new ModalHeaderType("viewCollectionItem", "viewCollectionItem");
    public static final ModalHeaderType TRANSLATION  = new ModalHeaderType("translation", "translation");
    public static final ModalHeaderType ADD_TRANSLATION  = new ModalHeaderType("addTranslation", "addTranslation");
    public static final ModalHeaderType UPDATE_TRANSLATION  = new ModalHeaderType("updateTranslation", "updateTranslation");
    public static final ModalHeaderType SELECT_ASSET  = new ModalHeaderType("selectAsset", "selectAsset");
    public static final ModalHeaderType CUSTOM  = new ModalHeaderType("custom", "custom");


    public static ModalHeaderType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public ModalHeaderType() {
        //do nothing
    }

    public ModalHeaderType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        } else {
            throw new RuntimeException("Cannot add the type: (" + type + "). It already exists as a type via " + getInstance(type).getClass().getName());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        ModalHeaderType other = (ModalHeaderType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
