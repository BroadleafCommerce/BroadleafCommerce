package com.springcommerce.demo.framework.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.springcommerce.demo.framework.dao.HumanConcernsDao;
import com.springcommerce.demo.framework.domain.Person;

@Service("humanService")
public class HumanServiceImpl implements HumanService {

	@Resource
    private HumanConcernsDao humanConcernsDao;
	
	public Person readPersonById(Long personId) {
		return humanConcernsDao.readPersonById(personId);
	}

	public void savePerson(Person person) {
		humanConcernsDao.savePerson(person);
	}
}
