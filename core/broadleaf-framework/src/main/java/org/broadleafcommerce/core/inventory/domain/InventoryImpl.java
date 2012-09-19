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
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BLC_INVENTORY", uniqueConstraints = {@UniqueConstraint(columnNames = {"FULFILLMENT_LOCATION_ID", "SKU_ID"})})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blInventoryElements")
@Inheritance(strategy = InheritanceType.JOINED)
public class InventoryImpl implements Inventory {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "InventoryId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "InventoryId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "InventoryImpl", allocationSize = 50)
    @Column(name = "INVENTORY_ID")
    protected Long id;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = FulfillmentLocationImpl.class, optional=false)
    @JoinColumn(name = "FULFILLMENT_LOCATION_ID", nullable = false)
    protected FulfillmentLocation fulfillmentLocation;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = SkuImpl.class, optional=false)
    @JoinColumn(name = "SKU_ID", nullable = false)
    protected Sku sku;

    @Column(name = "QUANTITY_AVAILABLE", nullable = false)
    protected Integer quantityAvailable;

    @Column(name = "QUANTITY_ON_HAND", nullable = false)
    protected Integer quantityOnHand;

    @Column(name = "EXPECTED_AVAILABILITY_DATE")
    protected Date expectedAvailabilityDate;

    @Version
    @Column(name = "VERSION_NUM", nullable = false)
    protected Long version;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public FulfillmentLocation getFulfillmentLocation() {
        return fulfillmentLocation;
    }

    @Override
    public void setFulfillmentLocation(FulfillmentLocation fulfillmentLocation) {
        this.fulfillmentLocation = fulfillmentLocation;
    }

    @Override
    public Sku getSku() {
        return sku;
    }

    @Override
    public void setSku(Sku sku) {
        this.sku = sku;
    }

    @Override
    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    @Override
    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    @Override
    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    @Override
    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    @Override
    public Date getExpectedAvailabilityDate() {
        return expectedAvailabilityDate;
    }

    @Override
    public void setExpectedAvailabilityDate(Date expectedAvailabilityDate) {
        this.expectedAvailabilityDate = expectedAvailabilityDate;
    }

    @Override
    public Long getVersion() {
        return version;
    }

}
