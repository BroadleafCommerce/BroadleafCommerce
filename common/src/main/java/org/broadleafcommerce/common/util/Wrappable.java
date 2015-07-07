/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.util;

/**
 * Utility interface for items that wrap an internal, delegate item
 *
 * @author Jeff Fischer
 */
public interface Wrappable {

    /**
     * Can this wrapped item be unwrapped as the indicated type?
     *
     * @param unwrapType The type to check.
     * @return True/false.
     */
    public boolean isUnwrappableAs(Class unwrapType);

    /**
     * Get the wrapped delegate item
     *
     * @param unwrapType The java type as which to unwrap this instance.
     * @return The unwrapped reference
     */
    public <T> T unwrap(Class<T> unwrapType);

}
