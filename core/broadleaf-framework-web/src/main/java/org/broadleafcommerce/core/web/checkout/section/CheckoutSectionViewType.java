/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.checkout.section;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class CheckoutSectionViewType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, CheckoutSectionViewType> TYPES = new LinkedHashMap<String, CheckoutSectionViewType>();

    public static final CheckoutSectionViewType ORDER_INFO = new CheckoutSectionViewType("ORDER_INFO", "Order Info (Contact Info) View");
    public static final CheckoutSectionViewType BILLING_INFO = new CheckoutSectionViewType("BILLING_INFO", "Billing Info View");
    public static final CheckoutSectionViewType SHIPPING_INFO = new CheckoutSectionViewType("SHIPPING_INFO", "Shipping Info View");
    public static final CheckoutSectionViewType PAYMENT_INFO = new CheckoutSectionViewType("PAYMENT_INFO", "Payment Info View");

    public static CheckoutSectionViewType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public CheckoutSectionViewType() {
        //do nothing
    }

    public CheckoutSectionViewType(final String type, final String friendlyType) {
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
        if (getClass() != obj.getClass())
            return false;
        CheckoutSectionViewType other = (CheckoutSectionViewType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}

