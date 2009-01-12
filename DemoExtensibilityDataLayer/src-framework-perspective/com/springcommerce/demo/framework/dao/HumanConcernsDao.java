package com.springcommerce.demo.framework.dao;

import com.springcommerce.demo.framework.domain.Person;

public interface HumanConcernsDao {

    public Person readPersonById(Long personId);
  
    public void savePerson(Person person);
    
}