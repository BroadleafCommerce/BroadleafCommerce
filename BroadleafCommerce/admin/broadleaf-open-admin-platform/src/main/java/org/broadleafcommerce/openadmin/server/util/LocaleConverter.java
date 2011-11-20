package org.broadleafcommerce.openadmin.server.util;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;

public class LocaleConverter implements Converter<String, Locale> {

	@Override
	public Locale convert(String localeString) {
		if (localeString == null) {
    		return null;
    	}
    	String[] components = localeString.split("_");
    	if (components.length == 1) {
    		return new Locale(components[0]);
    	} else if (components.length == 2) {
    		return new Locale(components[0], components[1]);
    	} else if (components.length == 3) {
    		return new Locale(components[0], components[1], components[2]);
    	}
		return null;
	}

}
