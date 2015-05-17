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
package org.broadleafcommerce.common.i18n.domain;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An extensible enumeration of entities that have translatable fields. Any entity that wishes to have a translatable
 * field must register itself in this TYPES map.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class TranslatedEntity implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, TranslatedEntity> TYPES = new LinkedHashMap<String, TranslatedEntity>();

    public static final TranslatedEntity PRODUCT = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.Product", "Product");
    public static final TranslatedEntity SKU = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.Sku", "Sku");
    public static final TranslatedEntity CATEGORY = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.Category", "Category");
    public static final TranslatedEntity PRODUCT_OPTION = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.ProductOption", "ProdOption");
    public static final TranslatedEntity PRODUCT_OPTION_VALUE = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.ProductOptionValue", "ProdOptionVal");
    public static final TranslatedEntity STATIC_ASSET = new TranslatedEntity("org.broadleafcommerce.cms.file.domain.StaticAsset", "StaticAsset");
    public static final TranslatedEntity SEARCH_FACET = new TranslatedEntity("org.broadleafcommerce.core.search.domain.SearchFacet", "SearchFacet");
    public static final TranslatedEntity FULFILLMENT_OPTION = new TranslatedEntity("org.broadleafcommerce.core.order.domain.FulfillmentOption", "FulfillmentOption");
    public static final TranslatedEntity OFFER = new TranslatedEntity("org.broadleafcommerce.core.offer.domain.Offer", "Offer");
    public static final TranslatedEntity CHALLENGE_QUESTION = new TranslatedEntity("org.broadleafcommerce.profile.core.domain.ChallengeQuestion", "ChallengeQuestion");
    public static final TranslatedEntity SKU_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.SkuAttribute", "SkuAttribute");
    public static final TranslatedEntity PRODUCT_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.ProductAttribute", "ProductAttribute");
    public static final TranslatedEntity CATEGORY_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.CategoryAttribute", "CategoryAttribute");
    public static final TranslatedEntity CUSTOMER_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.profile.core.domain.CustomerAttribute", "CustomerAttribute");

    public static TranslatedEntity getInstance(final String type) {
        return TYPES.get(type);
    }
    
    public static TranslatedEntity getInstanceFromFriendlyType(final String friendlyType) {
        for (Entry<String, TranslatedEntity> entry : TYPES.entrySet()) {
            if (entry.getValue().getFriendlyType().equals(friendlyType)) {
                return entry.getValue();
            }
        }
        
        return null;
    }

    private String type;
    private String friendlyType;

    public TranslatedEntity() {
        //do nothing
    }

    public TranslatedEntity(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    public static Map<String, TranslatedEntity> getTypes() {
        return TYPES;
    }
    
    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
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
        TranslatedEntity other = (TranslatedEntity) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
