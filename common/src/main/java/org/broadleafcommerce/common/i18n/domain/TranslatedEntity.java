/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
    public static final TranslatedEntity SITE = new TranslatedEntity("org.broadleafcommerce.common.site.domain.Site", "Site");
    public static final TranslatedEntity STATIC_ASSET = new TranslatedEntity("org.broadleafcommerce.cms.file.domain.StaticAsset", "StaticAsset");
    public static final TranslatedEntity SEARCH_FACET = new TranslatedEntity("org.broadleafcommerce.core.search.domain.SearchFacet", "SearchFacet");
    public static final TranslatedEntity FULFILLMENT_OPTION = new TranslatedEntity("org.broadleafcommerce.core.order.domain.FulfillmentOption", "FulfillmentOption");
    public static final TranslatedEntity OFFER = new TranslatedEntity("org.broadleafcommerce.core.offer.domain.Offer", "Offer");
    public static final TranslatedEntity CHALLENGE_QUESTION = new TranslatedEntity("org.broadleafcommerce.profile.core.domain.ChallengeQuestion", "ChallengeQuestion");
    public static final TranslatedEntity SKU_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.SkuAttribute", "SkuAttribute");
    public static final TranslatedEntity PRODUCT_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.ProductAttribute", "ProductAttribute");
    public static final TranslatedEntity CATEGORY_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.core.catalog.domain.CategoryAttribute", "CategoryAttribute");
    public static final TranslatedEntity CUSTOMER_ATTRIBUTE = new TranslatedEntity("org.broadleafcommerce.profile.core.domain.CustomerAttribute", "CustomerAttribute");
    public static final TranslatedEntity PAGE = new TranslatedEntity("org.broadleafcommerce.cms.page.domain.Page", "Page");
    public static final TranslatedEntity PAGE_TEMPLATE = new TranslatedEntity("org.broadleafcommerce.cms.page.domain.PageTemplate", "PageTemplate");
    public static final TranslatedEntity STRUCTURED_CONTENT_TYPE = new TranslatedEntity("org.broadleafcommerce.cms.structure.domain.StructuredContentType", "StructuredContentType");
    public static final TranslatedEntity COUNTRY = new TranslatedEntity("org.broadleafcommerce.profile.core.domain.Country", "Country");
    public static final TranslatedEntity COUNTRY_SUBDIVISION = new TranslatedEntity("org.broadleafcommerce.profile.core.domain.CountrySubdivision", "CountrySubdivision");
    public static final TranslatedEntity COUNTRY_SUBDIVISION_CATEGORY = new TranslatedEntity("org.broadleafcommerce.profile.core.domain.CountrySubdivisionCategory", "CountrySubdivisionCategory");
    public static final TranslatedEntity CATALOG = new TranslatedEntity("org.broadleafcommerce.common.site.domain.Catalog", "Catalog");
    public static final TranslatedEntity STRUCTURED_CONTENT = new TranslatedEntity("org.broadleafcommerce.cms.structure.domain.StructuredContent", "StructuredContent");
    public static final TranslatedEntity FIELD = new TranslatedEntity("org.broadleafcommerce.core.search.domain.Field", "Field");

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

    @Override
    public String getType() {
        return type;
    }

    @Override
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
