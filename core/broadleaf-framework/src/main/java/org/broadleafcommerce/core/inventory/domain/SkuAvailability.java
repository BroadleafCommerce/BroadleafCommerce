/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
 * @author bpolster
 * @see {@link SkuAvailabilityImpl}
 * @deprecated This is no longer required and is instead implemented as a third-party inventory module
 */
@Deprecated
public interface SkuAvailability extends Serializable {

    /**
     * Returns the id of this SkuAvailability
     */
    Long getId();

    /**
     * Sets the id of this SkuAvailability record
     */
    void setId(Long id);

    /**
     * Returns the id of this SKU associated with SkuAvailability record
     */
    Long getSkuId();

    /**
     * Sets the id of this SKU
     */
    void setSkuId(Long id);

    /**
     * Returns the USPSLocation id of this skuAvailability.   SKU availability records may or may not be location specific and
     * using null locations are a common implementation model.
     */
    Long getLocationId();

    /**
     * Sets the USPSLocation id of this skuAvailability.  SKU availability records may or may not be location specific and
     * using null locations are a common implementation model.
     */
    void setLocationId(Long id);

    /**
     * Returns an implementation specific availability status.   This property can return null.
     */
    AvailabilityStatusType getAvailabilityStatus();

    /**
     * Sets the availability status.
     */
    void setAvailabilityStatus(AvailabilityStatusType status);

    /**
     * Returns the data the SKU will be available.
     * This property may return null which has an implementation specific meaning.
     */
    Date getAvailabilityDate();

    /**
     * Sets the date the SKU will be available.  Setting to null is allowed and has an
     * implementation specific meaning.
     */
    void setAvailabilityDate(Date availabilityDate);

    /**
     * Returns the number of this items that are currently in stock and available for sell.
     * Returning null has an implementation specific meaning.
     */
    Integer getQuantityOnHand();

    /**
     * Sets the quantity on hand.  Setting to null is allowed and has an
     * implementation specific meaning.
     */
    void setQuantityOnHand(Integer quantityOnHand);

    /**
     * Returns the reserve quantity.   Nulls will be treated the same as 0.
     * Implementations may want to manage a reserve quantity at each location so that the
     * available quantity for purchases is the quantityOnHand - reserveQuantity.
     */
    Integer getReserveQuantity();

    /**
     * Sets the reserve quantity.
     * Implementations may want to manage a reserve quantity at each location so that the
     * available quantity for purchases is the quantityOnHand - reserveQuantity.
     */
    void setReserveQuantity(Integer reserveQuantity);

    /**
     * Returns the getQuantityOnHand() - getReserveQuantity().
     * Preferred implementation is to return null if getQuantityOnHand() is null and to treat
     * a null in getReserveQuantity() as ZERO.
     */
    Integer getAvailableQuantity();

}
