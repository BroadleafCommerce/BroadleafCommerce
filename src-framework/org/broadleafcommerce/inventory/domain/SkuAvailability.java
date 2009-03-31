package org.broadleafcommerce.inventory.domain;

import java.util.Date;

import org.broadleafcommerce.inventory.service.AvailabilityStatusEnum;
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
 */
public interface SkuAvailability {

    /**
     * Returns the id of this SkuAvailability
     */
	public Long getId();

    /**
     * Returns the id of this sku associated with SkuAvailability record
     */
    public Long getSkuId();

    /**
     * Sets the id of this SkuAvailability record
     */
	public void setId(Long id);

    /**
     * Sets the id of this sku
     */
    public void setSkuId(Long id);

    /**
     * Returns the Location id of this skuAvailability
     */
    public Long getLocationId();

    /**
     * Sets the Location id of this skuAvailability
     */
    public void setLocationId(Long id);

    /*
     * Returns the quantity on hand for this SKU
     */
    public Long getQuantityOnHand();

    /*
     * Sets the quantity on hand for this SKU
     */
    public void setQuantityOnHand(Long qoh);

    /*
     * Returns the availability status
     */
    public AvailabilityStatusEnum getAvailabilityStatus();

    /*
     * Sets the availability status
     */
    public void setAvailabilityStatus(AvailabilityStatusEnum status);

    /*
     * Returns the data the sku will be available.   Returns today when the value is null and the AvailabilityStatusEnum.isAvailable() returns true
     */
	public Date getAvailabilityDate();

	/*
	 * Sets the date the sku will be available.
	 */
	public void setAvailabilityDate(Date availabilityDate);
}
