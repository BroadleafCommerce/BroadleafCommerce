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
package org.broadleafcommerce.core.order.fulfillment.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.core.order.domain.FulfillmentOptionImpl;

import java.io.Serial;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_OPTION_FIXED")
@AdminPresentationClass(friendlyName = "Fixed Price Fulfillment")
public class FixedPriceFulfillmentOptionImpl extends FulfillmentOptionImpl implements FixedPriceFulfillmentOption {

    @Serial
    private static final long serialVersionUID = 2L;

    @Column(name = "PRICE", precision = 19, scale = 5, nullable = false)
    @AdminPresentation(friendlyName = "FixedPriceFulfillmentOptionImpl_price",
            order = Presentation.FieldOrder.DESCRIPTION + 1000)
    protected BigDecimal price;

    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(excluded = true)
    protected BroadleafCurrency currency;

    @Override
    public Money getPrice() {
        return price == null ? null : BroadleafCurrencyUtils.getMoney(price, getCurrency());
    }

    @Override
    public void setPrice(Money price) {
        this.price = Money.toAmount(price);
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(BroadleafCurrency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !getClass().isAssignableFrom(o.getClass())) {
            return false;
        }

        final FixedPriceFulfillmentOptionImpl that = (FixedPriceFulfillmentOptionImpl) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getPrice(), that.getPrice())
                .append(getCurrency(), that.getCurrency())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getPrice())
                .append(getCurrency())
                .toHashCode();
    }

    @Override
    public CreateResponse<FixedPriceFulfillmentOption> createOrRetrieveCopyInstance(
            MultiTenantCopyContext context
    ) throws CloneNotSupportedException {
        CreateResponse<FixedPriceFulfillmentOption> createResponse = super.createOrRetrieveCopyInstance(context);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        FixedPriceFulfillmentOption myClone = createResponse.getClone();
        myClone.setPrice(getPrice());
        myClone.setCurrency(currency);

        return createResponse;
    }

}
