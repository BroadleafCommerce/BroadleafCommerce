package com.springcommerce.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.springcommerce.demo.framework.domain.Person;
import com.springcommerce.demo.framework.processors.PersonProcessor;
import com.springcommerce.demo.framework.service.HumanService;

public class DemoMerge {

	public static void main(String[] items) {
		/*
		 * TODO will need to merge the persistence-*.xml files, the *-orm.xml files and the applicationContext-*.xml files.
		 */
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/applicationContext-framework.xml", "/applicationContext-framework-processing.xml"});
		HumanService human = (HumanService) applicationContext.getBean("humanService");
		Person person = human.readPersonById(new Long(1));
		PersonProcessor processor = (PersonProcessor) applicationContext.getBean("personProcessor");
		processor.alterPerson(person);
		human.savePerson(person);
	}
	
}
