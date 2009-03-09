package org.broadleafcommerce.util;

import org.apache.commons.lang.StringUtils;

public class UrlUtil {
	public static String generateUrlKey(String toConvert) {
		//remove all non-word characters
		String result = toConvert.replaceAll("\\W","");
		//uncapitalizes the first letter of the url key
		return StringUtils.uncapitalize(result);
	}
}
