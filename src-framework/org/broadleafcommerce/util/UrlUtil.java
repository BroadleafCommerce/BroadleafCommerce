package org.broadleafcommerce.util;

public class UrlUtil {
	public static String generateUrlKey(String toConvert) {
		StringBuffer newUrlKey = new StringBuffer();
		boolean firstChar = true;
		for (int i = 0; i < toConvert.length(); i++) {
			if (Character.isLetter(toConvert.charAt(i)))
				if (firstChar) {
					newUrlKey.append(toConvert.substring(i,i+1).toLowerCase());
					firstChar = false;
				} else {
					newUrlKey.append(toConvert.charAt(i));
				}
		}
		return newUrlKey.toString();
	}
}
