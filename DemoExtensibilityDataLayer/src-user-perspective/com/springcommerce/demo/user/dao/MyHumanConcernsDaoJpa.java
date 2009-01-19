package com.springcommerce.demo.user.dao;

import com.springcommerce.demo.framework.dao.HumanConcernsDaoJpa;
import com.springcommerce.demo.framework.domain.AbstractPerson;
import com.springcommerce.demo.user.domain.SpecializedPerson;

public class MyHumanConcernsDaoJpa extends HumanConcernsDaoJpa {

	/* (non-Javadoc)
	 * @see com.springcommerce.demo.framework.dao.HumanConcernsDaoJpa#createNewPerson()
	 */
	@Override
	public AbstractPerson createNewPerson() {
		SpecializedPerson person = new SpecializedPerson();
		person.setAge(37);
		person.setAgeFactor(2);
		person.setName("Jeff Fischer");
		
		em.persist(person);
		
		return person;
	}

	/* (non-Javadoc)
	 * @see com.springcommerce.demo.framework.dao.HumanConcernsDaoJpa#readPersonById(java.lang.Long)
	 */
	@Override
	public AbstractPerson readPersonById(Long personId) {
		return em.find(SpecializedPerson.class, personId);
	}

}
