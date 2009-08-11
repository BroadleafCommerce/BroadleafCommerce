/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.inventory.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.catalog.domain.Sku;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The Class SkuAvailabilityImpl is the default implementation of {@link SkuAvailability}.
 * <br>
 * <br>
 * This class is retrieved using the AvailabilityService.   The service allows availability to be
 * be location specific (e.g. for store specific inventory availability)
 * <br>
 * <br>
 * This implementation uses a Hibernate implementation of JPA configured through annotations.
 * The Entity references the following tables:
 * BLC_SKU_AVAILABILITY
 *
 * @see {@link Sku}
 * @author bpolster
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU_AVAILABILITY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SkuAvailabilityImpl implements SkuAvailability {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "SkuAvailabilityId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SkuAvailabilityId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SkuAvailabilityImpl", allocationSize = 50)
    @Column(name = "SKU_AVAILABILITY_ID")
    protected Long id;

    /** The sale price. */
    @Column(name = "SKU_ID")
    protected Long skuId;

    /** The retail price. */
    @Column(name = "LOCATION_ID")
    protected Long locationId;

    /** The quantity on hand. */
    @Column(name = "QTY_ON_HAND")
    protected Integer quantityOnHand;

    /** The reserve quantity. */
    @Column(name = "RESERVE_QTY")
    protected Integer reserveQuantity;

    /** The description. */
    @Column(name = "AVAILABILITY_STATUS")
    protected String availabilityStatus;

    /** The date this product will be available. */
    @Column(name = "AVAILABILITY_DATE")
    protected Date availabilityDate;

    public Long getId() {
        return id;
    }

    public Long getLocationId() {
        return locationId;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public void setQuantityOnHand(Integer qoh) {
        this.quantityOnHand = qoh;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Date getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(Date availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    /**
     * Returns an implementation specific availability status.   This property can return null.
     */
    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    /**
     * Sets the availability status.
     */
    public void setAvailabilityStatus(String status) {
        this.availabilityStatus = status;
    }

    /**
     * Returns the reserve quantity.   Nulls will be treated the same as 0.
     * Implementations may want to manage a reserve quantity at each location so that the
     * available quantity for purchases is the quantityOnHand - reserveQuantity.
     */
    public Integer getReserveQuantity() {
        return reserveQuantity;
    }

    /**
     * Sets the reserve quantity.
     * Implementations may want to manage a reserve quantity at each location so that the
     * available quantity for purchases is the quantityOnHand - reserveQuantity.
     */
    public void setReserveQuantity(Integer reserveQuantity) {
        this.reserveQuantity = reserveQuantity;
    }

    /**
     * Returns the getQuantityOnHand() - getReserveQuantity().
     * Preferred implementation is to return null if getQuantityOnHand() is null and to treat
     * a null in getReserveQuantity() as ZERO.
     */
    public Integer getAvailableQuantity() {
        if (getQuantityOnHand() == null || getReserveQuantity() == null) {
            return getQuantityOnHand();
        } else {
            return getQuantityOnHand() - getReserveQuantity();
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
        result = prime * result + ((skuId == null) ? 0 : skuId.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkuAvailabilityImpl other = (SkuAvailabilityImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (locationId == null) {
            if (other.locationId != null)
                return false;
        } else if (!locationId.equals(other.locationId))
            return false;
        if (skuId == null) {
            if (other.skuId != null)
                return false;
        } else if (!skuId.equals(other.skuId))
            return false;
        return true;
    }
}
