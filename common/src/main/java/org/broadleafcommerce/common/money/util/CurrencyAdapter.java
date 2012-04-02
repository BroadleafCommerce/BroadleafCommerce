package org.broadleafcommerce.common.money.util;

import java.util.Currency;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
