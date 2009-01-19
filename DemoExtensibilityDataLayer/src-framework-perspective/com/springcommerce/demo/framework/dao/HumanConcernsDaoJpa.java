package com.springcommerce.demo.framework.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.springcommerce.demo.framework.domain.AbstractPerson;
import com.springcommerce.demo.framework.domain.Person;

public class HumanConcernsDaoJpa implements HumanConcernsDao {

    @PersistenceContext
    protected EntityManager em;
    
	public AbstractPerson readPersonById(Long personId) {
        return em.find(Person.class, personId);
    }
    
    public final void savePerson(AbstractPerson person) {
    	em.merge(person);
    }
    
    public AbstractPerson createNewPerson() {
    	Person person = new Person();
    	person.setAge(37);
    	person.setName("Jeff Fischer");
    	
    	em.persist(person);
    	
    	return person;
    }

}
