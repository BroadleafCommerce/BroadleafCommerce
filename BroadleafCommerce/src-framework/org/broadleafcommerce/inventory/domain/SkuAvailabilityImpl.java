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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

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
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class SkuAvailabilityImpl implements SkuAvailability, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue
    @Column(name = "SKU_AVAILABILITY_ID")
    private Long id;

    /** The sale price. */
    @Column(name = "SKU_ID")
    private Long skuId;

    /** The retail price. */
    @Column(name = "LOCATION_ID")
    private Long locationId;

    /** The quantity on hand. */
    @Column(name = "QTY_ON_HAND")
    private Integer quantityOnHand;

    /** The reserve quantity. */
    @Column(name = "RESERVE_QTY")
    private Integer reserveQuantity;

    /** The description. */
    @Column(name = "AVAILABILITY_STATUS")
    private String availabilityStatus;

    /** The date this product will be available. */
    @Column(name = "AVAILABILITY_DATE")
    private Date availabilityDate;


	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Long getLocationId() {
		return locationId;
	}

	@Override
	public Integer getQuantityOnHand() {
		return quantityOnHand;
	}

	@Override
	public Long getSkuId() {
		return skuId;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@Override
	public void setQuantityOnHand(Integer qoh) {
		this.quantityOnHand = qoh;
	}

	@Override
	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	@Override
	public Date getAvailabilityDate() {
		return availabilityDate;
	}

	@Override
	public void setAvailabilityDate(Date availabilityDate) {
		this.availabilityDate = availabilityDate;
	}


	//=======================================================================>


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
}
