/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.pricing.service.exception;

/**
 * @author jfischer
 *
 */
public class TaxException extends Exception {

    /**
     * 
     */
    public TaxException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public TaxException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public TaxException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TaxException(Throwable cause) {
        super(cause);
    }

}
