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
package org.broadleafcommerce.vendor.service.exception;

import org.broadleafcommerce.vendor.service.message.ShippingPriceResponse;

public class ShippingPriceException extends Exception {

    private static final long serialVersionUID = 1L;

    protected ShippingPriceResponse shippingPriceResponse;

    public ShippingPriceException() {
        super();
    }

    public ShippingPriceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShippingPriceException(String message) {
        super(message);
    }

    public ShippingPriceException(Throwable cause) {
        super(cause);
    }

    public ShippingPriceResponse getShippingPriceResponse() {
        return shippingPriceResponse;
    }

    public void setShippingPriceResponse(ShippingPriceResponse shippingPriceResponse) {
        this.shippingPriceResponse = shippingPriceResponse;
    }
}
