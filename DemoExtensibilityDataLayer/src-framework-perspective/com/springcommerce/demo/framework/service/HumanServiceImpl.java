package com.springcommerce.demo.framework.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springcommerce.demo.framework.dao.HumanConcernsDao;
import com.springcommerce.demo.framework.domain.AbstractPerson;
import com.springcommerce.demo.framework.processors.PersonProcessor;

@Service("humanService")
public class HumanServiceImpl implements HumanService {

	@Resource
    private HumanConcernsDao humanConcernsDao;
	@Resource(name="personProcessor")
    private PersonProcessor processor;
	
	public AbstractPerson readPersonById(Long personId) {
		return humanConcernsDao.readPersonById(personId);
	}
	
	@Transactional
	public AbstractPerson createNewPerson() {
		return humanConcernsDao.createNewPerson();
	}

	@Transactional
	public void updateAge(Long personId) {
		AbstractPerson person = readPersonById(personId);
		processor.alterPerson(person);
		humanConcernsDao.savePerson(person);
	}
	
}
