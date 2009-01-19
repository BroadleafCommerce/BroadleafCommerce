package com.springcommerce.demo.user.processors;

import com.springcommerce.demo.framework.domain.AbstractPerson;
import com.springcommerce.demo.framework.processors.PersonProcessor;
import com.springcommerce.demo.user.domain.SpecializedPerson;

public class SpecializedPersonProcessorImpl implements PersonProcessor {

	/* (non-Javadoc)
	 * @see com.springcommerce.demo.framework.processors.PersonProcessor#alterPerson(com.springcommerce.demo.framework.domain.Person)
	 */
	public void alterPerson(AbstractPerson person) {
		SpecializedPerson sp = (SpecializedPerson) person;
		sp.setAge(sp.getAge() * sp.getAgeFactor());
	}

}
