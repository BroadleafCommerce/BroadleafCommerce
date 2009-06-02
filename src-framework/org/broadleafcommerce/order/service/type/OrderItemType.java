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
package org.broadleafcommerce.order.service.type;

/**
 * Rather than create an Java enum type here, it is better to set up a more
 * "manual" enumeration class that can be extended. As a result, implementors
 * may add additional values in an extension that the framework can still
 * use.
 * 
 * @author jfischer
 */
public class OrderItemType {

    public static OrderItemType DISCRETE  = new OrderItemType("org.broadleafcommerce.order.domain.DiscreteOrderItem");
    public static OrderItemType BUNDLE = new OrderItemType("org.broadleafcommerce.order.domain.BundleOrderItem");
    public static OrderItemType GIFTWRAP = new OrderItemType("org.broadleafcommerce.order.domain.GiftWrapOrderItem");

    private final String className;

    protected OrderItemType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

}
