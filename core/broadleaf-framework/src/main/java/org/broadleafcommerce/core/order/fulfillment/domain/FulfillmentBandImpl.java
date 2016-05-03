/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

/**
 * 
 * @author Phillip Verheyden
 * @see {@link FulfillmentPriceBandImpl}, {@link FulfillmentWeightBandImpl}
 */
@MappedSuperclass
public abstract class FulfillmentBandImpl implements FulfillmentBand {

    private static final long serialVersionUID = 1L;

    @Column(name="RESULT_AMOUNT", precision=19, scale=5, nullable = false)
    protected BigDecimal resultAmount;
    
    @Column(name="RESULT_AMOUNT_TYPE", nullable = false)
    @AdminPresentation(friendlyName="Result Type", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType")
    protected String resultAmountType = FulfillmentBandResultAmountType.RATE.getType();

    @Override
    public BigDecimal getResultAmount() {
        return resultAmount;
    }

    @Override
    public void setResultAmount(BigDecimal resultAmount) {
        this.resultAmount = resultAmount;
    }

    @Override
    public FulfillmentBandResultAmountType getResultAmountType() {
        return FulfillmentBandResultAmountType.getInstance(resultAmountType);
    }

    @Override
    public void setResultAmountType(FulfillmentBandResultAmountType resultAmountType) {
        this.resultAmountType = resultAmountType.getType();
    }

}
