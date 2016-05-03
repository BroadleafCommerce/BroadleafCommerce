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
package org.broadleafcommerce.common.extension;

/**
 * Responsible for exercising a method on an {@link ExtensionHandler}. This provides a vehicle for exercising an ExtensionHandler
 * API via direct call, rather than reflection. This could be important for performance if a high volume of calls are being
 * made. See {@link ExtensionManager#execute(ExtensionManagerOperation, Object...)}.
 *
 * @author Jeff Fischer
 */
public interface ExtensionManagerOperation {

    /**
     * Call a method on the handler using some params. This generally involves casting to the proper types. For example:
     * </p>
     * <pre>
     * {@code
     *  public static final ExtensionManagerOperation applyAdditionalFilters = new ExtensionManagerOperation() {
     *        @Override
     *        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
     *            return ((OfferServiceExtensionHandler) handler).applyAdditionalFilters((List<Offer>) params[0], (Order) params[1]);
     *        }
     *  };
     * }
     * </pre>
     * @param handler
     * @param params
     * @return the result
     */
    ExtensionResultStatusType execute(ExtensionHandler handler, Object... params);

}
