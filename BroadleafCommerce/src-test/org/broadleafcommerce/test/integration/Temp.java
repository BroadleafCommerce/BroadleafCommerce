package org.broadleafcommerce.test.integration;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Temp {

	public static void main(String[] items) {
		try {
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{ "/applicationContext-test.xml"});
		} catch (BeansException e) {
			e.printStackTrace();
		}
	}
}
