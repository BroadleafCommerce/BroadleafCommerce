package org.broadleafcommerce.rules.service;


public class ParseToRuleFile {

	// This utility method will return the boolean expression symbols
	public static String booleanExpressionParser(String str) {
		String s;
		int i = Integer.valueOf(str);
		switch (i) {
		case 0:
			s = " == ";
			break;
		default:
			s = "";
		}
		return s;
	}

	public static String patternParser(String s) {
		String pattern;
		if (s.equals("CouponCode")) {
			pattern = "CouponCode(";
		} else {
			pattern = "";
		}
		return pattern;
	}

	public static String propertyParser(String s) {
		String property;
		if (s.equals("code")) {
			property = " code ";
		} else {
			property = "";
		}
		return property;
	}

}
