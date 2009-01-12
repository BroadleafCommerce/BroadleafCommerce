package com.springcommerce.demo.framework.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.springcommerce.demo.framework.domain.Person;


@Repository("HumanConcernsDao")
public class HumanConcernsDaoJpa implements HumanConcernsDao {

    @PersistenceContext
    private EntityManager em;

    public Person readPersonById(Long personId) {
        return em.find(Person.class, personId);
    }
    
    public void savePerson(Person person) {
    	em.persist(person);
    }

}
