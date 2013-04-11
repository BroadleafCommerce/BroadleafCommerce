/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.inventory.domain;

import org.broadleafcommerce.core.inventory.service.type.AvailabilityStatusType;

import java.io.Serializable;
import java.util.Date;
/**
 * Implementations of this interface are used to hold data about SKU availability.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to how the
 * class is persisted.  If you just want to add additional fields then you should extend {@link SkuAvailabilityImpl}.
 *
 * @see {@link SkuAvailabilityImpl}
 * @author bpolster
 * 
 * @deprecated This is no longer required and is instead implemented as a third-party inventory module
 */
@Deprecated
public interface SkuAvailability extends Serializable {

    /**
     * Returns the id of this SkuAvailability
     */
    public Long getId();

    /**
     * Sets the id of this SkuAvailability record
     */
    public void setId(Long id);

    /**
     * Returns the id of this SKU associated with SkuAvailability record
     */
    public Long getSkuId();

    /**
     * Sets the id of this SKU
     */
    public void setSkuId(Long id);

    /**
     * Returns the USPSLocation id of this skuAvailability.   SKU availability records may or may not be location specific and
     * using null locations are a common implementation model.
     *
     */
    public Long getLocationId();

    /**
     * Sets the USPSLocation id of this skuAvailability.  SKU availability records may or may not be location specific and
     * using null locations are a common implementation model.
     */
    public void setLocationId(Long id);

    /**
     * Returns an implementation specific availability status.   This property can return null.
     */
    public AvailabilityStatusType getAvailabilityStatus();

    /**
     * Sets the availability status.
     */
    public void setAvailabilityStatus(AvailabilityStatusType status);

    /**
     * Returns the data the SKU will be available.
     * This property may return null which has an implementation specific meaning.
     */
    public Date getAvailabilityDate();

    /**
     * Sets the date the SKU will be available.  Setting to null is allowed and has an
     * implementation specific meaning.
     */
    public void setAvailabilityDate(Date availabilityDate);

    /**
     * Returns the number of this items that are currently in stock and available for sell.
     * Returning null has an implementation specific meaning.
     */
    public Integer getQuantityOnHand();

    /**
     * Sets the quantity on hand.  Setting to null is allowed and has an
     * implementation specific meaning.
     */
    public void setQuantityOnHand(Integer quantityOnHand);

    /**
     * Returns the reserve quantity.   Nulls will be treated the same as 0.
     * Implementations may want to manage a reserve quantity at each location so that the
     * available quantity for purchases is the quantityOnHand - reserveQuantity.
     */
    public Integer getReserveQuantity();

    /**
     * Sets the reserve quantity.
     * Implementations may want to manage a reserve quantity at each location so that the
     * available quantity for purchases is the quantityOnHand - reserveQuantity.
     */
    public void setReserveQuantity(Integer reserveQuantity);

    /**
     * Returns the getQuantityOnHand() - getReserveQuantity().
     * Preferred implementation is to return null if getQuantityOnHand() is null and to treat
     * a null in getReserveQuantity() as ZERO.
     */
    public Integer getAvailableQuantity();
}
