/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.money.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Currency;

/**
 * Unfortunately, JAXB doesn't know how to deal with java.util.Currency because it doesn't have a
 * default constructor.
 * 
 * Source via: http://weblogs.java.net/blog/kohsuke/archive/2005/09/using_jaxb_20s.html
 * 
 * @author phillipverheyden
 *
 */
public class CurrencyAdapter extends XmlAdapter<String, Currency> {

    @Override
    public String marshal(Currency currency) throws Exception {
        return currency.toString();
    }

    @Override
    public Currency unmarshal(String currencyString) throws Exception {
        return Currency.getInstance(currencyString);
    }

}
