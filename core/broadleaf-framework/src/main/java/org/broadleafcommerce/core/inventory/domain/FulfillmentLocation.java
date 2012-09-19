/**
 * Copyright 2012 the original author or authors.
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
 **/
package org.broadleafcommerce.core.inventory.domain;

import org.broadleafcommerce.profile.core.domain.Address;

import java.io.Serializable;

public interface FulfillmentLocation extends Serializable {

    /**
     * @return the id of the fulfillment location
     */
    public Long getId();

    /**
     * @param id the id of the fulfillment location
     */
    public void setId(Long id);

    /**
     * Get the @link Address of the fulfillment location
     * @return the @link Address of the fulfillment location
     */
    public Address getAddress();

    /**
     * Set the @link Address of the fulfillment location
     * @param address
     */
    public void setAddress(Address address);

    /**
     * @return a boolean value of whether or not a customer can pick up inventory at the fulfillment location
     */
    public Boolean getPickupLocation();

    /**
     * Sets whether or not a customer can pick up inventory at the fulfillment location
     * @param pickupLocation
     */
    public void setPickupLocation(Boolean pickupLocation);

    /**
     * @return a boolean value of whether this fulfillment location can ship
     */
    public Boolean getShippingLocation();

    /**
     * Sets whether or not this fulfillment location can ship
     * @param shippingLocation
     */
    public void setShippingLocation(Boolean shippingLocation);

    /**
     * Retrieves if this fulfillment location as default
     * @return
     */
    public Boolean getDefaultLocation();

    /**
     * Sets whether or not this fulfillment location is the default one
     * @param defaultLocation
     */
    public void setDefaultLocation(Boolean defaultLocation);

}
