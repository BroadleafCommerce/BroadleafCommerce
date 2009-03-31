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
import javax.persistence.Transient;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.inventory.service.AvailabilityStatusEnum;
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
 *
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

    /** The name. */
    @Column(name = "QTY_ON_HAND")
    private Long quantityOnHand;

    /** The description. */
    @Column(name = "AVAILABILITY_STATUS")
    private String availabilityStatus;

    @Transient
    private Date availabilityDate;

	@Override
	public AvailabilityStatusEnum getAvailabilityStatus() {
		if (availabilityStatus != null) {
			return AvailabilityStatusEnum.valueOf(availabilityStatus);
		} else {
			return null;
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Long getLocationId() {
		return locationId;
	}

	@Override
	public Long getQuantityOnHand() {
		return quantityOnHand;
	}

	@Override
	public Long getSkuId() {
		return skuId;
	}

	@Override
	public void setAvailabilityStatus(AvailabilityStatusEnum status) {
		if (status != null) {
			availabilityStatus = status.toString();
		}

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
	public void setQuantityOnHand(Long qoh) {
		this.quantityOnHand = qoh;
	}

	@Override
	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Date getAvailabilityDate() {
		if (availabilityDate == null && getAvailabilityStatus() != null) {
			if (getAvailabilityStatus().isAvailable()) {
				return new Date();
			}
		}
		return availabilityDate;
	}

	public void setAvailabilityDate(Date availabilityDate) {
		this.availabilityDate = availabilityDate;
	}
}
