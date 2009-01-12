package com.springcommerce.demo.framework.service;

import com.springcommerce.demo.framework.domain.Person;

public interface HumanService {

	public Person readPersonById(Long personId);
	
	public void savePerson(Person person);
	
}
