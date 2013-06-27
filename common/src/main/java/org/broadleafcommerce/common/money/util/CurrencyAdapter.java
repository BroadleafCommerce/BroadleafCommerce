/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
