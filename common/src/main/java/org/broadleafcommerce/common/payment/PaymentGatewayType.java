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

package org.broadleafcommerce.common.payment;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * <p>This is designed such that individual payment modules will extend this to add their own type. For instance, while
 * this class does not explicitly have a 'Braintree' payment gateway type, the Braintree module will provide an extension
 * to this class and add itself in the list of types. For instance:</p>
 * 
 * <pre>
 * {@code
 * public class BraintreeGatewayType extends PaymentGatewayType {
 *     public static final BRAINTREE = new PaymentGatewayType("BRAINTREE", "Braintree");
 * }
 * </pre>
 * 
 * And then in your application context:
 * <pre>
 * {@code
 * <bean class="org.broadleafcommerce.vendor.braintree.BraintreeGatewayType" />
 * }
 * </pre>
 * 
 * <p>This is especially useful in auditing scenarios so that, at a glance, you can easily see what gateway a particular
 * order payment was processed by.</p>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class PaymentGatewayType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentGatewayType> TYPES = new LinkedHashMap<String, PaymentGatewayType>();

    public static final PaymentGatewayType TEMPORARY  = new PaymentGatewayType("Temporary", "This is a temporary Order Payment");
    public static final PaymentGatewayType PASSTHROUGH  = new PaymentGatewayType("Passthrough", "Passthrough Payment");

    public static PaymentGatewayType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentGatewayType() {
        // do nothing
    }

    public PaymentGatewayType(String type, String friendlyType) {
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

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)){
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
        PaymentGatewayType other = (PaymentGatewayType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
