package com.springcommerce.demo.framework.service;

import com.springcommerce.demo.framework.domain.AbstractPerson;

public interface HumanService {

	public AbstractPerson readPersonById(Long personId);
	
	public void updateAge(Long personId);
	
	public AbstractPerson createNewPerson();
	
}
