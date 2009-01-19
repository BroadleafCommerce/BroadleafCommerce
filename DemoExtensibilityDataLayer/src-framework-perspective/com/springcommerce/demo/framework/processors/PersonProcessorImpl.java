package com.springcommerce.demo.framework.processors;

import com.springcommerce.demo.framework.domain.AbstractPerson;

public class PersonProcessorImpl implements PersonProcessor {

	/* (non-Javadoc)
	 * @see com.springcommerce.demo.framework.processors.PersonProcessor#alterPerson(com.springcommerce.demo.framework.domain.Person)
	 */
	public void alterPerson(AbstractPerson person) {
		person.setAge(person.getAge() + 1);
	}
	
}
