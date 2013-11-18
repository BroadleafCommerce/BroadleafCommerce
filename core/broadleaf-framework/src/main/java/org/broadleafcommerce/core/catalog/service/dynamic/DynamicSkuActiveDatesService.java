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
package org.broadleafcommerce.core.catalog.service.dynamic;

import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.Date;

import javax.annotation.Nonnull;

/**
 * <p>Interface for dynamically determining the activity dates.</p>
 * 
 * Provides an ability to set active dates programatically.   Intended for use by add-on modules like 
 * the PriceList module which supports activeDates dates by PriceList.   
 * 
 * Even if the dates are being overridden dynamically, the master activeStart and activeEnd dates still
 * control the global activeDates of a SKU.    
 * 
 * <p>Rather than implementing this interface directly, consider sub-classing the {@link DefaultDynamicSkuActiveDatesServiceImpl}
 * and providing overrides to methods there.</p>
 * 
 * @author bpolster
 *
 */
public interface DynamicSkuActiveDatesService {

    /**
     * Returns the activeStartDate for the SKU if it has been overridden.
     * 
     * @param sku
     * @return
     */
    @Nonnull
    @SuppressWarnings("rawtypes")
    public Date getDynamicSkuActiveStartDate(Sku sku);

    /**
     * Returns the activeEndDate for the SKU if it has been overridden.
     * 
     * @param sku
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Date getDynamicSkuActiveEndDate(Sku sku);
}
