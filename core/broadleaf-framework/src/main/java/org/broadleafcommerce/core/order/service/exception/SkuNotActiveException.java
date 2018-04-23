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
package org.broadleafcommerce.core.order.service.exception;

/**
 * This runtime exception will be thrown when an attempt to add to cart is made for
 * a specific sku that is not active.
 *
 * @author radhikari
 */
public class SkuNotActiveException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String ERROR_CODE = "SKU_NOT_ACTIVE";

       public SkuNotActiveException(String message) {
        super(message);

    }

    public SkuNotActiveException(String message, Throwable cause) {
        super(message, cause);

    }



}
