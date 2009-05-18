package org.broadleafcommerce.pricing.dao;

import java.math.BigDecimal;

import org.broadleafcommerce.pricing.domain.ShippingRate;

public interface ShippingRateDao {

    ShippingRate save(ShippingRate shippingRate);

    ShippingRate readShippingRateById(Long id);

    ShippingRate readShippingRateByFeeTypesUnityQty(String feeType, String feeSubType, BigDecimal unitQuantity);

    //List<ShippingRate> readOrdersForCustomer(Long id);

}
