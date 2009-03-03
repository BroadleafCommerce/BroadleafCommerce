package org.broadleafcommerce.util;

public class UrlUtil {
	public static String generateUrlKey(String toConvert) {
		StringBuffer newUrlKey = new StringBuffer();
		for (int i = 0; i < toConvert.length(); i++) {
			if (Character.isLetter(toConvert.charAt(i)))
				newUrlKey.append(toConvert.charAt(i));
		}
		return newUrlKey.toString();
	}
}
