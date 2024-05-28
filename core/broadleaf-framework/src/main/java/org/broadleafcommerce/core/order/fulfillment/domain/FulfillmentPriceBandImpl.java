/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.fulfillment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_PRICE_BAND")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blFulfillmentOptionElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX)
})
public class FulfillmentPriceBandImpl extends FulfillmentBandImpl implements FulfillmentPriceBand {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentPriceBandId")
    @GenericGenerator(
            name = "FulfillmentPriceBandId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "FulfillmentPriceBandImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.core.order.fulfillment.domain.FulfillmentPriceBandImpl")
            }
    )
    @Column(name = "FULFILLMENT_PRICE_BAND_ID")
    protected Long id;

    @Column(name = "RETAIL_PRICE_MINIMUM_AMOUNT", precision = 19, scale = 5, nullable = false)
    protected BigDecimal retailPriceMinimumAmount;

    @ManyToOne(targetEntity = BandedPriceFulfillmentOptionImpl.class)
    @JoinColumn(name = "FULFILLMENT_OPTION_ID")
    protected BandedPriceFulfillmentOption option;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public BigDecimal getRetailPriceMinimumAmount() {
        return retailPriceMinimumAmount;
    }

    @Override
    public void setRetailPriceMinimumAmount(BigDecimal retailPriceMinimumAmount) {
        this.retailPriceMinimumAmount = retailPriceMinimumAmount;
    }

    @Override
    public BandedPriceFulfillmentOption getOption() {
        return option;
    }

    @Override
    public void setOption(BandedPriceFulfillmentOption option) {
        this.option = option;
    }

}
