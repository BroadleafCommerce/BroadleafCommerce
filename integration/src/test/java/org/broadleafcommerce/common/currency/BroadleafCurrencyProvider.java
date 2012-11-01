package org.broadleafcommerce.common.currency;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.testng.annotations.DataProvider;


public class BroadleafCurrencyProvider {
  
    @DataProvider(name = "USCurrency")
    public static Object[][] provideUSCurrency() {
        BroadleafCurrency currency=new BroadleafCurrencyImpl();
        currency.setCurrencyCode("USD");
        currency.setDefaultFlag(true);
        currency.setFriendlyName("US Dollar");
        
        return new Object[][] { { currency } };
    }
    @DataProvider(name = "FRCurrency")
    public static Object[][] provideFRCurrency() {
        BroadleafCurrency currency=new BroadleafCurrencyImpl();
        currency.setCurrencyCode("EUR");
        currency.setDefaultFlag(true);
        currency.setFriendlyName("EURO Dollar");
        return new Object[][] { { currency } };
    }
}
