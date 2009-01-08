package com.springcommerce.demo.framework;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.springcommerce.demo.framework.service.HumanService;

public class Demo {

	public static void main(String[] items) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/applicationContext-framework.xml"});
		HumanService human = (HumanService) applicationContext.getBean("humanService");
		System.out.println(human.readPersonById(new Long(1)).getName());
	}
	
}
