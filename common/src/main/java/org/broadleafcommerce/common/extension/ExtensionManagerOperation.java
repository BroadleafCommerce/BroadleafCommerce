/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
