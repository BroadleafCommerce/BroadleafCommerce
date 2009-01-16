package com.springcommerce.demo.framework.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springcommerce.demo.framework.dao.HumanConcernsDao;
import com.springcommerce.demo.framework.domain.Person;
import com.springcommerce.demo.framework.processors.PersonProcessor;

@Service("humanService")
public class HumanServiceImpl implements HumanService {

	//@Resource
    private HumanConcernsDao humanConcernsDao;
	//@Resource(name="personProcessor")
    private PersonProcessor processor;
	
	public Person readPersonById(Long personId) {
		return humanConcernsDao.readPersonById(personId);
	}

	@Transactional
	public void updateAge(Long personId) {
		Person person = readPersonById(personId);
		processor.alterPerson(person);
		humanConcernsDao.savePerson(person);
	}
	
}
