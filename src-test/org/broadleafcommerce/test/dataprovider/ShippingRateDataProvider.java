package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.pricing.domain.ShippingRateImpl;
import org.testng.annotations.DataProvider;

public class ShippingRateDataProvider {

    @DataProvider(name = "basicShippingRates")
    public static Object[][] provideBasicShippingRates(){
        ShippingRateImpl sr = new ShippingRateImpl();
        sr.setFeeType("SHIPPING");
        sr.setFeeSubType("ALL");
        sr.setFeeBand(1);
        sr.setBandUnitQuantity(BigDecimal.valueOf(29.99));
        sr.setBandResultQuantity(BigDecimal.valueOf(8.5));
        sr.setBandResultPercent(0);
        ShippingRateImpl sr2 = new ShippingRateImpl();
        sr2.setFeeType("SHIPPING");
        sr2.setFeeSubType("ALL");
        sr2.setFeeBand(2);
        sr2.setBandUnitQuantity(BigDecimal.valueOf(999999.99));
        sr2.setBandResultQuantity(BigDecimal.valueOf(8.5));
        sr2.setBandResultPercent(0);
        return new Object[][] {{sr, sr2}};
    }

}
