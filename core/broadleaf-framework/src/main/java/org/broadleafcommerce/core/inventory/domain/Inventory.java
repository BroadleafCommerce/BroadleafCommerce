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
 */
package org.broadleafcommerce.core.inventory.domain;

import org.broadleafcommerce.core.catalog.domain.Sku;

import java.io.Serializable;
import java.util.Date;

public interface Inventory extends Serializable {

    /**
     * Retrieves the unique identifier of the Inventory
     * @return id
     */
    public Long getId();

    /**
     * Sets the unique identifier of the Inventory
     * @param id
     */
    public void setId(Long id);

    /**
     * Retrieves the fulfillment location information related to this inventory
     * @return FulfillmentLocation
     */
    public FulfillmentLocation getFulfillmentLocation();

    /**
     * Sets the fulfillment location information related to this inventory
     * @param fulfillmentLocation
     */
    public void setFulfillmentLocation(FulfillmentLocation fulfillmentLocation);

    /**
     * Retrieves the sku for this Inventory
     * @return sku
     */
    public Sku getSku();

    /**
     * Sets the sku for this Inventory
     * @param sku
     */
    public void setSku(Sku sku);

    /**
     * Retrieves the actual inventory in possession of the business
     * @return quantity
     */
    public Integer getQuantityOnHand();

    /**
     * Sets the actual inventory in possession of the business
     * @param quantity
     */
    public void setQuantityOnHand(Integer quantity);

    /**
     * Retrieves the inventory available for sale.
     * This is typically the difference of quantity on hand reduced by the inventory allocated for existing orders.
     * @return quantityAvailable
     */
    public Integer getQuantityAvailable();

    /**
     * Sets the inventory available for sale.
     * @param quantity
     */
    public void setQuantityAvailable(Integer quantity);

    /**
     * Retrieves the expected availability date
     * @return Date
     */
    public Date getExpectedAvailabilityDate();

    /**
     * Sets the expected availability date
     * @param expectedAvailabilityDate
     */
    public void setExpectedAvailabilityDate(Date expectedAvailabilityDate);

    /**
     * Retrieves the version set by Hibernate. Version has a getter only.
     * @return
     */
    public Long getVersion();

}
