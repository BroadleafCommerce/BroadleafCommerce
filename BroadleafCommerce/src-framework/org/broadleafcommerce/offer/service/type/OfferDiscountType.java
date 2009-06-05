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
package org.broadleafcommerce.offer.service.type;

import java.util.Hashtable;
import java.util.Map;

/**
 * An extendible enumeration of discount types.
 *
 */
public class OfferDiscountType {

    private static final Map<String, OfferDiscountType> types = new Hashtable<String, OfferDiscountType>();

    public static OfferDiscountType PERCENT_OFF = new OfferDiscountType("PERCENT_OFF");
    public static OfferDiscountType AMOUNT_OFF = new OfferDiscountType("AMOUNT_OFF");
    public static OfferDiscountType FIX_PRICE = new OfferDiscountType("FIX_PRICE");

    public static OfferDiscountType getInstance(String type) {
        return types.get(type);
    }

    private final String type;

    protected OfferDiscountType(String type) {
        this.type = type;
        types.put(type, this);
    }

    public String getType() {
        return type;
    }
}
