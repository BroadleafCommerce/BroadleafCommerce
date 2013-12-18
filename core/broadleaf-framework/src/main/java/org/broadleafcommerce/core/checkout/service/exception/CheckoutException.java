/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.checkout.service.exception;

import org.broadleafcommerce.common.exception.BroadleafException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;

public class CheckoutException extends BroadleafException {

    private static final long serialVersionUID = 1L;
    
    private CheckoutResponse checkoutResponse;

    public CheckoutException() {
        super();
    }

    public CheckoutException(String message, CheckoutSeed seed) {
        super(message);
        checkoutResponse = seed;
    }

    public CheckoutException(Throwable cause, CheckoutSeed seed) {
        super(cause);
        checkoutResponse = seed;
    }

    public CheckoutException(String message, Throwable cause, CheckoutSeed seed) {
        super(message, cause);
        checkoutResponse = seed;
    }

    public CheckoutResponse getCheckoutResponse() {
        return checkoutResponse;
    }

    public void setCheckoutResponse(CheckoutResponse checkoutResponse) {
        this.checkoutResponse = checkoutResponse;
    }

}
