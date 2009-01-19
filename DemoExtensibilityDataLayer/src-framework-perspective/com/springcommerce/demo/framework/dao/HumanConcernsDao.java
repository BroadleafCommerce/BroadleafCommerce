package com.springcommerce.demo.framework.dao;

import com.springcommerce.demo.framework.domain.AbstractPerson;

public interface HumanConcernsDao {

    public AbstractPerson readPersonById(Long personId);
  
    public void savePerson(AbstractPerson person);
    
    public AbstractPerson createNewPerson();
    
}