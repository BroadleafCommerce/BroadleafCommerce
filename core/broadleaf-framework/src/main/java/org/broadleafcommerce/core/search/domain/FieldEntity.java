/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An extensible enumeration of entities that are used for searching and reporting
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class FieldEntity implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, FieldEntity> TYPES = new LinkedHashMap<String, FieldEntity>();

    public static final FieldEntity PRODUCT = new FieldEntity("PRODUCT", "Product");
    public static final FieldEntity SKU = new FieldEntity("SKU", "Sku");
    public static final FieldEntity CUSTOMER = new FieldEntity("CUSTOMER", "Customer");
    public static final FieldEntity ORDER = new FieldEntity("ORDER", "Order");
    public static final FieldEntity ORDERITEM = new FieldEntity("ORDER_ITEM", "Order Item");
    public static final FieldEntity OFFER = new FieldEntity("OFFER", "Offer");
    public static final FieldEntity FULFILLMENT_ORDER = new FieldEntity("FULFILLMENT_ORDER", "Fulfillment Order");

    public static FieldEntity getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    protected List<String> additionalLookupTypes = new ArrayList<>();;

    public FieldEntity() {
        //do nothing
    }

    public FieldEntity(final String type, final String friendlyType) {
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
    
    public void addAditionalLookupType(String additionalLookupType) {
        if (additionalLookupTypes == null) {
            additionalLookupTypes = new ArrayList<>();
        }
        additionalLookupTypes.add(additionalLookupType);
    }
    
    public List<String> getAdditionalLookupTypes() {
        return Collections.unmodifiableList(additionalLookupTypes);
    }
    
    public List<String> getAllLookupTypes() {
        if (CollectionUtils.isNotEmpty(getAdditionalLookupTypes())) {
            List<String> result = new ArrayList<>(getAdditionalLookupTypes());
            result.add(getType());
            return Collections.unmodifiableList(result);
        } else {
            return Arrays.asList(getType());
        }
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
        FieldEntity other = (FieldEntity) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
